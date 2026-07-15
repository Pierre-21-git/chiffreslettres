package fr.pierre.chiffreslettres.ui.entrainement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Partagé par tout le sous-graphe "entrainement" (§6.1) : score cumulé affiché
 * en continu tant que le joueur enchaîne les manches, enregistré dans
 * l'historique quand la session s'arrête.
 */
class EntrainementLibreViewModel(
    private val historiqueRepository: HistoriqueRepository,
    private val profilId: Long,
) : ViewModel() {
    private val _manches = MutableStateFlow<List<ResultatManche>>(emptyList())
    val manches: StateFlow<List<ResultatManche>> = _manches.asStateFlow()

    fun enregistrerMancheChiffres(niveau: Niveau, score: Int) {
        enregistrer(ResultatManche(ModeJeu.CHIFFRES, niveau.name, score))
    }

    fun enregistrerMancheLettres(niveau: NiveauLettres, score: Int, motJoue: String?) {
        enregistrer(ResultatManche(ModeJeu.LETTRES, niveau.name, score, motJoue))
    }

    private fun enregistrer(resultat: ResultatManche) {
        _manches.value = _manches.value + resultat
    }

    /** Appelé en quittant l'entraînement : enregistre la session dans l'historique si au moins une manche a été jouée. */
    fun terminerEtEnregistrer() {
        val manchesJouees = _manches.value
        if (manchesJouees.isNotEmpty()) {
            viewModelScope.launch {
                historiqueRepository.enregistrerSession(profilId, TypePartie.LIBRE, manchesJouees)
            }
        }
        _manches.value = emptyList()
    }
}
