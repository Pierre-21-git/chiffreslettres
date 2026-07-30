package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

/** Écran hôte : en attente qu'un second téléphone se connecte à la partie publiée. */
@Composable
fun AttenteHoteScreen(
    nomServiceAffiche: String?,
    erreur: String?,
    onAnnulerErreur: () -> Unit,
    onAnnuler: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.attente_hote_titre), onAnnuler)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Text(stringResource(R.string.attente_hote_en_attente), style = MaterialTheme.typography.titleMedium)
            Afficheur(
                label = stringResource(R.string.attente_hote_partie_publiee),
                valeur = nomServiceAffiche ?: stringResource(R.string.attente_hote_valeur_provisoire),
                centre = true,
            )
            Text(
                stringResource(R.string.attente_hote_instructions),
                style = MaterialTheme.typography.bodySmall,
            )
            BoutonSecondaireContour(stringResource(R.string.action_annuler), onClick = onAnnuler)
        }
    }

    if (erreur != null) {
        AlertDialog(
            onDismissRequest = onAnnulerErreur,
            title = { Text(stringResource(R.string.attente_hote_connexion_impossible)) },
            text = { Text(erreur) },
            confirmButton = { TextButton(onClick = onAnnulerErreur) { Text(stringResource(R.string.action_ok)) } },
        )
    }
}
