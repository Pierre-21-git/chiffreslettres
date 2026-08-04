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
    /** Dernier mot revalidé alors qu'il était déjà trouvé (retour utilisateur : signalé au joueur, sans point ni arrêt), remis à null au clic suivant. */
    val motDejaTrouve: String? = null,
    val nombreVoyellesChoisi: Int? = null,
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
    niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    configurationAlphabet: ConfigurationAlphabetLettres,
    private val defiRepository: DefiRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
    private val nombreLettres: Int = TirageLettres.NOMBRE_LETTRES,
    private val random: Random = Random,
) : ViewModel() {

    private val sac = SacLettres.creer(
        configurationAlphabet.distributionBase,
        configurationAlphabet.voyelles,
        configurationAlphabet.lettresExcluesParNiveau.getValue(niveau),
    )
    private val niveauCode = niveau.name
    private var timerJob: Job? = null
    private var enregistre = false

    private val _uiState = MutableStateFlow(DefiMotsMaxUiState(niveau = niveau, nombreLettres = nombreLettres))
    val uiState: StateFlow<DefiMotsMaxUiState> = _uiState.asStateFlow()

    fun choisirNombreVoyelles(nombreVoyelles: Int) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val lettres = TirageLettres.tirer(sac, nombreVoyelles, nombreLettres, random)
        _uiState.update { it.copy(lettresTirees = lettres, tirageTermine = true, nombreVoyellesChoisi = nombreVoyelles) }
        demarrerChrono()
    }

    private fun demarrerChrono() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.tempsRestantSecondes > 0) {
                delay(1000)
                if (_uiState.value.termine) return@launch
                _uiState.update { it.copy(tempsRestantSecondes = it.tempsRestantSecondes - 1) }
            }
            terminer()
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
        _uiState.update { it.copy(indicesUtilises = indicesUtilises, motSaisi = mot, motDejaTrouve = null) }
    }

    /**
     * Valide le mot en cours (retour utilisateur) : vide → arrêt volontaire ; invalide (hors
     * dictionnaire) → arrêt du défi (comme le défi série, une erreur y met fin) ; déjà trouvé →
     * signalé, lettres dégrisées, le défi continue sans point ; nouveau → +1, lettres dégrisées,
     * le défi continue.
     */
    fun valider() {
        val etat = _uiState.value
        if (etat.termine || !etat.tirageTermine) return
        val mot = etat.motSaisi
        if (mot.isBlank() || !dictionnaire.estJouable(mot)) {
            terminer()
            return
        }
        if (mot in etat.motsTrouves) {
            _uiState.update { it.copy(indicesUtilises = emptyList(), motSaisi = "", motDejaTrouve = mot) }
            return
        }
        _uiState.update {
            it.copy(motsTrouves = it.motsTrouves + mot, indicesUtilises = emptyList(), motSaisi = "", motDejaTrouve = null)
        }
    }

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        _uiState.update { it.copy(termine = true) }
        if (enregistre) return
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
        _uiState.update { DefiMotsMaxUiState(niveau = it.niveau, nombreLettres = nombreLettres) }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
