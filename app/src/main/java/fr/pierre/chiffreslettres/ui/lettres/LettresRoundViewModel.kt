package fr.pierre.chiffreslettres.ui.lettres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.letters.meilleurMot
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LettresRoundUiState(
    val niveau: NiveauLettres,
    val nombreLettres: Int,
    val lettresTirees: List<Char> = emptyList(),
    val consonneAutorisee: Boolean = true,
    val tirageTermine: Boolean = false,
    /** Indices dans [lettresTirees] des tuiles cliquées, dans l'ordre (spec §4.5, retour utilisateur : mot construit au clic, pas au clavier). */
    val indicesUtilises: List<Int> = emptyList(),
    val motSaisi: String = "",
    /** Null = pas de limite de temps (entraînement libre, retour utilisateur). */
    val tempsRestantSecondes: Int?,
    val termine: Boolean = false,
    val meilleurMot: String? = null,
    val motJoueurValide: Boolean? = null,
    val scoreObtenu: Int? = null,
)

/** Tirage pas-à-pas (§4.1) puis recherche du mot le plus long (§4.5). */
class LettresRoundViewModel(
    niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    dureeSecondes: Int? = null,
    private val nombreLettres: Int = TirageLettres.NOMBRE_LETTRES,
) : ViewModel() {

    private val sac = SacLettres.creer(niveau)
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        LettresRoundUiState(
            niveau = niveau,
            nombreLettres = nombreLettres,
            consonneAutorisee = TirageLettres.consonneAutorisee(emptyList(), nombreLettres),
            tempsRestantSecondes = dureeSecondes,
        ),
    )
    val uiState: StateFlow<LettresRoundUiState> = _uiState.asStateFlow()

    fun tirerLettre(consonneDemandee: Boolean) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val lettre = TirageLettres.tirerProchaineLettre(sac, etat.lettresTirees, consonneDemandee, nombreLettres)
        val nouvellesLettres = etat.lettresTirees + lettre
        val tirageTermine = nouvellesLettres.size >= nombreLettres
        _uiState.update {
            it.copy(
                lettresTirees = nouvellesLettres,
                consonneAutorisee = TirageLettres.consonneAutorisee(nouvellesLettres, nombreLettres),
                tirageTermine = tirageTermine,
            )
        }
        if (tirageTermine) demarrerChrono()
    }

    private fun demarrerChrono() {
        if (_uiState.value.tempsRestantSecondes == null) return
        timerJob = viewModelScope.launch {
            while ((_uiState.value.tempsRestantSecondes ?: 0) > 0) {
                delay(1000)
                if (_uiState.value.termine) return@launch
                _uiState.update { it.copy(tempsRestantSecondes = it.tempsRestantSecondes?.minus(1)) }
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
        val etat = _uiState.value
        if (etat.termine) return
        mettreAJourMot(etat.indicesUtilises.dropLast(1))
    }

    fun effacerMot() {
        if (_uiState.value.termine) return
        mettreAJourMot(emptyList())
    }

    private fun mettreAJourMot(indicesUtilises: List<Int>) {
        val etat = _uiState.value
        val mot = indicesUtilises.map { etat.lettresTirees[it] }.joinToString("")
        _uiState.update { it.copy(indicesUtilises = indicesUtilises, motSaisi = mot) }
    }

    fun valider() = terminer()

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val etat = _uiState.value
        val motValide = etat.motSaisi.isNotBlank() && dictionnaire.estJouable(etat.motSaisi)
        val meilleur = meilleurMot(etat.lettresTirees, dictionnaire)
        val score = if (motValide) etat.motSaisi.trim().length else 0
        _uiState.update {
            it.copy(termine = true, meilleurMot = meilleur, motJoueurValide = motValide, scoreObtenu = score)
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
