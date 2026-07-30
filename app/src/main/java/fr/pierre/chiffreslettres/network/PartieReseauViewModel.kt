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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class RoleReseau { HOTE, INVITE }

enum class TransportReseau { WIFI, BLUETOOTH }

/** Unifie les cibles découvertes des deux transports pour un affichage/sélection génériques. */
sealed interface CibleDecouverte {
    val libelle: String

    data class Wifi(val partie: PartieDecouverte) : CibleDecouverte {
        override val libelle: String get() = partie.nomService
    }

    data class Bluetooth(val partie: PartieDecouverteBluetooth) : CibleDecouverte {
        override val libelle: String get() = partie.nom
    }
}

sealed interface EtatPartieReseau {
    data object ChoixRole : EtatPartieReseau
    data object Preparation : EtatPartieReseau
    data class AttenteHote(val nomServiceAffiche: String) : EtatPartieReseau
    data object RechercheInvite : EtatPartieReseau
    data class ConnexionEnCours(val cible: CibleDecouverte) : EtatPartieReseau
    data class Connecte(val profilDistant: ProfilReseau, val role: RoleReseau) : EtatPartieReseau
    data class Erreur(val message: String) : EtatPartieReseau
}

/**
 * Partagé par le sous-graphe de navigation "reseau" (même principe que PartieDuoViewModel pour
 * le scope de sous-graphe). Construit manuellement avec le pseudo/avatar du profil actif (pas de
 * DI dans ce projet) — voir partieReseauViewModel() dans AppNavHost.kt.
 *
 * Deux transports au choix (Wifi local ou Bluetooth, cf. TransportReseau) : le Wifi a été mis en
 * échec par une isolation des clients sur certains réseaux domestiques (EHOSTUNREACH constaté en
 * test réel), le Bluetooth s'appaire directement entre les 2 téléphones sans dépendre du routeur.
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
    private val hoteBluetooth = HoteBluetooth(context.applicationContext)
    private val inviteBluetooth = InviteBluetooth(context.applicationContext)

    private val _etat = MutableStateFlow<EtatPartieReseau>(EtatPartieReseau.ChoixRole)
    val etat: StateFlow<EtatPartieReseau> = _etat.asStateFlow()

    private val _partiesTrouvees = MutableStateFlow<List<CibleDecouverte>>(emptyList())
    val partiesTrouvees: StateFlow<List<CibleDecouverte>> = _partiesTrouvees.asStateFlow()

    private var connexionActive: ConnexionSocket? = null
    private var jobRole: Job? = null

    fun choisirHote(transport: TransportReseau) {
        jobRole?.cancel()
        val flux = when (transport) {
            TransportReseau.WIFI -> hoteReseau.demarrer(pseudo, avatar)
            TransportReseau.BLUETOOTH -> hoteBluetooth.demarrer(pseudo, avatar)
        }
        jobRole = viewModelScope.launch {
            flux.collect { etatHote ->
                when (etatHote) {
                    is EtatHote.Preparation -> _etat.value = EtatPartieReseau.Preparation
                    is EtatHote.EnAttente -> _etat.value = EtatPartieReseau.AttenteHote(etatHote.nomServiceAffiche)
                    is EtatHote.ClientConnecte -> attendreHandshake(etatHote.connexion, RoleReseau.HOTE)
                    is EtatHote.Erreur -> _etat.value = EtatPartieReseau.Erreur(etatHote.message)
                }
            }
        }
    }

    fun choisirInvite(transport: TransportReseau) {
        jobRole?.cancel()
        _etat.value = EtatPartieReseau.RechercheInvite
        _partiesTrouvees.value = emptyList()
        val flux = when (transport) {
            TransportReseau.WIFI -> inviteReseau.rechercherParties().map { liste -> liste.map { CibleDecouverte.Wifi(it) } }
            TransportReseau.BLUETOOTH ->
                inviteBluetooth.rechercherAppareils().map { liste -> liste.map { CibleDecouverte.Bluetooth(it) } }
        }
        jobRole = viewModelScope.launch {
            flux.collect { _partiesTrouvees.value = it }
        }
    }

    fun rejoindre(cible: CibleDecouverte) {
        _etat.value = EtatPartieReseau.ConnexionEnCours(cible)
        jobRole?.cancel() // plus la peine de continuer à scanner une fois qu'on tente une connexion
        viewModelScope.launch {
            try {
                val connexion = when (cible) {
                    is CibleDecouverte.Wifi -> inviteReseau.rejoindre(cible.partie, pseudo, avatar)
                    is CibleDecouverte.Bluetooth -> inviteBluetooth.rejoindre(cible.partie, pseudo, avatar)
                }
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
