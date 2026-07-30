package fr.pierre.chiffreslettres.network

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Enveloppe une connexion déjà établie (socket TCP ou Bluetooth RFCOMM, hôte ou invité) via ses
 * flux bruts : indépendant du transport sous-jacent, seule la fermeture ([fermeture]) diffère
 * entre les deux. Lecture/écriture ligne par ligne, un message JSON par ligne, toujours sur
 * Dispatchers.IO. Possède sa propre CoroutineScope interne (indépendante du ViewModel appelant) :
 * fermer() en est le seul point d'arrêt.
 *
 * Channel non borné plutôt qu'un SharedFlow : évite toute ambiguïté sur la rétention des
 * messages émis avant le premier collecteur (un SharedFlow à replay=0 peut perdre un message
 * émis avant l'abonnement). Un seul lecteur à la fois est attendu pour cette sous-version
 * (le handshake).
 */
class ConnexionSocket(entree: InputStream, sortie: OutputStream, private val fermeture: () -> Unit) {
    private val lecteur = BufferedReader(InputStreamReader(entree, Charsets.UTF_8))
    private val ecrivain = BufferedWriter(OutputStreamWriter(sortie, Charsets.UTF_8))
    private val portee = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val canalEntrant = Channel<MessageReseau>(Channel.UNLIMITED)
    val messagesRecus: Flow<MessageReseau> = canalEntrant.receiveAsFlow()

    private val _estOuverte = MutableStateFlow(true)
    val estOuverte: StateFlow<Boolean> = _estOuverte.asStateFlow()

    init {
        portee.launch {
            try {
                while (true) {
                    val ligne = lecteur.readLine() ?: break
                    MessageReseau.depuisJson(ligne)?.let { canalEntrant.send(it) }
                }
            } catch (e: Exception) {
                // Coupure réseau : traitée comme une fermeture, cf. estOuverte.
            } finally {
                _estOuverte.value = false
                canalEntrant.close()
            }
        }
    }

    suspend fun envoyer(message: MessageReseau) = withContext(Dispatchers.IO) {
        ecrivain.write(message.versJson().toString())
        ecrivain.newLine()
        ecrivain.flush()
    }

    /**
     * Ferme la connexion sous-jacente : c'est ce qui débloque un readLine() en attente (annuler
     * la coroutine seule ne suffit pas, un appel bloquant Java n'est pas interrompu par
     * Job.cancel()).
     */
    fun fermer() {
        runCatching { fermeture() }
        portee.cancel()
        _estOuverte.value = false
    }
}
