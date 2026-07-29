package fr.pierre.chiffreslettres.network

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class RoleReseau { HOTE, INVITE }

sealed interface EtatPartieReseau {
    data object ChoixRole : EtatPartieReseau
    data object Preparation : EtatPartieReseau
    data class AttenteHote(val nomServiceAffiche: String) : EtatPartieReseau
    data object RechercheInvite : EtatPartieReseau
    data class ConnexionEnCours(val cible: PartieDecouverte) : EtatPartieReseau
    data class Connecte(val profilDistant: ProfilReseau, val role: RoleReseau) : EtatPartieReseau
    data class Erreur(val message: String) : EtatPartieReseau
}

/**
 * Partagé par le sous-graphe de navigation "reseau" (même principe que PartieDuoViewModel pour
 * le scope de sous-graphe). Construit manuellement avec le pseudo/avatar du profil actif (pas de
 * DI dans ce projet) — voir partieReseauViewModel() dans AppNavHost.kt.
 *
 * S'arrête à EtatPartieReseau.Connecte : la ConnexionSocket établie est prête pour une future
 * sous-version qui y branchera la logique de jeu (synchronisation des manches, seeds partagées,
 * etc. — hors scope ici).
 */
class PartieReseauViewModel(
    context: Context,
    private val pseudo: String,
    private val avatar: String,
) : ViewModel() {

    private val hoteReseau = HoteReseau(context.applicationContext)
    private val inviteReseau = InviteReseau(context.applicationContext)

    private val _etat = MutableStateFlow<EtatPartieReseau>(EtatPartieReseau.ChoixRole)
    val etat: StateFlow<EtatPartieReseau> = _etat.asStateFlow()

    private val _partiesTrouvees = MutableStateFlow<List<PartieDecouverte>>(emptyList())
    val partiesTrouvees: StateFlow<List<PartieDecouverte>> = _partiesTrouvees.asStateFlow()

    private var connexionActive: ConnexionSocket? = null
    private var jobRole: Job? = null

    fun choisirHote() {
        jobRole?.cancel()
        jobRole = viewModelScope.launch {
            hoteReseau.demarrer(pseudo, avatar).collect { etatHote ->
                when (etatHote) {
                    is EtatHote.Preparation -> _etat.value = EtatPartieReseau.Preparation
                    is EtatHote.EnAttente -> _etat.value = EtatPartieReseau.AttenteHote(etatHote.nomServiceAffiche)
                    is EtatHote.ClientConnecte -> attendreHandshake(etatHote.connexion, RoleReseau.HOTE)
                    is EtatHote.Erreur -> _etat.value = EtatPartieReseau.Erreur(etatHote.message)
                }
            }
        }
    }

    fun choisirInvite() {
        jobRole?.cancel()
        _etat.value = EtatPartieReseau.RechercheInvite
        _partiesTrouvees.value = emptyList()
        jobRole = viewModelScope.launch {
            inviteReseau.rechercherParties().collect { _partiesTrouvees.value = it }
        }
    }

    fun rejoindre(partie: PartieDecouverte) {
        _etat.value = EtatPartieReseau.ConnexionEnCours(partie)
        jobRole?.cancel() // plus la peine de continuer à scanner une fois qu'on tente une connexion
        viewModelScope.launch {
            try {
                val connexion = inviteReseau.rejoindre(partie, pseudo, avatar)
                attendreHandshake(connexion, RoleReseau.INVITE)
            } catch (e: Exception) {
                _etat.value = EtatPartieReseau.Erreur("Connexion impossible : ${e.message}")
            }
        }
    }

    private suspend fun attendreHandshake(connexion: ConnexionSocket, role: RoleReseau) {
        connexionActive = connexion
        val bonjourDistant = withTimeoutOrNull(TIMEOUT_HANDSHAKE_MS) {
            connexion.messagesRecus.filterIsInstance<MessageReseau.Bonjour>().first()
        }
        if (bonjourDistant == null) {
            _etat.value = EtatPartieReseau.Erreur("Le pair n'a pas répondu à temps.")
            connexion.fermer()
            connexionActive = null
            return
        }
        _etat.value = EtatPartieReseau.Connecte(bonjourDistant.profil, role)
    }

    fun annulerEtRevenirAuChoix() {
        jobRole?.cancel()
        connexionActive?.fermer()
        connexionActive = null
        _partiesTrouvees.value = emptyList()
        _etat.value = EtatPartieReseau.ChoixRole
    }

    override fun onCleared() {
        jobRole?.cancel()
        connexionActive?.fermer()
    }
}
