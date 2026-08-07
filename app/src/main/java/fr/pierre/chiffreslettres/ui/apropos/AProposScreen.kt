package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.BuildConfig
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun AProposScreen(
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.apropos_titre), onRetour)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                stringResource(R.string.apropos_sous_titre_version),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.apropos_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.apropos_sous_titre_licences),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.apropos_licence_code),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_dictionnaire),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_dictionnaire_en),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_dictionnaire_es),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                stringResource(R.string.apropos_licence_dictionnaire_de),
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
    }
}
