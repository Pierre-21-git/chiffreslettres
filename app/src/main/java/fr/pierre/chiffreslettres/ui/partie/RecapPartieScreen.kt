package fr.pierre.chiffreslettres.ui.partie

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
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun RecapPartieScreen(resultats: List<ResultatManche>, onTerminer: () -> Unit, onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnTeteEcran("Partie classique terminée", onRetour)
        Text("Score total : ${resultats.sumOf { it.score }}", style = MaterialTheme.typography.titleLarge)
        for ((index, resultat) in resultats.withIndex()) {
            val libelleMode = if (resultat.mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
            Text("Manche ${index + 1} ($libelleMode) : ${resultat.score} points")
        }
        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text("Terminer") }
    }
}
