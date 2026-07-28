package fr.pierre.chiffreslettres.ui.partieduo

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
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun RecapPartieDuoScreen(
    pseudo1: String,
    pseudo2: String,
    resultats1: List<ResultatManche>,
    resultats2: List<ResultatManche>,
    onTerminer: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    val total1 = resultats1.sumOf { it.score }
    val total2 = resultats2.sumOf { it.score }
    val messageVainqueur = when {
        total1 > total2 -> "$pseudo1 remporte le duel !"
        total2 > total1 -> "$pseudo2 remporte le duel !"
        else -> "Match nul !"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnTeteEcran("Partie duo terminée", onRetour)
        Text(messageVainqueur, style = MaterialTheme.typography.titleLarge)
        Text("$pseudo1 : $total1 points", style = MaterialTheme.typography.titleMedium)
        Text("$pseudo2 : $total2 points", style = MaterialTheme.typography.titleMedium)

        for (index in resultats1.indices) {
            val r1 = resultats1[index]
            val r2 = resultats2.getOrNull(index) ?: continue
            val libelleMode = if (r1.mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
            Text("Manche ${index + 1} ($libelleMode) : $pseudo1 ${r1.score} pts — $pseudo2 ${r2.score} pts")
        }

        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text("Terminer") }
    }
}
