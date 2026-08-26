package fr.pierre.chiffreslettres.ui.reglages

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ReglagesStore
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import kotlinx.coroutines.launch

@Composable
fun ReglagesScreen(
    reglagesStore: ReglagesStore,
    onRetour: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val rappelActif by reglagesStore.rappelDefiActif.collectAsState(initial = false)

    // Demande la permission de notification uniquement à l'activation du rappel (retour
    // mainteneur F-Droid : pas de demande tant que la fonctionnalité correspondante n'est pas
    // utilisée par l'utilisateur), pas au lancement de l'app.
    val lanceurPermissionNotification =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.reglages_titre), onRetour)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.reglages_rappel_quotidien_titre), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.reglages_rappel_quotidien_description), style = MaterialTheme.typography.bodyMedium)
            }
            Switch(
                checked = rappelActif,
                onCheckedChange = { actif ->
                    scope.launch { reglagesStore.definirRappelDefiActif(actif) }
                    if (actif &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        lanceurPermissionNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
            )
        }
    }
}
