package fr.pierre.chiffreslettres.ui.partie

import androidx.lifecycle.ViewModel
import fr.pierre.chiffreslettres.data.ResultatManche
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Partagé par le sous-graphe "partie". La séquence n'est connue qu'une fois
 * `ConfigurationPartieScreen` validée (§6.2) — le ViewModel démarre donc vide
 * et se remplit via [demarrer], plutôt que de la recevoir au constructeur.
 */
class PartieStructureeViewModel : ViewModel() {

    private val _sequence = MutableStateFlow<List<ManchePlanifiee>>(emptyList())
    val sequence: StateFlow<List<ManchePlanifiee>> = _sequence.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _resultats = MutableStateFlow<List<ResultatManche>>(emptyList())
    val resultats: StateFlow<List<ResultatManche>> = _resultats.asStateFlow()

    fun demarrer(sequence: List<ManchePlanifiee>) {
        _sequence.value = sequence
        _index.value = 0
        _resultats.value = emptyList()
    }

    fun enregistrerResultat(resultat: ResultatManche) {
        _resultats.value = _resultats.value + resultat
    }

    fun mancheSuivante() {
        _index.value += 1
    }
}
