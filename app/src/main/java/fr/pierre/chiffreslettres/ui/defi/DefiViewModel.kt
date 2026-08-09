package fr.pierre.chiffreslettres.ui.defi

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.widget.DefiQuotidienWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Partagé par le sous-graphe "défi", comme `PartieStructureeViewModel` pour la partie solo.
 * Couvre les deux types de défi (retour utilisateur : le défi chrono vient s'ajouter au défi
 * série existant, pas le remplacer) :
 * - [TypeDefi.SERIE] (comportement historique) : enchaîne les manches d'un même mode/niveau (le
 *   chrono de chaque manche reste celui de la partie solo pour ce niveau), s'arrête à la
 *   première erreur ou au temps écoulé, et enregistre la série ([mancheSuivante]/[echec]).
 * - [TypeDefi.CHRONO] : budget de temps global ([budgetSecondes]), enchaîne les manches tant
 *   qu'il reste du temps (chaque manche démarre avec le temps restant, cf.
 *   [dureeProchaineManche]) ; un échec ne met pas fin au défi, seul l'épuisement du budget le
 *   fait, en enregistrant le nombre de réussites ([mancheChronoTerminee]).
 *
 * [essaiId] sert de clé pour recréer une nouvelle instance de `ChiffresRoundViewModel`/
 * `LettresRoundViewModel` à chaque manche (voir sa doc : contrairement à [index], il ne revient
 * jamais à une valeur déjà utilisée, y compris après [recommencer]).
 */
class DefiViewModel(
    private val defiRepository: DefiRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
    private val mode: ModeJeu,
    private val niveauCode: String,
    private val type: TypeDefi = TypeDefi.SERIE,
    private val budgetSecondes: Int = 0,
    /** Non nuls seulement quand cette manche fait partie du défi quotidien (retour utilisateur). */
    private val defiQuotidienRepository: DefiQuotidienRepository? = null,
    private val jourQuotidien: String? = null,
    context: Context? = null,
) : ViewModel() {

    private val appContext = context?.applicationContext

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _reussites = MutableStateFlow(0)
    val reussites: StateFlow<Int> = _reussites.asStateFlow()

    private val _termine = MutableStateFlow(false)
    val termine: StateFlow<Boolean> = _termine.asStateFlow()

    /**
     * Identifiant de manche jamais réutilisé (contrairement à [index], qui repart de 0 à
     * chaque [recommencer]) : sert de clé pour recréer `ChiffresRoundViewModel`/
     * `LettresRoundViewModel` à chaque manche. Sans lui, `viewModel(key = "defi-...-0")`
     * renverrait l'instance déjà terminée de la toute première manche après un
     * "Recommencer", laissant afficher son ancien panneau de résultat avec un bouton
     * "Continuer" actif sans qu'aucun mot/compte n'ait été rejoué (bug remonté par
     * l'utilisateur).
     */
    private val _essaiId = MutableStateFlow(0)
    val essaiId: StateFlow<Int> = _essaiId.asStateFlow()

    private var debutChrono = System.currentTimeMillis()

    /** Défi chrono uniquement : temps restant (s) à donner à la prochaine manche, 0 si le budget est épuisé. */
    fun dureeProchaineManche(): Int {
        val ecoulees = ((System.currentTimeMillis() - debutChrono) / 1000).toInt()
        return (budgetSecondes - ecoulees).coerceAtLeast(0)
    }

    /** Défi série : réussite confirmée par le joueur (bouton "Continuer") : manche suivante. */
    fun mancheSuivante() {
        _index.value += 1
        _essaiId.value += 1
    }

    /** Défi série : échec (mauvais compte/mot, ou temps écoulé) : termine le défi et enregistre la série. */
    fun echec() {
        if (_termine.value) return
        _termine.value = true
        val serieFinale = _index.value
        viewModelScope.launch {
            defiRepository.enregistrer(profilId, mode, niveauCode, type, serieFinale)
            tropheeRepository.reevaluer(profilId)
        }
    }

    /**
     * Défi chrono : une manche vient de se terminer (réussie ou non). Comptabilise la réussite
     * éventuelle, puis enchaîne sur une nouvelle manche s'il reste du budget, ou termine le défi
     * et enregistre le nombre total de réussites sinon.
     */
    fun mancheChronoTerminee(reussie: Boolean) {
        if (_termine.value) return
        if (reussie) _reussites.value += 1
        _index.value += 1
        if (dureeProchaineManche() <= 0) {
            _termine.value = true
            val reussitesFinales = _reussites.value
            viewModelScope.launch {
                defiRepository.enregistrer(profilId, mode, niveauCode, type, reussitesFinales)
                tropheeRepository.reevaluer(profilId)
            }
        } else {
            _essaiId.value += 1
        }
    }

    /**
     * Défi quotidien (série ou chrono) : l'objectif du jour vient d'être atteint. Arrête
     * immédiatement le défi (retour utilisateur : pas question de laisser continuer à jouer
     * après la réussite) et enregistre la performance du jour, en comptant la dernière manche
     * qui vient de faire atteindre l'objectif — sans cela, cette performance n'était jamais
     * écrite en base (bug remonté par l'utilisateur : le trophée "défi chrono lettres, au moins
     * 3" ne se débloquait jamais via le défi quotidien, dont l'objectif en lettres est toujours
     * exactement 3 ; le même défaut existait côté défi série quotidien, où aucune réussite
     * n'était alors jamais enregistrée pour les trophées de série).
     */
    fun objectifQuotidienAtteint() {
        if (_termine.value) return
        if (type == TypeDefi.CHRONO) _reussites.value += 1 else _index.value += 1
        _termine.value = true
        val valeurFinale = if (type == TypeDefi.CHRONO) _reussites.value else _index.value
        viewModelScope.launch {
            defiRepository.enregistrer(profilId, mode, niveauCode, type, valeurFinale)
            tropheeRepository.reevaluer(profilId)
            // Dans la même coroutine liée au ViewModel (pas au composable) : un retour arrière
            // intempestif juste après ne peut plus interrompre l'enregistrement de la réussite du
            // jour ni la mise à jour du widget (retour utilisateur).
            if (defiQuotidienRepository != null && jourQuotidien != null) {
                defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien, niveauCode)
                appContext?.let { DefiQuotidienWidgetProvider.demanderMiseAJour(it) }
            }
        }
    }

    fun recommencer() {
        _index.value = 0
        _reussites.value = 0
        _termine.value = false
        _essaiId.value += 1
        debutChrono = System.currentTimeMillis()
    }
}
