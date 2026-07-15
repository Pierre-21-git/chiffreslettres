package fr.pierre.chiffreslettres.ui.entrainement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

/** Remplace l'ancien duo "choix du mode" + "choix du niveau" par une liste unique (retour utilisateur). */
@Composable
fun ChoixNiveauEntrainementScreen(
    onNiveauChiffresChoisi: (Niveau) -> Unit,
    onNiveauLettresChoisi: (NiveauLettres) -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Entraînement", onRetour)

        Text("Chiffres", style = MaterialTheme.typography.titleMedium)
        for (niveau in Niveau.entries) {
            Button(onClick = { onNiveauChiffresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) { Text(niveau.label) }
        }

        Text("Lettres", style = MaterialTheme.typography.titleMedium)
        for (niveau in NiveauLettres.entries) {
            Button(onClick = { onNiveauLettresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) { Text(niveau.label) }
        }
    }
}
