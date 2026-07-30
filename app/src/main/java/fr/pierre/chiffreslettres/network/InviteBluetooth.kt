package fr.pierre.chiffreslettres.network

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

data class PartieDecouverteBluetooth(val nom: String, val device: BluetoothDevice)

/**
 * Côté invité en Bluetooth : combine les appareils déjà appairés (disponibles immédiatement,
 * `bondedDevices`) et une découverte active (`startDiscovery`) pour les appareils non encore
 * appairés — un premier appairage se fera au moment de la connexion.
 */
class InviteBluetooth(private val context: Context) {
    private val adaptateur = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    fun rechercherAppareils(): Flow<List<PartieDecouverteBluetooth>> = callbackFlow {
        val adaptateurActif = adaptateur
        if (adaptateurActif == null || !adaptateurActif.isEnabled) {
            close()
            return@callbackFlow
        }

        val trouves = LinkedHashMap<String, PartieDecouverteBluetooth>()

        runCatching { adaptateurActif.bondedDevices }.getOrNull()?.forEach { device ->
            trouves[device.address] = PartieDecouverteBluetooth(device.name ?: device.address, device)
        }
        trySend(trouves.values.toList())

        val recepteur = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action != BluetoothDevice.ACTION_FOUND) return
                val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    ?: return
                trouves[device.address] = PartieDecouverteBluetooth(device.name ?: device.address, device)
                trySend(trouves.values.toList())
            }
        }
        ContextCompat.registerReceiver(
            context,
            recepteur,
            IntentFilter(BluetoothDevice.ACTION_FOUND),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        try {
            adaptateurActif.startDiscovery()
        } catch (e: SecurityException) {
            runCatching { context.unregisterReceiver(recepteur) }
            close()
            return@callbackFlow
        }

        awaitClose {
            runCatching { adaptateurActif.cancelDiscovery() }
            runCatching { context.unregisterReceiver(recepteur) }
        }
    }

    suspend fun rejoindre(cible: PartieDecouverteBluetooth, pseudo: String, avatar: String): ConnexionSocket =
        withContext(Dispatchers.IO) {
            runCatching { adaptateur?.cancelDiscovery() } // recommandé avant une connexion (doc Android)
            val socket = cible.device.createRfcommSocketToServiceRecord(UUID_SERVICE_BLUETOOTH)
            socket.connect()
            ConnexionSocket(socket.inputStream, socket.outputStream) { socket.close() }
                .also { it.envoyer(MessageReseau.Bonjour(ProfilReseau(pseudo, avatar))) }
        }
}
