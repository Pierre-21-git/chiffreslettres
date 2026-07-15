package fr.pierre.chiffreslettres.ui.chiffres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.numbers.Bareme
import fr.pierre.chiffreslettres.numbers.Expression
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.numbers.Operation
import fr.pierre.chiffreslettres.numbers.ReservoirChiffres
import fr.pierre.chiffreslettres.numbers.Solveur
import fr.pierre.chiffreslettres.numbers.TirageChiffres
import kotlin.math.abs
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
    val operationsEffectuees: List<String> = emptyList(),
    /** Null = pas de limite de temps (entraînement libre, retour utilisateur). */
    val tempsRestantSecondes: Int?,
    val termine: Boolean = false,
    val scoreObtenu: Int? = null,
    val solutionSolveur: Expression?,
)

private data class Etape(val jetons: List<Jeton>, val operationsEffectuees: List<String>)

/** Interface calculatrice pas-à-pas du §3.4 : combine deux jetons à la fois. */
class ChiffresRoundViewModel(
    private val niveau: Niveau,
    dureeSecondes: Int? = null,
    nombreJetons: Int = ReservoirChiffres.NOMBRE_JETONS_DEFAUT,
) : ViewModel() {

    private val historique = mutableListOf<Etape>()
    private var prochainId = 0
    private var timerJob: Job? = null
    private val jetonsInitiaux: List<Jeton>

    private val _uiState: MutableStateFlow<ChiffresRoundUiState>
    val uiState: StateFlow<ChiffresRoundUiState>

    init {
        val tirage = TirageChiffres.tirer(niveau, nombreJetons = nombreJetons)
        jetonsInitiaux = tirage.nombres.map { valeur -> Jeton(prochainId++, Expression.Valeur(valeur)) }
        _uiState = MutableStateFlow(
            ChiffresRoundUiState(
                niveau = niveau,
                cible = tirage.cible,
                jetons = jetonsInitiaux,
                tempsRestantSecondes = dureeSecondes,
                solutionSolveur = tirage.solution,
            ),
        )
        uiState = _uiState.asStateFlow()
        demarrerChrono()
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
        historique.add(Etape(etat.jetons, etat.operationsEffectuees))
        val nouveauxJetons = etat.jetons.filter { it.id != gauche.id && it.id != droite.id } + Jeton(prochainId++, resultat)
        val ligne = "${gauche.expression.resultat} ${operation.symbole} ${droite.expression.resultat} = ${resultat.resultat}"
        _uiState.update {
            it.copy(
                jetons = nouveauxJetons,
                premierSelectionne = null,
                operateurSelectionne = null,
                operationsEffectuees = it.operationsEffectuees + ligne,
            )
        }
    }

    fun annulerDerniereOperation() {
        val precedente = historique.removeLastOrNull() ?: return
        _uiState.update {
            it.copy(
                jetons = precedente.jetons,
                premierSelectionne = null,
                operateurSelectionne = null,
                operationsEffectuees = precedente.operationsEffectuees,
            )
        }
    }

    /** Repart du tirage initial, façon "Effacer" du mode Lettres. */
    fun effacerCalcul() {
        if (_uiState.value.termine) return
        historique.clear()
        _uiState.update {
            it.copy(
                jetons = jetonsInitiaux,
                premierSelectionne = null,
                operateurSelectionne = null,
                operationsEffectuees = emptyList(),
            )
        }
    }

    fun valider() = terminer()

    private fun terminer() {
        if (_uiState.value.termine) return
        timerJob?.cancel()
        val etat = _uiState.value
        // Le dernier élément de la liste des comptes est le résultat le plus récent
        // (chaque combinaison remplace ses deux opérandes par le résultat en fin de
        // liste) : c'est ce résultat qui compte comme proposition, pas le jeton
        // éventuellement sélectionné (retour utilisateur).
        val proposition = etat.jetons.lastOrNull()?.expression?.resultat
        val ecartMinimalAtteignable = etat.solutionSolveur?.let { abs(etat.cible - it.resultat) } ?: 0
        val score = Bareme.score(niveau, etat.cible, proposition, ecartMinimalAtteignable)
        _uiState.update { it.copy(termine = true, scoreObtenu = score) }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
