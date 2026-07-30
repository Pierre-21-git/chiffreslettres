package fr.pierre.chiffreslettres.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.partie.ManchePlanifiee
import fr.pierre.chiffreslettres.ui.partie.sequenceAlternee
import fr.pierre.chiffreslettres.ui.partieduo.ModeScoreDuo
import fr.pierre.chiffreslettres.ui.partieduo.ResultatDuoManche
import fr.pierre.chiffreslettres.ui.partieduo.TourDuo
import fr.pierre.chiffreslettres.ui.partieduo.VainqueurManche
import fr.pierre.chiffreslettres.ui.partieduo.premierJoueurManche
import fr.pierre.chiffreslettres.ui.partieduo.vainqueurMancheChiffres
import fr.pierre.chiffreslettres.ui.partieduo.vainqueurMancheLettres
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class RoleReseau { HOTE, INVITE }

enum class TransportReseau { WIFI, BLUETOOTH }

/** Unifie les cibles découvertes des deux transports pour un affichage/sélection génériques. */
sealed interface CibleDecouverte {
    val libelle: String

    data class Wifi(val partie: PartieDecouverte) : CibleDecouverte {
        override val libelle: String get() = partie.nomService
    }

    data class Bluetooth(val partie: PartieDecouverteBluetooth) : CibleDecouverte {
        override val libelle: String get() = partie.nom
    }
}

sealed interface EtatPartieReseau {
    data object ChoixRole : EtatPartieReseau
    data object Preparation : EtatPartieReseau
    data class AttenteHote(val nomServiceAffiche: String) : EtatPartieReseau
    data object RechercheInvite : EtatPartieReseau
    data class ConnexionEnCours(val cible: CibleDecouverte) : EtatPartieReseau
    data class Connecte(val profilDistant: ProfilReseau, val role: RoleReseau) : EtatPartieReseau
    data class Erreur(val message: String) : EtatPartieReseau
}

/**
 * Une manche se joue simultanément des 2 côtés, mais son déclenchement suit l'ordre A,B,B,A de
 * [premierJoueurManche] (comme en duo local) : un seul des 2 joueurs choisit le nombre de
 * voyelles (lettres) ou appuie sur "Commencer la manche" (chiffres) ; l'autre attend, puis les 2
 * jouent en même temps une fois la manche déclenchée.
 */
sealed interface EtatManche {
    data object AttenteDeclenchement : EtatManche
    data object EnCours : EtatManche
    data object Revelation : EtatManche
}

/**
 * Partagé par le sous-graphe de navigation "reseau" (même principe que PartieDuoViewModel pour
 * le scope de sous-graphe). Construit manuellement avec le pseudo/avatar du profil actif (pas de
 * DI dans ce projet) — voir partieReseauViewModel() dans AppNavHost.kt.
 *
 * Phase de jeu calquée sur PartieDuoViewModel : mêmes types (TourDuo, premierJoueurManche,
 * ResultatDuoManche, ComparaisonDuo), mais [tour] désigne ici qui déclenche la manche (pas qui
 * joue — les 2 jouent toujours simultanément), et resultatsJoueur1/resultatsJoueur2 sont indexés
 * par identité fixe (hôte=JOUEUR1, invité=JOUEUR2), jamais par tour.
 */
