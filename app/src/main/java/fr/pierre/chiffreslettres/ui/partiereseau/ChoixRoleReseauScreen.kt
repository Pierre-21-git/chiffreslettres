package fr.pierre.chiffreslettres.ui.partiereseau

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import fr.pierre.chiffreslettres.network.TransportReseau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

private const val DUREE_DECOUVRABLE_BLUETOOTH_SECONDES = 300

/**
 * Choix du transport (Wifi local ou Bluetooth) puis du rôle pour une partie en réseau. Les
 * permissions "à proximité" nécessaires depuis Android 13 (NEARBY_WIFI_DEVICES côté Wifi,
 * BLUETOOTH_CONNECT/BLUETOOTH_SCAN côté Bluetooth) sont demandées au clic sur une tuile, pas au
 * lancement de l'app.
 *
 * Le Wifi local peut être bloqué par une isolation des clients sur certaines box domestiques
 * (retour utilisateur, EHOSTUNREACH constaté) : le Bluetooth s'appaire directement entre les 2
 * téléphones sans dépendre du routeur, d'où ce second transport au choix plutôt qu'un remplacement.
 */
@Composable
fun ChoixRoleReseauScreen(
    pseudoActif: String,
    onHeberger: (TransportReseau) -> Unit,
    onRejoindre: (TransportReseau) -> Unit,
    onRetour: () -> Unit,
) {
    val context = LocalContext.current
    var transport by remember { mutableStateOf(TransportReseau.WIFI) }
    var actionEnAttente by remember { mutableStateOf<(() -> Unit)?>(null) }

    val lanceurDecouvrable = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Best effort : on démarre l'hébergement même si l'utilisateur refuse la visibilité —
        // les appareils déjà appairés fonctionneront quand même.
        actionEnAttente?.invoke()
        actionEnAttente = null
    }
    val lanceurPermissionWifi = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { accordee ->
        if (accordee) actionEnAttente?.invoke()
        actionEnAttente = null
    }
    val lanceurPermissionsBluetooth = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { resultats ->
        if (resultats.values.all { it }) actionEnAttente?.invoke() else actionEnAttente = null
    }

    fun demarrerVisibiliteBluetoothPuis(action: () -> Unit) {
        actionEnAttente = action
        lanceurDecouvrable.launch(
            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DUREE_DECOUVRABLE_BLUETOOTH_SECONDES),
        )
    }

    fun avecPermissionsTransport(action: () -> Unit) {
        when (transport) {
            TransportReseau.WIFI -> {
                val accordee = ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) ==
                    PackageManager.PERMISSION_GRANTED
                if (accordee) {
                    action()
                } else {
                    actionEnAttente = action
                    lanceurPermissionWifi.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                }
            }
            TransportReseau.BLUETOOTH -> {
                val accordees = listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
                    .all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
                if (accordees) {
                    action()
                } else {
                    actionEnAttente = action
                    lanceurPermissionsBluetooth.launch(
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                    )
                }
            }
        }
    }

    fun executerHeberger(action: () -> Unit) {
        val actionApresPermissions = if (transport == TransportReseau.BLUETOOTH) {
            { demarrerVisibiliteBluetoothPuis(action) }
        } else {
            action
        }
        avecPermissionsTransport(actionApresPermissions)
    }

    fun executerRejoindre(action: () -> Unit) = avecPermissionsTransport(action)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("Jouer en réseau", onRetour)
        PucePseudo(pseudoActif)
        Text(
            "Connectez deux téléphones pour jouer en duo ou en confrontation, chacun sur son " +
                "propre appareil.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Text("Comment se connecter ?", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BoutonChoixTransport(
                "Wifi local",
                selectionne = transport == TransportReseau.WIFI,
                onClick = { transport = TransportReseau.WIFI },
            )
            BoutonChoixTransport(
                "Bluetooth",
                selectionne = transport == TransportReseau.BLUETOOTH,
                onClick = { transport = TransportReseau.BLUETOOTH },
            )
        }
        Text(
            when (transport) {
                TransportReseau.WIFI -> "Les deux téléphones doivent être sur le même réseau Wifi."
                TransportReseau.BLUETOOTH -> "L'appareil hôte devra accepter d'être visible ; l'appairage " +
                    "peut être demandé à la première connexion."
            },
            style = MaterialTheme.typography.bodySmall,
        )

        TuilePrincipale("Héberger une partie", onClick = { executerHeberger { onHeberger(transport) } })
        TuilePrincipale("Rejoindre une partie", onClick = { executerRejoindre { onRejoindre(transport) } })
    }
}

@Composable
private fun BoutonChoixTransport(texte: String, selectionne: Boolean, onClick: () -> Unit) {
    if (selectionne) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    }
}
