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
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Jeton(val id: Int, val expression: Expression)

/**
 * Détail d'une manche chiffres terminée (retour mainteneur, easter eggs "Nombre premier",
 * "Calcul mental", "Chemin minimal", "Chirurgical", "Speedrun", "Va-tout", "100 heures de jeu") :
 * calculé une seule fois à la validation, transmis tel quel jusqu'à l'enregistrement en base.
 */
data class DetailChiffresManche(
    val cible: Int,
    val nombreOperations: Int,
    /** Résultat intermédiaire le plus élevé parmi les opérations jouées, null si aucune opération. */
    val maxEtapeIntermediaire: Int?,
    /** Null si manche non chronométrée (entraînement libre). */
    val dureeSecondesEcoulees: Int?,
    val tempsRestantSecondes: Int?,
    /** Écart avec la cible de la proposition finale, null si rien n'a été proposé (easter egg "À côté de la plaque"). */
    val ecartCible: Int?,
    /** Masque des opérations utilisées (bit = `Operation.ordinal`), pour l'easter egg "Boîte à outils". */
    val operateursUtilises: Int,
)

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
    /** Écart avec la cible de la proposition finale, null si rien n'a été proposé — utilisé pour comparer deux joueurs en mode duo confrontation (le score seul ne suffit pas : deux écarts différents peuvent tomber dans le même palier de points). */
    val ecartCible: Int? = null,
    val solutionSolveur: Expression?,
    val detailFinal: DetailChiffresManche? = null,
)

private data class Etape(val jetons: List<Jeton>, val operationsEffectuees: List<String>)

/** Interface calculatrice pas-à-pas du §3.4 : combine deux jetons à la fois. */
class ChiffresRoundViewModel(
    private val niveau: Niveau,
    private val dureeSecondes: Int? = null,
    nombreJetons: Int = ReservoirChiffres.NOMBRE_JETONS_DEFAUT,
    /** Permet au mode Défi de forcer une solution exacte même sur Monique/Mathieu. */
    garantieSolution: Boolean = niveau.garantieSolution,
    /** Permet au mode Duo de rejouer exactement le même tirage pour les deux joueurs (même graine, nouvelle instance de Random par joueur). */
    random: Random = Random,
) : ViewModel() {

    private val historique = mutableListOf<Etape>()
    private var prochainId = 0
    private var timerJob: Job? = null
    private val jetonsInitiaux: List<Jeton>

    private val _uiState: MutableStateFlow<ChiffresRoundUiState>
    val uiState: StateFlow<ChiffresRoundUiState>

    init {
        val tirage = TirageChiffres.tirer(niveau, random, nombreJetons = nombreJetons, garantieSolution = garantieSolution)
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
        // éventuellement sélectionné (retour utilisateur). Si aucune opération n'a été
        // faite, il n'y a pas de proposition (jetons = tirage initial) : compter alors
        // un des nombres tirés comme un "compte" donnerait des points sans rien avoir
        // résolu (bug remonté par l'utilisateur, cf. Bareme "0 si rien n'a été proposé").
        val proposition = if (historique.isEmpty()) null else etat.jetons.lastOrNull()?.expression?.resultat
        // Écart entre la cible et la meilleure valeur atteignable pour ce tirage (0 si une
        // solution exacte existait) — cf. TirageChiffres.Resultat.solution, utilisé par le
        // barème pour créditer 10 points à l'approche la plus proche théoriquement possible
        // quand aucune solution exacte n'existe (retour utilisateur).
        val meilleurEcartAtteignable = etat.solutionSolveur?.let { abs(etat.cible - it.resultat) } ?: 0
        val score = Bareme.score(niveau, etat.cible, proposition, meilleurEcartAtteignable)
        val ecart = proposition?.let { abs(etat.cible - it) }
        val maxEtapeIntermediaire = etat.operationsEffectuees
            .mapNotNull { ligne -> ligne.substringAfterLast("= ").toIntOrNull()?.let { abs(it) } }
            .maxOrNull()
        // Masque des opérations utilisées (retour utilisateur, easter egg "Boîte à outils") :
        // détecté sur le texte des lignes plutôt que sur `historique` (qui ne garde que l'état
        // avant chaque combinaison, pas le type d'opération employée).
        val operateursUtilises = Operation.entries.fold(0) { masque, operation ->
            if (etat.operationsEffectuees.any { it.contains(operation.symbole) }) masque or (1 shl operation.ordinal) else masque
        }
        val detail = DetailChiffresManche(
            cible = etat.cible,
            nombreOperations = etat.operationsEffectuees.size,
            maxEtapeIntermediaire = maxEtapeIntermediaire,
            dureeSecondesEcoulees = dureeSecondes?.let { it - (etat.tempsRestantSecondes ?: 0) },
            tempsRestantSecondes = etat.tempsRestantSecondes,
            ecartCible = ecart,
            operateursUtilises = operateursUtilises,
        )
        _uiState.update { it.copy(termine = true, scoreObtenu = score, ecartCible = ecart, detailFinal = detail) }
    }

    override fun onCleared() {
        timerJob?.cancel()
    }
}
