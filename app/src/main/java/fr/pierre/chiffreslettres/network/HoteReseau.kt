package fr.pierre.chiffreslettres.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import java.net.ServerSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface EtatHote {
    data object Preparation : EtatHote
    data class EnAttente(val nomServiceAffiche: String) : EtatHote
    data class ClientConnecte(val connexion: ConnexionSocket) : EtatHote
    data class Erreur(val message: String) : EtatHote
}

/**
 * Côté hôte : ouvre un ServerSocket sur un port libre, le publie via NSD sous un nom lisible
 * ("Partie de <pseudo>"), accepte UNE seule connexion (jeu à 2 joueurs) puis cesse aussitôt de se
 * faire connaître. accept() tourne sur Dispatchers.IO, jamais dans un callback NSD.
 */
class HoteReseau(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    fun demarrer(pseudo: String, avatar: String): Flow<EtatHote> = callbackFlow {
        trySend(EtatHote.Preparation)

        val socketServeur = try {
            withContext(Dispatchers.IO) { ServerSocket(0).apply { reuseAddress = true } }
        } catch (e: Exception) {
            trySend(EtatHote.Erreur("Impossible d'ouvrir le port réseau : ${e.message}"))
            close()
            return@callbackFlow
        }

        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "$PREFIXE_NOM_SERVICE_NSD$pseudo"
            serviceType = TYPE_SERVICE_NSD
            port = socketServeur.localPort
        }

        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(infoEnregistre: NsdServiceInfo) {
                trySend(EtatHote.EnAttente(infoEnregistre.serviceName))
            }
            override fun onRegistrationFailed(info: NsdServiceInfo, errorCode: Int) {
                trySend(EtatHote.Erreur("Publication réseau impossible (code $errorCode)"))
                close()
            }
            override fun onServiceUnregistered(info: NsdServiceInfo) {}
            override fun onUnregistrationFailed(info: NsdServiceInfo, errorCode: Int) {}
        }

        try {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
        } catch (e: SecurityException) {
            trySend(EtatHote.Erreur("Permission réseau manquante (Appareils à proximité)."))
            close()
            return@callbackFlow
        }

        launch(Dispatchers.IO) {
            try {
                val socketClient = socketServeur.accept()
                runCatching { nsdManager.unregisterService(listener) }
                runCatching { socketServeur.close() } // un seul invité voulu (jeu à 2)
                val connexion = ConnexionSocket(socketClient.getInputStream(), socketClient.getOutputStream()) {
                    socketClient.close()
                }
                connexion.envoyer(MessageReseau.Bonjour(ProfilReseau(pseudo, avatar)))
                trySend(EtatHote.ClientConnecte(connexion))
            } catch (e: Exception) {
                if (isActive) trySend(EtatHote.Erreur("Connexion interrompue : ${e.message}"))
            }
        }

        awaitClose {
            runCatching { nsdManager.unregisterService(listener) }
            runCatching { socketServeur.close() }
        }
    }
}
