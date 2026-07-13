package fr.pierre.chiffreslettres.ui.entrainement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.numbers.Niveau

@Composable
fun ChoixNiveauChiffresScreen(scoreCumule: Int, onNiveauChoisi: (Niveau) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Score cumulé : $scoreCumule", style = MaterialTheme.typography.titleMedium)
        Text("Choisir un niveau", style = MaterialTheme.typography.headlineSmall)
        for (niveau in Niveau.entries) {
            Button(onClick = { onNiveauChoisi(niveau) }) { Text(niveau.label) }
        }
    }
}
