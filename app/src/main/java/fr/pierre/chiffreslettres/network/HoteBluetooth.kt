package fr.pierre.chiffreslettres.network

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Côté hôte en Bluetooth : ouvre un canal RFCOMM sur l'UUID fixe du jeu, accepte UNE seule
 * connexion (jeu à 2 joueurs). Contrairement au Wifi, pas de service à publier explicitement :
 * l'appareil doit juste être "visible" (déclenché côté écran via ACTION_REQUEST_DISCOVERABLE)
 * pour qu'un invité non encore appairé puisse le découvrir.
 */
class HoteBluetooth(private val context: Context) {
    private val adaptateur = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    fun demarrer(pseudo: String, avatar: String): Flow<EtatHote> = callbackFlow {
        trySend(EtatHote.Preparation)

        val adaptateurActif = adaptateur
        if (adaptateurActif == null || !adaptateurActif.isEnabled) {
            trySend(EtatHote.Erreur("Le Bluetooth doit être activé."))
            close()
            return@callbackFlow
        }

        val serveur: BluetoothServerSocket = try {
            withContext(Dispatchers.IO) {
                adaptateurActif.listenUsingRfcommWithServiceRecord(NOM_SERVICE_BLUETOOTH, UUID_SERVICE_BLUETOOTH)
            }
        } catch (e: SecurityException) {
            trySend(EtatHote.Erreur("Permission Bluetooth manquante."))
            close()
            return@callbackFlow
        } catch (e: Exception) {
            trySend(EtatHote.Erreur("Impossible d'ouvrir le service Bluetooth : ${e.message}"))
            close()
            return@callbackFlow
        }

        val nomAffiche = runCatching { adaptateurActif.name }.getOrNull() ?: NOM_SERVICE_BLUETOOTH
        trySend(EtatHote.EnAttente(nomAffiche))

        launch(Dispatchers.IO) {
            try {
                val socketClient = serveur.accept()
                runCatching { serveur.close() } // un seul invité voulu (jeu à 2)
                val connexion = ConnexionSocket(socketClient.inputStream, socketClient.outputStream) {
                    socketClient.close()
                }
                connexion.envoyer(MessageReseau.Bonjour(ProfilReseau(pseudo, avatar)))
                trySend(EtatHote.ClientConnecte(connexion))
            } catch (e: Exception) {
                if (isActive) trySend(EtatHote.Erreur("Connexion interrompue : ${e.message}"))
            }
        }

        awaitClose { runCatching { serveur.close() } }
    }
}
