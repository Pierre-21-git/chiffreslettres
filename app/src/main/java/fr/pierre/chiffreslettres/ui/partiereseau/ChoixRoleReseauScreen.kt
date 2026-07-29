package fr.pierre.chiffreslettres.ui.partiereseau

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

/**
 * Choix du rôle pour une partie en réseau (2 téléphones sur le même Wifi). La permission
 * "Appareils à proximité" (NEARBY_WIFI_DEVICES, obligatoire dès Android 13 pour la découverte
 * Wifi) est demandée au clic sur l'une des deux tuiles plutôt qu'au lancement de l'app.
 */
@Composable
fun ChoixRoleReseauScreen(
    pseudoActif: String,
    onHeberger: () -> Unit,
    onRejoindre: () -> Unit,
    onRetour: () -> Unit,
) {
    val context = LocalContext.current
    var permissionAccordee by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var actionEnAttente by remember { mutableStateOf<(() -> Unit)?>(null) }
    val lanceurPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { accordee ->
        permissionAccordee = accordee
        if (accordee) actionEnAttente?.invoke()
        actionEnAttente = null
    }
    fun executerAvecPermission(action: () -> Unit) {
        if (permissionAccordee) {
            action()
        } else {
            actionEnAttente = action
            lanceurPermission.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("Jouer en réseau", onRetour)
        PucePseudo(pseudoActif)
        Text(
            "Connectez deux téléphones sur le même réseau Wifi pour jouer en duo ou en " +
                "confrontation, chacun sur son propre appareil.",
            style = MaterialTheme.typography.bodyMedium,
        )
        if (!permissionAccordee) {
            Text(
                "La permission « Appareils à proximité » sera demandée pour trouver l'autre " +
                    "téléphone sur le réseau.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        TuilePrincipale("Héberger une partie", onClick = { executerAvecPermission(onHeberger) })
        TuilePrincipale("Rejoindre une partie", onClick = { executerAvecPermission(onRejoindre) })
    }
}
