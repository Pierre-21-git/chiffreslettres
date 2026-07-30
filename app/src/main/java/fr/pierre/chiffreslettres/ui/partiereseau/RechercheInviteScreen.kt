package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.network.CibleDecouverte
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

/** Écran invité : liste des cibles découvertes (Wifi ou Bluetooth selon le transport choisi), à sélectionner pour rejoindre. */
@Composable
fun RechercheInviteScreen(
    parties: List<CibleDecouverte>,
    connexionEnCours: Boolean,
    erreur: String?,
    onSelectionner: (CibleDecouverte) -> Unit,
    onAnnulerErreur: () -> Unit,
    onAnnuler: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.recherche_invite_titre), onAnnuler)

        if (connexionEnCours) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Text(stringResource(R.string.recherche_invite_connexion_en_cours), style = MaterialTheme.typography.titleMedium)
            }
        } else if (parties.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Text(stringResource(R.string.recherche_invite_recherche_en_cours), style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.recherche_invite_instructions),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        } else {
            Text(stringResource(R.string.recherche_invite_parties_trouvees), style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (partie in parties) {
                    OutlinedButton(onClick = { onSelectionner(partie) }, modifier = Modifier.fillMaxWidth()) {
                        Text(partie.libelle)
                    }
                }
            }
        }

        BoutonSecondaireContour(stringResource(R.string.action_annuler), onClick = onAnnuler)
    }

    if (erreur != null) {
        AlertDialog(
            onDismissRequest = onAnnulerErreur,
            title = { Text(stringResource(R.string.recherche_invite_connexion_impossible)) },
            text = { Text(erreur) },
            confirmButton = { TextButton(onClick = onAnnulerErreur) { Text(stringResource(R.string.action_ok)) } },
        )
    }
}
