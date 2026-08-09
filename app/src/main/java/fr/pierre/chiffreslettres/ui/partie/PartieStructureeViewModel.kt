package fr.pierre.chiffreslettres.ui.partie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypePartie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Partagé par le sous-graphe "partie". La séquence n'est connue qu'une fois
 * `ConfigurationPartieScreen` validée (§6.2) — le ViewModel démarre donc vide
 * et se remplit via [demarrer], plutôt que de la recevoir au constructeur.
 */
class PartieStructureeViewModel(
    private val historiqueRepository: HistoriqueRepository,
    private val tropheeRepository: TropheeRepository,
    private val profilId: Long,
) : ViewModel() {

    private val _sequence = MutableStateFlow<List<ManchePlanifiee>>(emptyList())
    val sequence: StateFlow<List<ManchePlanifiee>> = _sequence.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _resultats = MutableStateFlow<List<ResultatManche>>(emptyList())
    val resultats: StateFlow<List<ResultatManche>> = _resultats.asStateFlow()

    private var enregistre = false

    fun demarrer(sequence: List<ManchePlanifiee>) {
        _sequence.value = sequence
        _index.value = 0
        _resultats.value = emptyList()
        enregistre = false
    }

    /**
     * Enregistre le résultat de la manche courante et, s'il s'agit de la dernière, sauvegarde la
     * partie en base immédiatement — sans attendre l'écran récap ni un clic "Terminer", qu'un
     * retour arrière intempestif pourrait court-circuiter (retour utilisateur).
     */
    fun enregistrerResultat(resultat: ResultatManche) {
        _resultats.value = _resultats.value + resultat
        if (_index.value == _sequence.value.lastIndex) {
            enregistrerSessionSiNecessaire()
        }
    }

    fun mancheSuivante() {
        _index.value += 1
    }

    private fun enregistrerSessionSiNecessaire() {
        if (enregistre) return
        enregistre = true
        val resultats = _resultats.value
        viewModelScope.launch {
            historiqueRepository.enregistrerSession(profilId, TypePartie.STRUCTUREE, resultats)
            tropheeRepository.reevaluer(profilId)
        }
    }
}
