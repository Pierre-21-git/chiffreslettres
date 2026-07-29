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
import androidx.compose.ui.unit.dp
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
        EnTeteEcran("Héberger une partie", onAnnuler)
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Text("En attente d'un adversaire…", style = MaterialTheme.typography.titleMedium)
            Afficheur(label = "Partie publiée", valeur = nomServiceAffiche ?: "…", centre = true)
            Text(
                "L'autre téléphone doit être sur le même réseau Wifi et choisir « Rejoindre une partie ».",
                style = MaterialTheme.typography.bodySmall,
            )
            BoutonSecondaireContour("Annuler", onClick = onAnnuler)
        }
    }

    if (erreur != null) {
        AlertDialog(
            onDismissRequest = onAnnulerErreur,
            title = { Text("Connexion impossible") },
            text = { Text(erreur) },
            confirmButton = { TextButton(onClick = onAnnulerErreur) { Text("OK") } },
        )
    }
}