class PartieReseauViewModel(
    context: Context,
    private val pseudo: String,
    private val avatar: String,
) : ViewModel() {

    private val hoteReseau = HoteReseau(context.applicationContext)
    private val inviteReseau = InviteReseau(context.applicationContext)
    private val hoteBluetooth = HoteBluetooth(context.applicationContext)
    private val inviteBluetooth = InviteBluetooth(context.applicationContext)

    private val _etat = MutableStateFlow<EtatPartieReseau>(EtatPartieReseau.ChoixRole)
    val etat: StateFlow<EtatPartieReseau> = _etat.asStateFlow()

    private val _partiesTrouvees = MutableStateFlow<List<CibleDecouverte>>(emptyList())
    val partiesTrouvees: StateFlow<List<CibleDecouverte>> = _partiesTrouvees.asStateFlow()

    private var connexionActive: ConnexionSocket? = null
    private var jobRole: Job? = null

    // --- Phase de jeu ---

    private val _sequence = MutableStateFlow<List<ManchePlanifiee>>(emptyList())
    val sequence: StateFlow<List<ManchePlanifiee>> = _sequence.asStateFlow()

    private val _seeds = MutableStateFlow<List<Long>>(emptyList())
    val seeds: StateFlow<List<Long>> = _seeds.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    /** Qui déclenche la manche courante (pas qui la joue : les 2 jouent toujours ensemble). */
    private val _tour = MutableStateFlow(TourDuo.JOUEUR1)
    val tour: StateFlow<TourDuo> = _tour.asStateFlow()

    private val _etatManche = MutableStateFlow<EtatManche>(EtatManche.AttenteDeclenchement)
    val etatManche: StateFlow<EtatManche> = _etatManche.asStateFlow()

    // Indexés par numéro de manche (pas des listes ajoutées séquentiellement) : un message
    // ("commencer la manche", choix des voyelles, résultat) peut arriver côté récepteur avant
    // qu'il n'ait lui-même atteint cette manche (ex. encore sur l'écran de révélation précédent,
    // pas encore cliqué "Suivant"). Il ne doit jamais être perdu, seulement mémorisé jusqu'à ce
    // que l'index local le rattrape — recalculerEtatManche() est l'unique endroit qui décide de
    // l'état affiché, à partir de l'index courant et de ce qui a déjà été reçu pour cet index.
    private val _resultatsJoueur1 = MutableStateFlow<Map<Int, ResultatDuoManche>>(emptyMap())
    val resultatsJoueur1: StateFlow<Map<Int, ResultatDuoManche>> = _resultatsJoueur1.asStateFlow()

    private val _resultatsJoueur2 = MutableStateFlow<Map<Int, ResultatDuoManche>>(emptyMap())
    val resultatsJoueur2: StateFlow<Map<Int, ResultatDuoManche>> = _resultatsJoueur2.asStateFlow()

    private val _manchesDeclenchees = MutableStateFlow<Set<Int>>(emptySet())

    /** Manches pour lesquelles l'adversaire a signalé être arrivé sur l'écran d'attente. */
    private val _adversairePretPourManche = MutableStateFlow<Set<Int>>(emptySet())
    val adversairePretPourManche: StateFlow<Set<Int>> = _adversairePretPourManche.asStateFlow()

    private val _choixVoyellesRecu = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val choixVoyellesRecu: StateFlow<Map<Int, Int>> = _choixVoyellesRecu.asStateFlow()

    private val _erreurJeu = MutableStateFlow<String?>(null)
    val erreurJeu: StateFlow<String?> = _erreurJeu.asStateFlow()

    /** Fixé une fois au démarrage : l'hôte est toujours JOUEUR1, l'invité toujours JOUEUR2. */
    var monTourDuo: TourDuo = TourDuo.JOUEUR1
        private set
    var mode: ModeScoreDuo = ModeScoreDuo.DUO
        private set

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
        jobRole?.cancel() // plus la peine de continuer à scanner une fois qu'on tente une connexion
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
        monTourDuo = if (role == RoleReseau.HOTE) TourDuo.JOUEUR1 else TourDuo.JOUEUR2
        _etat.value = EtatPartieReseau.Connecte(bonjourDistant.profil, role)
        demarrerEcouteJeu(connexion)
    }

    private fun demarrerEcouteJeu(connexion: ConnexionSocket) {
        viewModelScope.launch {
            connexion.messagesRecus.collect { message ->
                when (message) {
                    is MessageReseau.Configuration -> {
                        val niveau = Niveau.valueOf(message.niveauCode)
                        mode = ModeScoreDuo.valueOf(message.modeCode)
                        initialiserPartie(niveau, message.seeds)
                    }
                    is MessageReseau.DemarrerManche -> {
                        _manchesDeclenchees.value = _manchesDeclenchees.value + message.index
                        recalculerEtatManche()
                    }
                    is MessageReseau.ChoixVoyelles -> {
                        _choixVoyellesRecu.value = _choixVoyellesRecu.value + (message.index to message.nombre)
                        _manchesDeclenchees.value = _manchesDeclenchees.value + message.index
                        recalculerEtatManche()
                    }
                    is MessageReseau.ResultatDeManche ->
                        enregistrerResultat(autre(monTourDuo), message.index, versResultatDuoManche(message))
                    is MessageReseau.PretPourManche ->
                        _adversairePretPourManche.value = _adversairePretPourManche.value + message.index
                    else -> {}
                }
            }
        }
        viewModelScope.launch {
            connexion.estOuverte.collect { ouverte ->
                if (!ouverte && _sequence.value.isNotEmpty() && _index.value < _sequence.value.size) {
                    _erreurJeu.value = "Connexion perdue avec l'adversaire."
                }
            }
        }
    }

    private fun initialiserPartie(niveau: Niveau, seeds: List<Long>) {
        val niveauLettres = NiveauLettres.valueOf(niveau.name)
        _sequence.value = sequenceAlternee(niveau.manchesParMode, niveauLettres, niveau.manchesParMode, niveau)
        _seeds.value = seeds
        _index.value = 0
        _tour.value = premierJoueurManche(0)
        recalculerEtatManche()
    }

    /**
     * Seule fonction qui décide de l'état affiché pour la manche courante, à partir de ce qui a
     * déjà été reçu/joué pour cet index précis — jamais d'après l'ordre d'arrivée des messages
     * (cf. commentaire sur resultatsJoueur1/2). Appelée après toute mise à jour pertinente.
     */
    private fun recalculerEtatManche() {
        val idx = _index.value
        if (idx >= _sequence.value.size) return
        val r1 = _resultatsJoueur1.value[idx]
        val r2 = _resultatsJoueur2.value[idx]
        _etatManche.value = when {
            r1 != null && r2 != null -> EtatManche.Revelation
            idx in _manchesDeclenchees.value -> EtatManche.EnCours
            else -> EtatManche.AttenteDeclenchement
        }
    }

    fun demarrerCommeHote(niveau: Niveau, mode: ModeScoreDuo) {
        this.mode = mode
        val niveauLettres = NiveauLettres.valueOf(niveau.name)
        val sequence = sequenceAlternee(niveau.manchesParMode, niveauLettres, niveau.manchesParMode, niveau)
        val seeds = List(sequence.size) { Random.nextLong() }
        viewModelScope.launch {
            connexionActive?.envoyer(MessageReseau.Configuration(niveau.name, mode.name, seeds))
        }
        initialiserPartie(niveau, seeds)
    }

    /** Envoyé par le non-déclencheur dès qu'il affiche l'écran d'attente d'une manche. */
    fun signalerPret() {
        val idx = _index.value
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.PretPourManche(idx)) }
    }

    /** Manches chiffres : appelée uniquement quand `tour == monTourDuo` (je suis le déclencheur). */
    fun declencherManche() {
        val idx = _index.value
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.DemarrerManche(idx)) }
        _manchesDeclenchees.value = _manchesDeclenchees.value + idx
        recalculerEtatManche()
    }

    /** Manches lettres : appelée uniquement quand `tour == monTourDuo` (je suis le déclencheur). */
    fun envoyerChoixVoyelles(nombre: Int) {
        val idx = _index.value
        viewModelScope.launch { connexionActive?.envoyer(MessageReseau.ChoixVoyelles(idx, nombre)) }
        _manchesDeclenchees.value = _manchesDeclenchees.value + idx
        recalculerEtatManche()
    }

    fun enregistrerMonResultat(resultat: ResultatDuoManche) {
        val idx = _index.value
        viewModelScope.launch {
            connexionActive?.envoyer(
                MessageReseau.ResultatDeManche(
                    index = idx,
                    modeJeu = resultat.resultat.mode.name,
                    niveauCode = resultat.resultat.niveauCode,
                    score = resultat.resultat.score,
                    motJoue = resultat.resultat.motJoue,
                    ecartCible = resultat.ecartCible,
                    detail = resultat.detail,
                ),
            )
        }
        enregistrerResultat(monTourDuo, _index.value, resultat)
    }

    private fun enregistrerResultat(role: TourDuo, index: Int, resultat: ResultatDuoManche) {
        if (role == TourDuo.JOUEUR1) {
            _resultatsJoueur1.value = _resultatsJoueur1.value + (index to resultat)
        } else {
            _resultatsJoueur2.value = _resultatsJoueur2.value + (index to resultat)
        }
        recalculerEtatManche()
    }

    /** Clic sur "Manche suivante"/"Voir les résultats" de l'écran de révélation. */
    fun mancheSuivante() {
        val nouvelIndex = _index.value + 1
        _index.value = nouvelIndex
        if (nouvelIndex >= _sequence.value.size) return
        _tour.value = premierJoueurManche(nouvelIndex)
        recalculerEtatManche()
    }

    private fun autre(role: TourDuo): TourDuo = if (role == TourDuo.JOUEUR1) TourDuo.JOUEUR2 else TourDuo.JOUEUR1

    private fun versResultatDuoManche(message: MessageReseau.ResultatDeManche): ResultatDuoManche = ResultatDuoManche(
        resultat = ResultatManche(ModeJeu.valueOf(message.modeJeu), message.niveauCode, message.score, message.motJoue),
        ecartCible = message.ecartCible,
        detail = message.detail,
    )

    /**
     * Identique à PartieDuoViewModel.resultatsFinaux() (mêmes règles DUO/CONFRONTATION) : réécrit
     * ici plutôt que réutilisé tel quel, PartieDuoViewModel étant un ViewModel Android distinct
     * couplé à sa propre navigation locale.
     */
    fun resultatsFinaux(): Pair<List<ResultatManche>, List<ResultatManche>> {
        val r1 = _resultatsJoueur1.value
        val r2 = _resultatsJoueur2.value
        val manchesCompletes = r1.keys.intersect(r2.keys).sorted()
        if (mode != ModeScoreDuo.CONFRONTATION) {
            return manchesCompletes.map { r1.getValue(it).resultat } to manchesCompletes.map { r2.getValue(it).resultat }
        }
        val finaux1 = mutableListOf<ResultatManche>()
        val finaux2 = mutableListOf<ResultatManche>()
        for (i in manchesCompletes) {
            val a = r1.getValue(i)
            val b = r2.getValue(i)
            val vainqueur = when (a.resultat.mode) {
                ModeJeu.CHIFFRES -> vainqueurMancheChiffres(a.ecartCible, b.ecartCible)
                ModeJeu.LETTRES -> vainqueurMancheLettres(a.resultat.motJoue, b.resultat.motJoue)
            }
            when (vainqueur) {
                VainqueurManche.JOUEUR1 -> {
                    finaux1 += a.resultat
                    finaux2 += b.resultat.copy(score = 0)
                }
                VainqueurManche.JOUEUR2 -> {
                    finaux1 += a.resultat.copy(score = 0)
                    finaux2 += b.resultat
                }
                VainqueurManche.EGALITE -> {
                    finaux1 += a.resultat
                    finaux2 += b.resultat
                }
            }
        }
        return finaux1 to finaux2
    }

    fun annulerEtRevenirAuChoix() {
        jobRole?.cancel()
        connexionActive?.fermer()
        connexionActive = null
        _partiesTrouvees.value = emptyList()
        _sequence.value = emptyList()
        _adversairePretPourManche.value = emptySet()
        _etat.value = EtatPartieReseau.ChoixRole
    }

    override fun onCleared() {
        jobRole?.cancel()
        connexionActive?.fermer()
    }
}
