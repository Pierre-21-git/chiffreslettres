package fr.pierre.chiffreslettres.ui.entrainement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChoixModeScreen(
    scoreCumule: Int,
    onChoixChiffres: () -> Unit,
    onChoixLettres: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text("Score cumulé : $scoreCumule", style = MaterialTheme.typography.titleMedium)
        Text("Entraînement libre", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onChoixChiffres) { Text("Chiffres") }
        Button(onClick = onChoixLettres) { Text("Lettres") }
    }
}
