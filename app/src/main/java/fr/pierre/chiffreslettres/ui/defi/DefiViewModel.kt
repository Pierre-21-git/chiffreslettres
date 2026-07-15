package fr.pierre.chiffreslettres.ui.defi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Partagé par le sous-graphe "défi", comme `PartieStructureeViewModel` pour la partie solo :
 * enchaîne les manches d'un même mode/niveau (le chrono reste celui de la partie solo pour ce
 * niveau, retour utilisateur), s'arrête à la première erreur ou au temps écoulé, et enregistre
 * la série. [index] sert de clé pour recréer une nouvelle instance de `ChiffresRoundViewModel`/
 * `LettresRoundViewModel` à chaque manche, exactement comme `PartieStructureeViewModel.index`.
 */
class DefiViewModel(
    private val defiRepository: DefiRepository,
    private val profilId: Long,
    private val mode: ModeJeu,
    private val niveauCode: String,
) : ViewModel() {

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _termine = MutableStateFlow(false)
    val termine: StateFlow<Boolean> = _termine.asStateFlow()

    /** Réussite confirmée par le joueur (bouton "Continuer") : manche suivante. */
    fun mancheSuivante() {
        _index.value += 1
    }

    /** Échec (mauvais compte/mot, ou temps écoulé) : termine le défi et enregistre la série. */
    fun echec() {
        if (_termine.value) return
        _termine.value = true
        val serieFinale = _index.value
        viewModelScope.launch {
            defiRepository.enregistrer(profilId, mode, niveauCode, serieFinale)
        }
    }

    fun recommencer() {
        _index.value = 0
        _termine.value = false
    }
}
