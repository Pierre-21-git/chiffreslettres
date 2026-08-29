package fr.pierre.chiffreslettres.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.BaremeLettres
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.ui.defi.DUREE_SECONDES_DEFI_MOTS_MAX
import fr.pierre.chiffreslettres.ui.defi.seuilLongueurDefiLettres
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Sous-mode du jeu "duel mots" (retour utilisateur) : Duo = le plus de mots en 5 minutes, chacun
 * à l'aveugle jusqu'à la fin ; Confrontation = course au premier à [DuelMotsReseauViewModel.objectifMots]
 * mots, colonnes live ; Points = même mécanique que Confrontation, mais course à un total de
 * points (barème de lettres) au lieu d'un nombre de mots — pas de niveau, alphabet complet,
 * aucune longueur minimale de mot (retour utilisateur, 2026-08-28).
 */
enum class SousModeDuelMots { DUO, CONFRONTATION, POINTS }

/** Raison du rejet d'un mot en sous-mode Confrontation (retour utilisateur : jamais une perte, juste signalé, cf. `DefiMotsMaxViewModel`). */
enum class RaisonRejetMotDuelMots { INVALIDE, TROP_COURT, DEJA_PRIS_MOI, DEJA_PRIS_ADVERSAIRE }

/** Raison de fin de partie en sous-mode Confrontation (retour utilisateur : affichée à l'écran). */
enum class RaisonFinConfrontation { OBJECTIF_ATTEINT, TEMPS_ECOULE, TOUS_MOTS_TROUVES }

private fun SousModeDuelMots.versTypePartie(): TypePartie = when (this) {
    SousModeDuelMots.DUO -> TypePartie.DUEL_MOTS_RESEAU
    SousModeDuelMots.CONFRONTATION -> TypePartie.DUEL_MOTS_CONFRONTATION_RESEAU
    SousModeDuelMots.POINTS -> TypePartie.DUEL_MOTS_POINTS_RESEAU
}

/**
 * Objectifs de points proposés pour le sous-mode Points (retour utilisateur, 2026-08-28) : choix
 * libre, purement pour régler la durée de la partie — pas dérivé d'un niveau, contrairement à
 * [DuelMotsReseauViewModel.objectifMots] en sous-mode Confrontation.
 */
val OBJECTIFS_POINTS_DUEL = listOf(25, 50, 75, 100)

/**
 * Nombre de voyelles fixe pour le tirage partagé du duel mots (retour utilisateur : pas de choix
 * manuel comme en partie duo/réseau classique — le duel mots n'a pas d'écran "premier joueur qui
 * choisit", les deux démarrent avec le même tirage dès la configuration reçue).
 */
const val NOMBRE_VOYELLES_DUEL_MOTS = 3

/**
 * ViewModel du jeu "duel mots" (retour utilisateur, recherche de mots à 2, 100 % réseau — voir le
 * plan du 2026-08-10). Reprend le handshake de connexion (host/invité, WiFi/Bluetooth, types
 * [EtatPartieReseau]/[RoleReseau]/[TransportReseau]/[CibleDecouverte]) de [PartieReseauViewModel]
 * à l'identique, dupliqué plutôt que factorisé pour ne pas risquer de régression sur la partie
 * réseau existante (pas de téléphone sous la main pour vérifier un refactor de connexion
 * partagée entre les deux).
 *
 * Sous-mode Duo : chaque joueur joue son propre round côté Compose (réutilise
 * `DefiMotsMaxViewModel` avec `enregistrerResultat = false`, puisque c'est cette classe qui
 * enregistre la session), à l'aveugle ; les deux échangent leur liste finale via
 * [envoyerResultatDuo] une fois leurs 5 minutes écoulées.
 *
 * Sous-mode Confrontation : pas de round Compose séparé, l'état de jeu (tirage, saisie) vit
 * directement ici pour diffuser chaque mot validé en temps réel ([validerMotConfrontation]).
 */
