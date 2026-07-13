package fr.pierre.chiffreslettres.ui.lettres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.letters.meilleurMot
import fr.pierre.chiffreslettres.ui.entrainement.DureesParDefaut
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LettresRoundUiState(
    val niveau: NiveauLettres,
    val lettresTirees: List<Char> = emptyList(),
    val consonneAutorisee: Boolean = true,
    val tirageTermine: Boolean = false,
    val motSaisi: String = "",
    val tempsRestantSecondes: Int,
    val termine: Boolean = false,
    val meilleurMot: String? = null,
    val motJoueurValide: Boolean? = null,
    val scoreObtenu: Int? = null,
)

/** Tirage pas-à-pas (§4.1) puis recherche du mot le plus long (§4.5). */
class LettresRoundViewModel(
    niveau: NiveauLettres,
    private val dictionnaire: DictionnaireIndex,
    dureeSecondes: Int = DureesParDefaut.LETTRES_SECONDES,
) : ViewModel() {

    private val sac = SacLettres.creer(niveau)
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        LettresRoundUiState(
            niveau = niveau,
            consonneAutorisee = TirageLettres.consonneAutorisee(emptyList()),
            tempsRestantSecondes = dureeSecondes,
        ),
    )
    val uiState: StateFlow<LettresRoundUiState> = _uiState.asStateFlow()

    fun tirerLettre(consonneDemandee: Boolean) {
        val etat = _uiState.value
        if (etat.tirageTermine || etat.termine) return
        val lettre = TirageLettres.tirerProchaineLettre(sac, etat.lettresTirees, consonneDemandee)
        val nouvellesLettres = etat.lettresTirees + lettre
        val tirageTermine = nouvellesLettres.size >= TirageLettres.NOMBRE_LETTRES
        _uiState.update {
            it.copy(
                lettresTirees = nouvellesLettres,
                consonneAutorisee = TirageLettres.consonneAutorisee(nouvellesLettres),
                tirageTermine = tirageTermine,
            )
        }
        if (tirageTermine) demarrerChrono()
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

    fun saisirMot(mot: String) {
        if (_uiState.value.termine) return
        _uiState.update { it.copy(motSaisi = mot) }
    }

    fun valider() = terminer()

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val etat = _uiState.value
        val motValide = etat.motSaisi.isNotBlank() && dictionnaire.estJouable(etat.motSaisi, etat.lettresTirees)
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
