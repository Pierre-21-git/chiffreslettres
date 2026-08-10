package fr.pierre.chiffreslettres.ui.defi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Raison de fin du défi (retour utilisateur) : affichée à l'écran pour expliquer l'arrêt. */
enum class RaisonFinDefiMotsMax { VOLONTAIRE, TEMPS_ECOULE }

/**
 * Raison du rejet d'un mot qui ne met pas fin au défi (retour utilisateur : un mot invalide ou
 * trop court ne doit pas faire perdre la partie en cours, seulement être signalé).
 */
enum class RaisonRejetMotDefiMotsMax { DEJA_TROUVE, INVALIDE, TROP_COURT }

data class DefiMotsMaxUiState(
    val niveau: NiveauLettres,
    val nombreLettres: Int,
    val lettresTirees: List<Char> = emptyList(),
    val tirageTermine: Boolean = false,
    val indicesUtilises: List<Int> = emptyList(),
    val motSaisi: String = "",
    val tempsRestantSecondes: Int = DUREE_SECONDES_DEFI_MOTS_MAX,
    val termine: Boolean = false,
    /** Mots distincts déjà validés sur ce tirage, dans l'ordre de découverte. */
    val motsTrouves: List<String> = emptyList(),
    /** Dernier mot refusé sur cette saisie (déjà trouvé, invalide, ou trop court) — retour utilisateur : signalé au joueur, sans point ni arrêt du défi, remis à null au clic suivant. */
    val motRejeteTransitoire: String? = null,
    val raisonRejetTransitoire: RaisonRejetMotDefiMotsMax? = null,
    val nombreVoyellesChoisi: Int? = null,
    /** Pourquoi le défi s'est arrêté (retour utilisateur), null tant qu'il n'est pas terminé. */
    val raisonFin: RaisonFinDefiMotsMax? = null,
    /** Tous les mots d'au moins [seuilLongueurDefiLettres] lettres jouables sur ce tirage (retour utilisateur : révélés en fin de défi), triés du plus long au plus court. */
    val motsPossibles: List<String> = emptyList(),
)

/**
 * Défi "mots max" (retour utilisateur) : un seul tirage de lettres, 5 minutes fixes quel que soit
 * le niveau, le plus de mots distincts possible sur ce même tirage. Contrairement à la manche solo
 * (`LettresRoundViewModel`, un seul mot par tirage), les lettres utilisées sont "dégrisées" après
 * chaque mot validé pour permettre d'en chercher un autre sur le même tirage. Le défi ne s'arrête
 * que sur un mot refusé par le dictionnaire ou une validation à vide (retour utilisateur : pas de
 * bouton "terminer" séparé, valider sans lettre suffit) ; un mot déjà trouvé est signalé mais ne
 * met pas fin au défi, et ne compte pas de point supplémentaire. Un seul ViewModel couvre tout le
 * défi (pas de chaînage de manches comme `DefiViewModel`/`ChiffresRoundViewModel` en série/chrono),
 * donc il enregistre lui-même la performance finale.
 */
