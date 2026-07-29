package fr.pierre.chiffreslettres.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class PartieDecouverte(val nomService: String, val hote: InetAddress, val port: Int)

/**
 * Côté invité : découverte des services `_clettres._tcp.` publiés et connexion socket à celui
 * choisi. Les résolutions sont sérialisées via un Mutex : la pile NSD de la plateforme ne
 * supporte de façon fiable qu'une résolution à la fois sur beaucoup d'appareils Android 13/14
 * (piège plateforme connu), un second appel concurrent peut planter ou se bloquer selon l'OEM.
 */
class InviteReseau(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val mutexResolution = Mutex()

    fun rechercherParties(): Flow<List<PartieDecouverte>> = callbackFlow {
        val trouvees = MutableStateFlow<Map<String, PartieDecouverte>>(emptyMap())
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {}
            override fun onServiceFound(service: NsdServiceInfo) {
                launch {
                    val resolue = resoudre(service) ?: return@launch
                    trouvees.update { it + (resolue.nomService to resolue) }
                    trySend(trouvees.value.values.toList())
                }
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                trouvees.update { it - service.serviceName }
                trySend(trouvees.value.values.toList())
            }
            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                close()
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
        }
        try {
            nsdManager.discoverServices(TYPE_SERVICE_NSD, NsdManager.PROTOCOL_DNS_SD, listener)
        } catch (e: SecurityException) {
            close()
            return@callbackFlow
        }
        awaitClose { runCatching { nsdManager.stopServiceDiscovery(listener) } }
    }

    // API dépréciée au profit d'une variante à Executor apparue après l'API 33 (minSdk du
    // projet) : conservée volontairement pour garantir la présence de la méthode sur tous les
    // appareils ciblés, plutôt que de risquer une NoSuchMethodError sur Android 13.
    @Suppress("DEPRECATION")
    private suspend fun resoudre(service: NsdServiceInfo): PartieDecouverte? = mutexResolution.withLock {
        suspendCancellableCoroutine { continuation ->
            val listener = object : NsdManager.ResolveListener {
                override fun onServiceResolved(infoResolue: NsdServiceInfo) {
                    val hote = infoResolue.host
                    if (continuation.isActive) {
                        continuation.resume(
                            if (hote != null) PartieDecouverte(infoResolue.serviceName, hote, infoResolue.port) else null,
                        )
                    }
                }
                override fun onResolveFailed(infoEchouee: NsdServiceInfo, errorCode: Int) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
            nsdManager.resolveService(service, listener)
        }
    }

    suspend fun rejoindre(partie: PartieDecouverte, pseudo: String, avatar: String): ConnexionSocket =
        withContext(Dispatchers.IO) {
            val socket = Socket()
            socket.connect(InetSocketAddress(partie.hote, partie.port), TIMEOUT_CONNEXION_MS)
            ConnexionSocket(socket).also { it.envoyer(MessageReseau.Bonjour(ProfilReseau(pseudo, avatar))) }
        }
}
