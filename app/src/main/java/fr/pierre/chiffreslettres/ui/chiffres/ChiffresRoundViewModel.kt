package fr.pierre.chiffreslettres.ui.chiffres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.numbers.Bareme
import fr.pierre.chiffreslettres.numbers.Expression
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.numbers.Operation
import fr.pierre.chiffreslettres.numbers.Solveur
import fr.pierre.chiffreslettres.numbers.TirageChiffres
import fr.pierre.chiffreslettres.ui.entrainement.DureesParDefaut
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Jeton(val id: Int, val expression: Expression)

data class ChiffresRoundUiState(
    val niveau: Niveau,
    val cible: Int,
    val jetons: List<Jeton>,
    val premierSelectionne: Jeton? = null,
    val operateurSelectionne: Operation? = null,
    val tempsRestantSecondes: Int,
    val termine: Boolean = false,
    val scoreObtenu: Int? = null,
    val solutionSolveur: Expression?,
)

/** Interface calculatrice pas-à-pas du §3.4 : combine deux jetons à la fois. */
class ChiffresRoundViewModel(
    private val niveau: Niveau,
    dureeSecondes: Int = DureesParDefaut.CHIFFRES_SECONDES,
) : ViewModel() {

    private val historique = mutableListOf<List<Jeton>>()
    private var prochainId = 0
    private var timerJob: Job? = null

    private val _uiState: MutableStateFlow<ChiffresRoundUiState>
    val uiState: StateFlow<ChiffresRoundUiState>

    init {
        val tirage = TirageChiffres.tirer(niveau)
        _uiState = MutableStateFlow(
            ChiffresRoundUiState(
                niveau = niveau,
                cible = tirage.cible,
                jetons = tirage.nombres.map { valeur -> Jeton(prochainId++, Expression.Valeur(valeur)) },
                tempsRestantSecondes = dureeSecondes,
                solutionSolveur = tirage.solution,
            ),
        )
        uiState = _uiState.asStateFlow()
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

    fun cliquerJeton(jeton: Jeton) {
        val etat = _uiState.value
        if (etat.termine) return
        val premier = etat.premierSelectionne
        when {
            premier == null -> _uiState.update { it.copy(premierSelectionne = jeton) }
            etat.operateurSelectionne == null -> {
                _uiState.update { it.copy(premierSelectionne = if (jeton.id == premier.id) null else jeton) }
            }
            jeton.id != premier.id -> combiner(etat, premier, etat.operateurSelectionne, jeton)
        }
    }

    fun cliquerOperateur(operation: Operation) {
        val etat = _uiState.value
        if (etat.termine || etat.premierSelectionne == null || operation !in niveau.operations) return
        _uiState.update { it.copy(operateurSelectionne = operation) }
    }

    private fun combiner(etat: ChiffresRoundUiState, gauche: Jeton, operation: Operation, droite: Jeton) {
        val resultat = Solveur.combiner(gauche.expression, operation, droite.expression) ?: return
        historique.add(etat.jetons)
        val nouveauxJetons = etat.jetons.filter { it.id != gauche.id && it.id != droite.id } + Jeton(prochainId++, resultat)
        _uiState.update { it.copy(jetons = nouveauxJetons, premierSelectionne = null, operateurSelectionne = null) }
    }

    fun annulerDerniereOperation() {
        val precedent = historique.removeLastOrNull() ?: return
        _uiState.update { it.copy(jetons = precedent, premierSelectionne = null, operateurSelectionne = null) }
    }

    fun valider() = terminer()

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val etat = _uiState.value
        val proposition = etat.premierSelectionne?.expression?.resultat
        _uiState.update { it.copy(termine = true, scoreObtenu = Bareme.score(etat.cible, proposition)) }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
