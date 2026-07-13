package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class EntreeVersion(val version: String, val date: String, val changements: List<String>)

private val HISTORIQUE_VERSIONS = listOf(
    EntreeVersion(
        version = "1.0",
        date = "2026-07-13",
        changements = listOf(
            "Mode Chiffres et mode Lettres jouables en entraînement libre",
            "Partie structurée configurable (nombre de manches, niveaux)",
            "Profils joueurs, historique et statistiques",
            "Réglages (durée du chrono, gestion des profils) et écrans d'information",
        ),
    ),
)

@Composable
fun VersionsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Versions", style = MaterialTheme.typography.headlineSmall)
        for (entree in HISTORIQUE_VERSIONS) {
            Text("${entree.version} — ${entree.date}", style = MaterialTheme.typography.titleMedium)
            for (changement in entree.changements) {
                Text("• $changement")
            }
        }
    }
}
