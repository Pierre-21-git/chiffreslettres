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
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.BuildConfig
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun AProposScreen(onReglesDuJeu: () -> Unit, onVersions: () -> Unit, onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("À propos", onRetour)
        Text(
            "Chiffres & Lettres — version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.titleMedium,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Dictionnaire français : Dicollecte (hunspell-fr), licence MPL-2.0.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Bibliothèques open source utilisées : AndroidX Jetpack Compose, " +
                    "Room, Navigation (licence Apache 2.0).",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onReglesDuJeu, modifier = Modifier.fillMaxWidth()) { Text("Règles du jeu") }
            Button(onClick = onVersions, modifier = Modifier.fillMaxWidth()) { Text("Versions") }
        }
    }
}