class DuelMotsReseauViewModel(
    context: Context,
    private val pseudo: String,
    private val avatar: String,
    private val dictionnaire: DictionnaireIndex,
    private val configurationAlphabet: ConfigurationAlphabetLettres,
    private val historiqueRepository: HistoriqueRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
) : ViewModel() {

    private val hoteReseau = HoteReseau(context.applicationContext)
    private val inviteReseau = InviteReseau(context.applicationContext)
    private val hoteBluetooth = HoteBluetooth(context.applicationContext)
    private val inviteBluetooth = InviteBluetooth(context.applicationContext)

    private val _etat = MutableStateFlow<EtatPartieReseau>(EtatPartieReseau.ChoixRole)
    val etat: StateFlow<EtatPartieReseau> = _etat.asStateFlow()

    private val _partiesTrouvees = MutableStateFlow<List<CibleDecouverte>>(emptyList())
    val partiesTrouvees: StateFlow<List<CibleDecouverte>> = _partiesTrouvees.asStateFlow()

    private val _erreurJeu = MutableStateFlow<String?>(null)
    val erreurJeu: StateFlow<String?> = _erreurJeu.asStateFlow()

    private var connexionActive: ConnexionSocket? = null
    private var jobRole: Job? = null

    // --- Configuration de la partie ---
    var role: RoleReseau = RoleReseau.HOTE
        private set
    var sousMode: SousModeDuelMots = SousModeDuelMots.DUO
        private set
    var niveau: NiveauLettres = NiveauLettres.EMILE
        private set
    var objectifMots: Int = 0
        private set
    /** Sous-mode Points uniquement (retour utilisateur) : option "atteindre exactement l'objectif". */
    var atteindreExactement: Boolean = false
        private set

    private val _tirageTermine = MutableStateFlow(false)
    val tirageTermine: StateFlow<Boolean> = _tirageTermine.asStateFlow()

    /** Graine partagée du tirage unique — sert de clé au round Compose en sous-mode Duo. */
    private val _seed = MutableStateFlow<Long?>(null)
    val seed: StateFlow<Long?> = _seed.asStateFlow()

    // --- Sous-mode Confrontation : état de jeu direct (pas de round Compose séparé) ---
    private val _lettresTirees = MutableStateFlow<List<Char>>(emptyList())
    val lettresTirees: StateFlow<List<Char>> = _lettresTirees.asStateFlow()
    private val _indicesUtilises = MutableStateFlow<List<Int>>(emptyList())
    val indicesUtilises: StateFlow<List<Int>> = _indicesUtilises.asStateFlow()
    private val _motSaisi = MutableStateFlow("")
    val motSaisi: StateFlow<String> = _motSaisi.asStateFlow()
    private val _motRejete = MutableStateFlow<String?>(null)
    val motRejete: StateFlow<String?> = _motRejete.asStateFlow()
    private val _raisonRejet = MutableStateFlow<RaisonRejetMotDuelMots?>(null)
    val raisonRejet: StateFlow<RaisonRejetMotDuelMots?> = _raisonRejet.asStateFlow()
    private val _tempsRestantSecondes = MutableStateFlow(DUREE_SECONDES_DEFI_MOTS_MAX)
    val tempsRestantSecondes: StateFlow<Int> = _tempsRestantSecondes.asStateFlow()
    private val _motsPossiblesConfrontation = MutableStateFlow<List<String>>(emptyList())
    val motsPossiblesConfrontation: StateFlow<List<String>> = _motsPossiblesConfrontation.asStateFlow()
    private val _raisonFinConfrontation = MutableStateFlow<RaisonFinConfrontation?>(null)
    val raisonFinConfrontation: StateFlow<RaisonFinConfrontation?> = _raisonFinConfrontation.asStateFlow()
    private var timerJobConfrontation: Job? = null

    // --- Résultats (les deux sous-modes) ---
    private val _motsTrouvesMoi = MutableStateFlow<List<String>>(emptyList())
    val motsTrouvesMoi: StateFlow<List<String>> = _motsTrouvesMoi.asStateFlow()
    private val _motsTrouvesAdversaire = MutableStateFlow<List<String>>(emptyList())
    val motsTrouvesAdversaire: StateFlow<List<String>> = _motsTrouvesAdversaire.asStateFlow()

    /** Confrontation uniquement : true = j'ai gagné, false = l'adversaire a gagné, null = en cours. */
    private val _gagnant = MutableStateFlow<Boolean?>(null)
    val gagnant: StateFlow<Boolean?> = _gagnant.asStateFlow()

    /** Duo uniquement : le résultat final de l'adversaire (5 min écoulées de son côté) est-il déjà reçu ? */
    private val _resultatAdversaireDuoRecu = MutableStateFlow(false)

    private val _tropheesDebloques = MutableStateFlow<List<Trophee>>(emptyList())
    /** Trophées fraîchement débloqués à la fin de cette partie (retour utilisateur : écran dédié). */
    val tropheesDebloques: StateFlow<List<Trophee>> = _tropheesDebloques.asStateFlow()

    fun effacerTropheesDebloques() {
        _tropheesDebloques.value = emptyList()
    }
    val resultatAdversaireDuoRecu: StateFlow<Boolean> = _resultatAdversaireDuoRecu.asStateFlow()

    /** Duo uniquement : mon résultat final (5 min écoulées) déjà envoyé à l'adversaire ? */
    private var monResultatDuoEnvoye = false

    private var enregistre = false

    // --- Connexion (identique à PartieReseauViewModel) ---

    fun choisirHote(transport: TransportReseau) {
        jobRole?.cancel()
        val flux = when (transport) {
            TransportReseau.WIFI -> hoteReseau.demarrer(pseudo, avatar)
            TransportReseau.BLUETOOTH -> hoteBluetooth.demarrer(pseudo, avatar)
        }
        jobRole = viewModelScope.launch {
            flux.collect { etatHote ->
                when (etatHote) {
                    is EtatHote.Preparation -> _etat.value = EtatPartieReseau.Preparation
                    is EtatHote.EnAttente -> _etat.value = EtatPartieReseau.AttenteHote(etatHote.nomServiceAffiche)
                    is EtatHote.ClientConnecte -> attendreHandshake(etatHote.connexion, RoleReseau.HOTE)
                    is EtatHote.Erreur -> _etat.value = EtatPartieReseau.Erreur(etatHote.message)
                }
            }
        }
    }

    fun choisirInvite(transport: TransportReseau) {
        jobRole?.cancel()
        _etat.value = EtatPartieReseau.RechercheInvite
        _partiesTrouvees.value = emptyList()
        val flux = when (transport) {
            TransportReseau.WIFI -> inviteReseau.rechercherParties().map { liste -> liste.map { CibleDecouverte.Wifi(it) } }
            TransportReseau.BLUETOOTH ->
                inviteBluetooth.rechercherAppareils().map { liste -> liste.map { CibleDecouverte.Bluetooth(it) } }
        }
        jobRole = viewModelScope.launch {
            flux.collect { _partiesTrouvees.value = it }
        }
    }

    fun rejoindre(cible: CibleDecouverte) {
        _etat.value = EtatPartieReseau.ConnexionEnCours(cible)
        jobRole?.cancel()
        viewModelScope.launch {
            try {
                val connexion = when (cible) {
                    is CibleDecouverte.Wifi -> inviteReseau.rejoindre(cible.partie, pseudo, avatar)
                    is CibleDecouverte.Bluetooth -> inviteBluetooth.rejoindre(cible.partie, pseudo, avatar)
                }
                attendreHandshake(connexion, RoleReseau.INVITE)
            } catch (e: Exception) {
                _etat.value = EtatPartieReseau.Erreur("Connexion impossible : ${e.message}")
            }
        }
    }

    private suspend fun attendreHandshake(connexion: ConnexionSocket, role: RoleReseau) {
        connexionActive = connexion
        val bonjourDistant = withTimeoutOrNull(TIMEOUT_HANDSHAKE_MS) {
            connexion.messagesRecus.filterIsInstance<MessageReseau.Bonjour>().first()
        }
        if (bonjourDistant == null) {
            _etat.value = EtatPartieReseau.Erreur("Le pair n'a pas répondu à temps.")
            connexion.fermer()
            connexionActive = null
            return
        }
        this.role = role
        _etat.value = EtatPartieReseau.Connecte(bonjourDistant.profil, role)
        demarrerEcouteJeu(connexion)
    }

    private fun demarrerEcouteJeu(connexion: ConnexionSocket) {
        viewModelScope.launch {
            connexion.messagesRecus.collect { message ->
                when (message) {
                    is MessageReseau.ConfigurationDuelMots -> {
                        sousMode = SousModeDuelMots.valueOf(message.sousMode)
                        niveau = NiveauLettres.valueOf(message.niveauCode)
                        objectifMots = message.objectifMots ?: 0
                        atteindreExactement = message.atteindreExactement
                        demarrerTirage(message.seed)
                    }
                    is MessageReseau.MotTrouve -> recevoirMotAdversaire(message.mot)
                    is MessageReseau.MotRetire -> _motsTrouvesAdversaire.update { it - message.mot }
                    is MessageReseau.ResultatDuelMotsDuo -> recevoirResultatDuoAdversaire(message.motsTrouves)
                    is MessageReseau.FinDuelMots -> terminerConfrontation(
                        raison = RaisonFinConfrontation.valueOf(message.raison),
                        gagnantEstExpediteur = message.gagnantEstExpediteur,
                    )
                    else -> {}
                }
            }
        }
        viewModelScope.launch {
            connexion.estOuverte.collect { ouverte ->
                if (!ouverte && _seed.value != null && !enregistre) {
                    _erreurJeu.value = "Connexion perdue avec l'adversaire."
                }
            }
        }
    }

    /** Hôte uniquement : choisit le sous-mode/niveau (et l'objectif en Confrontation/Points) et démarre pour les deux côtés. */
    fun demarrerCommeHote(
        sousMode: SousModeDuelMots,
        niveau: NiveauLettres,
        objectifMots: Int?,
        atteindreExactement: Boolean = false,
    ) {
        this.sousMode = sousMode
        this.niveau = niveau
        this.objectifMots = objectifMots ?: 0
        this.atteindreExactement = atteindreExactement
        val graine = Random.nextLong()
        viewModelScope.launch {
            connexionActive?.envoyer(
                MessageReseau.ConfigurationDuelMots(sousMode.name, niveau.name, graine, objectifMots, atteindreExactement),
            )
        }
        demarrerTirage(graine)
    }

    private fun demarrerTirage(graine: Long) {
        _seed.value = graine
        // Réinitialisation systématique (retour utilisateur : sans effet sur la 1ère partie, déjà
        // à ces valeurs par défaut — indispensable pour que "Rejouer" reparte sur un état propre).
        _motsTrouvesMoi.value = emptyList()
        _motsTrouvesAdversaire.value = emptyList()
        enregistre = false
        if (sousMode == SousModeDuelMots.CONFRONTATION || sousMode == SousModeDuelMots.POINTS) {
            timerJobConfrontation?.cancel()
            val sac = SacLettres.creer(
                configurationAlphabet.distributionBase,
                configurationAlphabet.voyelles,
                configurationAlphabet.lettresExcluesParNiveau.getValue(niveau),
            )
            _lettresTirees.value = TirageLettres.tirer(sac, NOMBRE_VOYELLES_DUEL_MOTS, TirageLettres.NOMBRE_LETTRES, Random(graine))
            _motsPossiblesConfrontation.value = dictionnaire.rechercherAuMoins(_lettresTirees.value, seuilRequisMot())
                .distinct()
                .sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))
            _indicesUtilises.value = emptyList()
            _motSaisi.value = ""
            _motRejete.value = null
            _raisonRejet.value = null
            _gagnant.value = null
            _raisonFinConfrontation.value = null
            _tempsRestantSecondes.value = DUREE_SECONDES_DEFI_MOTS_MAX
            demarrerChronoConfrontation()
        } else {
            monResultatDuoEnvoye = false
            _resultatAdversaireDuoRecu.value = false
        }
        _tirageTermine.value = true
    }

    private fun demarrerChronoConfrontation() {
        timerJobConfrontation = viewModelScope.launch {
            while (_tempsRestantSecondes.value > 0) {
                delay(1000)
                if (_gagnant.value != null) return@launch
                _tempsRestantSecondes.update { it - 1 }
            }
            terminerLocalementConfrontation(RaisonFinConfrontation.TEMPS_ECOULE)
        }
    }

    // --- Sous-mode Confrontation / Points ---

    /** Sous-mode Points : pas de longueur minimale (retour utilisateur) ; sinon, seuil du niveau. */
    private fun seuilRequisMot(): Int = if (sousMode == SousModeDuelMots.POINTS) 1 else seuilLongueurDefiLettres(niveau)

    private fun scoreTotal(mots: List<String>): Int = mots.sumOf { BaremeLettres.scoreMot(it, configurationAlphabet.baremeLettres) }

    /** Mon score ou mon nombre de mots trouvés selon le sous-mode — sert de base de comparaison partout où Confrontation/Points divergent. */
    private fun monTotal(): Int = if (sousMode == SousModeDuelMots.POINTS) scoreTotal(_motsTrouvesMoi.value) else _motsTrouvesMoi.value.size

    private fun totalAdversaire(): Int =
        if (sousMode == SousModeDuelMots.POINTS) scoreTotal(_motsTrouvesAdversaire.value) else _motsTrouvesAdversaire.value.size

    fun cliquerLettreConfrontation(index: Int) {
        if (_gagnant.value != null) return
        val indices = _indicesUtilises.value
        if (index !in _lettresTirees.value.indices || index in indices) return
        mettreAJourMotConfrontation(indices + index)
    }

    fun annulerLettreConfrontation() {
        if (_gagnant.value != null) return
        mettreAJourMotConfrontation(_indicesUtilises.value.dropLast(1))
    }

    fun effacerMotConfrontation() {
        if (_gagnant.value != null) return
        mettreAJourMotConfrontation(emptyList())
    }

    private fun mettreAJourMotConfrontation(indices: List<Int>) {
        _indicesUtilises.value = indices
        _motSaisi.value = indices.map { _lettresTirees.value[it] }.joinToString("")
        _motRejete.value = null
        _raisonRejet.value = null
    }

    /**
     * Valide le mot en cours (retour utilisateur, sous-mode Confrontation) : un mot invalide,
     * trop court, déjà trouvé par moi ou par l'adversaire est signalé sans jamais faire perdre,
     * comme le défi mots (cf. `DefiMotsMaxViewModel`). Un nouveau mot est diffusé aussitôt à
     * l'adversaire pour qu'il ne puisse plus le proposer à son tour.
     */
    fun validerMotConfrontation() {
        if (_gagnant.value != null) return
        val mot = _motSaisi.value
        if (mot.isBlank()) return
        val seuil = seuilRequisMot()
        val raison = when {
            !dictionnaire.estJouable(mot) -> RaisonRejetMotDuelMots.INVALIDE
            mot.length < seuil -> RaisonRejetMotDuelMots.TROP_COURT
            mot in _motsTrouvesMoi.value -> RaisonRejetMotDuelMots.DEJA_PRIS_MOI
            mot in _motsTrouvesAdversaire.value -> RaisonRejetMotDuelMots.DEJA_PRIS_ADVERSAIRE
            else -> null
        }
        if (raison != null) {
            _indicesUtilises.value = emptyList()
            _motSaisi.value = ""
            _motRejete.value = mot
            _raisonRejet.value = raison
            return
        }
        _motsTrouvesMoi.update { it + mot }
        _indicesUtilises.value = emptyList()
        _motSaisi.value = ""
        _motRejete.value = null
        _raisonRejet.value = null
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.MotTrouve(mot, mot.length)) }
        val objectifAtteint = when (sousMode) {
            SousModeDuelMots.POINTS -> if (atteindreExactement) monTotal() == objectifMots else monTotal() >= objectifMots
            else -> monTotal() >= objectifMots
        }
        if (objectifAtteint) {
            timerJobConfrontation?.cancel()
            _raisonFinConfrontation.value = RaisonFinConfrontation.OBJECTIF_ATTEINT
            _gagnant.value = true
            viewModelScope.launch {
                connexionActive?.envoyer(
                    MessageReseau.FinDuelMots(gagnantEstExpediteur = true, raison = RaisonFinConfrontation.OBJECTIF_ATTEINT.name),
                )
            }
            enregistrerSessionSiNecessaire()
        } else {
            verifierTousMotsTrouvesConfrontation()
        }
    }

    /**
     * Sous-mode Points, option "atteindre exactement l'objectif" uniquement (retour utilisateur) :
     * retire un mot déjà trouvé de ma propre liste pour corriger un dépassement — il redevient
     * disponible pour les deux joueurs, comme s'il n'avait jamais été trouvé.
     */
    fun retirerMotConfrontation(mot: String) {
        if (_gagnant.value != null) return
        if (sousMode != SousModeDuelMots.POINTS || !atteindreExactement) return
        if (mot !in _motsTrouvesMoi.value) return
        _motsTrouvesMoi.update { it - mot }
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.MotRetire(mot)) }
    }

    private fun recevoirMotAdversaire(mot: String) {
        if (_gagnant.value != null) return
        _motsTrouvesAdversaire.update { it + mot }
        verifierTousMotsTrouvesConfrontation()
    }

    /**
     * Fin de partie détectée localement (temps écoulé ou tous les mots possibles trouvés) : à la
     * différence de l'objectif atteint (le premier arrivé gagne forcément), ce cas peut être une
     * vraie égalité — chaque côté calcule donc son propre vainqueur à partir de ses mots trouvés
     * déjà synchronisés en direct (`MotTrouve`), plutôt que d'inverser le booléen reçu du réseau
     * (qui afficherait à tort "perdu" chez l'un des deux en cas d'égalité, cf. règle habituelle
     * "égalité comptée comme gagnée pour les deux", [enregistrerSessionSiNecessaire]).
     */
    private fun terminerLocalementConfrontation(raison: RaisonFinConfrontation) {
        if (_gagnant.value != null) return
        timerJobConfrontation?.cancel()
        val jaiGagne = monTotal() >= totalAdversaire()
        _raisonFinConfrontation.value = raison
        _gagnant.value = jaiGagne
        viewModelScope.launch {
            connexionActive?.envoyer(MessageReseau.FinDuelMots(gagnantEstExpediteur = jaiGagne, raison = raison.name))
        }
        enregistrerSessionSiNecessaire()
    }

    private fun verifierTousMotsTrouvesConfrontation() {
        if (_gagnant.value != null) return
        val possibles = _motsPossiblesConfrontation.value
        if (possibles.isEmpty()) return
        if ((_motsTrouvesMoi.value + _motsTrouvesAdversaire.value).toSet().containsAll(possibles)) {
            terminerLocalementConfrontation(RaisonFinConfrontation.TOUS_MOTS_TROUVES)
        }
    }

    private fun terminerConfrontation(raison: RaisonFinConfrontation, gagnantEstExpediteur: Boolean) {
        if (_gagnant.value != null) return
        timerJobConfrontation?.cancel()
        _raisonFinConfrontation.value = raison
        _gagnant.value = when (raison) {
            RaisonFinConfrontation.OBJECTIF_ATTEINT -> !gagnantEstExpediteur
            RaisonFinConfrontation.TEMPS_ECOULE, RaisonFinConfrontation.TOUS_MOTS_TROUVES -> monTotal() >= totalAdversaire()
        }
        enregistrerSessionSiNecessaire()
    }

    // --- Sous-mode Duo ---

    /** Appelé côté Compose une fois le round `DefiMotsMaxViewModel` local terminé (5 min écoulées ou arrêt volontaire). */
    fun envoyerResultatDuo(motsTrouves: List<String>) {
        if (monResultatDuoEnvoye) return
        monResultatDuoEnvoye = true
        _motsTrouvesMoi.value = motsTrouves
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.ResultatDuelMotsDuo(motsTrouves)) }
        finaliserDuoSiPret()
    }

    private fun recevoirResultatDuoAdversaire(motsTrouves: List<String>) {
        _resultatAdversaireDuoRecu.value = true
        _motsTrouvesAdversaire.value = motsTrouves
        finaliserDuoSiPret()
    }

    private fun finaliserDuoSiPret() {
        if (monResultatDuoEnvoye && _resultatAdversaireDuoRecu.value) enregistrerSessionSiNecessaire()
    }

    // --- Enregistrement ---

    /**
     * Score = nombre de mots trouvés, gagné si mon score est au moins celui de l'adversaire
     * (retour utilisateur : une égalité compte comme gagnée pour les deux, comme les parties
     * duo/confrontation existantes depuis la 1.51).
     */
    private fun enregistrerSessionSiNecessaire() {
        if (enregistre) return
        enregistre = true
        val jaiGagne = when (sousMode) {
            SousModeDuelMots.DUO -> _motsTrouvesMoi.value.size >= _motsTrouvesAdversaire.value.size
            SousModeDuelMots.CONFRONTATION, SousModeDuelMots.POINTS -> _gagnant.value == true
        }
        // Pas de signal d'égalité disponible pour les sous-modes Confrontation/Points (retour
        // mainteneur, easter egg "Ex-aequo") : `_gagnant` est un simple booléen gagné/perdu.
        val egalite = if (sousMode == SousModeDuelMots.DUO) _motsTrouvesMoi.value.size == _motsTrouvesAdversaire.value.size else null
        val monScoreFinal = monTotal()
        val manche = ResultatManche(ModeJeu.LETTRES, niveau.name, monScoreFinal)
        // Duel points uniquement (retour utilisateur) : écart signé pour "Rouleau compresseur"/
        // "Déculottée", victoire exacte pour "Compte rond".
        val ecartDuel = if (sousMode == SousModeDuelMots.POINTS) monScoreFinal - totalAdversaire() else null
        val objectifExactAtteint = (sousMode == SousModeDuelMots.POINTS && atteindreExactement && jaiGagne).takeIf { it }
        viewModelScope.launch {
            historiqueRepository.enregistrerSession(
                profilId, sousMode.versTypePartie(), listOf(manche), jaiGagne, egalite, ecartDuel, objectifExactAtteint,
            )
            _tropheesDebloques.value = tropheeRepository.reevaluer(profilId)
        }
    }

    /**
     * Relance une partie sur la connexion déjà établie, sans refaire l'appairage (retour
     * utilisateur). Hôte uniquement : si l'invité l'appelait aussi, les deux téléphones tireraient
     * des graines différentes et désynchroniseraient la partie — l'invité repart automatiquement
     * dès réception de la nouvelle configuration ([demarrerEcouteJeu] écoute en continu).
     */
    fun rejouer() {
        if (role != RoleReseau.HOTE) return
        val objectifApplicable = sousMode == SousModeDuelMots.CONFRONTATION || sousMode == SousModeDuelMots.POINTS
        demarrerCommeHote(sousMode, niveau, objectifMots.takeIf { objectifApplicable }, atteindreExactement)
    }

    fun annulerEtRevenirAuChoix() {
        jobRole?.cancel()
        connexionActive?.fermer()
        connexionActive = null
        _partiesTrouvees.value = emptyList()
        _etat.value = EtatPartieReseau.ChoixRole
    }

    override fun onCleared() {
        jobRole?.cancel()
        timerJobConfrontation?.cancel()
        connexionActive?.fermer()
    }
}
