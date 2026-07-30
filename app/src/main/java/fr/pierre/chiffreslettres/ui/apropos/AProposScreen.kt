package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.BuildConfig
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun AProposScreen(
    onReglesDuJeu: () -> Unit,
    onVersions: () -> Unit,
    onTrophees: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.apropos_titre), onRetour)
        Text(
            stringResource(R.string.apropos_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
            style = MaterialTheme.typography.titleMedium,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.apropos_licence_code),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_dictionnaire),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_filtrage),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_bibliotheques),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onReglesDuJeu, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.apropos_bouton_regles_du_jeu)) }
            Button(onClick = onTrophees, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.apropos_bouton_trophees)) }
            if (BuildConfig.DEBUG) {
                Button(onClick = onVersions, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.apropos_bouton_versions)) }
            }
        }
    }
}
