package fr.pierre.chiffreslettres.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Les autres sections du §9 (Jouer à 2, Réglages, À propos) arriveront en
 * phase 4/5.
 */
@Composable
fun MenuPrincipalScreen(
    pseudoActif: String,
    onEntrainementLibre: () -> Unit,
    onPartieStructuree: () -> Unit,
    onStatistiques: () -> Unit,
    onChangerProfil: () -> Unit,
    onReglages: () -> Unit,
    onAPropos: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text("Chiffres & Lettres", style = MaterialTheme.typography.headlineMedium)
        Text("Profil actif : $pseudoActif", style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = onChangerProfil) { Text("Changer de profil") }
        Button(onClick = onEntrainementLibre) { Text("Entraînement libre") }
        Button(onClick = onPartieStructuree) { Text("Partie structurée") }
        Button(onClick = onStatistiques) { Text("Statistiques") }
        OutlinedButton(onClick = onReglages) { Text("Réglages") }
        OutlinedButton(onClick = onAPropos) { Text("À propos") }
    }
}