class DefiMotsMaxViewModel(
    private val niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    private val configurationAlphabet: ConfigurationAlphabetLettres,
    private val defiRepository: DefiRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
    private val nombreLettres: Int = TirageLettres.NOMBRE_LETTRES,
    private val random: Random = Random,
    /**
     * Faux pour le round "duel mots" (réseau), qui réutilise ce moteur mais enregistre lui-même
     * sa propre session/trophées (`TypePartie.DUEL_MOTS_RESEAU`) — sans ce garde-fou, chaque
     * partie de duel mots compterait aussi, à tort, comme un défi mots max solo.
     */
    private val enregistrerResultat: Boolean = true,
) : ViewModel() {

    private fun sacNeuf() = SacLettres.creer(
        configurationAlphabet.distributionBase,
        configurationAlphabet.voyelles,
        configurationAlphabet.lettresExcluesParNiveau.getValue(niveau),
    )

    private var sac = sacNeuf()
    private val niveauCode = niveau.name
    private val seuilLongueur = seuilLongueurDefiLettres(niveau)
    /**
     * Niveau Monique ou Mathieu uniquement (retour utilisateur) : les trophées Platine/Diamant
     * du défi mots exigent 10 mots de [seuilLongueur] lettres ou plus sur le même tirage — sans
     * garantie, un tirage aléatoire n'en contient pas forcément autant. Cf. [tirerAvecGarantie].
     */
    private val garantieDixMots = niveau == NiveauLettres.MONIQUE || niveau == NiveauLettres.MATHIEU
    private var timerJob: Job? = null
    private var enregistre = false

    private val _uiState = MutableStateFlow(DefiMotsMaxUiState(niveau = niveau, nombreLettres = nombreLettres))
    val uiState: StateFlow<DefiMotsMaxUiState> = _uiState.asStateFlow()

    fun choisirNombreVoyelles(nombreVoyelles: Int) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val lettres = if (garantieDixMots) tirerAvecGarantie(nombreVoyelles) else TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
        _uiState.update { it.copy(lettresTirees = lettres, tirageTermine = true, nombreVoyellesChoisi = nombreVoyelles) }
        demarrerChrono()
    }

    /**
     * Chaque tentative recrée un sac neuf (le précédent tirage l'a vidé) jusqu'à trouver un
     * tirage offrant au moins [NOMBRE_MOTS_GARANTIS] mots distincts de [seuilLongueur] lettres ou
     * plus, ou abandonne après [MAX_TENTATIVES_GARANTIE] essais (dictionnaire trop pauvre pour ce
     * tirage de lettres) en renvoyant le dernier tirage obtenu, pour ne jamais bloquer la partie.
     */
    private fun tirerAvecGarantie(nombreVoyelles: Int): List<Char> {
        repeat(MAX_TENTATIVES_GARANTIE - 1) {
            val tirage = TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
            if (dictionnaire.rechercherAuMoins(tirage, seuilLongueur).distinct().size >= NOMBRE_MOTS_GARANTIS) return tirage
            sac = sacNeuf()
        }
        return TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
    }

    private fun demarrerChrono() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.tempsRestantSecondes > 0) {
                delay(1000)
                if (_uiState.value.termine) return@launch
                _uiState.update { it.copy(tempsRestantSecondes = it.tempsRestantSecondes - 1) }
            }
            terminer(RaisonFinDefiMotsMax.TEMPS_ECOULE)
        }
    }

    fun cliquerLettre(index: Int) {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        if (index !in etat.lettresTirees.indices || index in etat.indicesUtilises) return
        mettreAJourMot(etat.indicesUtilises + index)
    }

    fun annulerLettre() {
        if (_uiState.value.termine) return
        mettreAJourMot(_uiState.value.indicesUtilises.dropLast(1))
    }

    fun effacerMot() {
        if (_uiState.value.termine) return
        mettreAJourMot(emptyList())
    }

    private fun mettreAJourMot(indicesUtilises: List<Int>) {
        val etat = _uiState.value
        val mot = indicesUtilises.map { etat.lettresTirees[it] }.joinToString("")
        _uiState.update { it.copy(indicesUtilises = indicesUtilises, motSaisi = mot, motRejeteTransitoire = null, raisonRejetTransitoire = null) }
    }

    /**
     * Valide le mot en cours (retour utilisateur) : vide → arrêt volontaire (seul cas qui met fin
     * au défi, c'est le mécanisme prévu pour arrêter volontairement) ; hors dictionnaire, trop
     * court pour le niveau (cf. [seuilLongueurDefiLettres]), ou déjà trouvé → simplement signalé
     * au joueur ([RaisonRejetMotDefiMotsMax]), lettres dégrisées, le défi continue sans point (un
     * mot raté ne doit pas faire perdre la partie en cours) ; nouveau mot valide → +1, lettres
     * dégrisées, le défi continue.
     */
    fun valider() {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        val mot = etat.motSaisi
        if (mot.isBlank()) {
            terminer(RaisonFinDefiMotsMax.VOLONTAIRE)
            return
        }
        if (!dictionnaire.estJouable(mot)) {
            rejeterMot(mot, RaisonRejetMotDefiMotsMax.INVALIDE)
            return
        }
        if (mot.length < seuilLongueur) {
            rejeterMot(mot, RaisonRejetMotDefiMotsMax.TROP_COURT)
            return
        }
        if (mot in etat.motsTrouves) {
            rejeterMot(mot, RaisonRejetMotDefiMotsMax.DEJA_TROUVE)
            return
        }
        _uiState.update {
            it.copy(motsTrouves = it.motsTrouves + mot, indicesUtilises = emptyList(), motSaisi = "", motRejeteTransitoire = null, raisonRejetTransitoire = null)
        }
    }

    private fun rejeterMot(mot: String, raison: RaisonRejetMotDefiMotsMax) {
        _uiState.update {
            it.copy(indicesUtilises = emptyList(), motSaisi = "", motRejeteTransitoire = mot, raisonRejetTransitoire = raison)
        }
    }

    private fun terminer(raison: RaisonFinDefiMotsMax) {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val motsPossibles = dictionnaire.rechercherAuMoins(_uiState.value.lettresTirees, seuilLongueur)
            .distinct()
            .sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))
        _uiState.update { it.copy(termine = true, raisonFin = raison, motsPossibles = motsPossibles) }
        if (enregistre || !enregistrerResultat) return
        enregistre = true
        val score = _uiState.value.motsTrouves.size
        viewModelScope.launch {
            defiRepository.enregistrer(profilId, ModeJeu.LETTRES, niveauCode, TypeDefi.MOTS_MAX, score)
            tropheeRepository.reevaluer(profilId)
        }
    }

    fun recommencer() {
        timerJob?.cancel()
        enregistre = false
        sac = sacNeuf()
        _uiState.update { DefiMotsMaxUiState(niveau = it.niveau, nombreLettres = nombreLettres) }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }

    private companion object {
        const val NOMBRE_MOTS_GARANTIS = 10
        const val MAX_TENTATIVES_GARANTIE = 300
    }
}
