package fr.pierre.chiffreslettres.ui.partieduo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

/** Même présentation que [fr.pierre.chiffreslettres.ui.partie.RecapPartieScreen], dupliquée pour les deux joueurs, avec le vainqueur en plus (retour utilisateur). */
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

        BlocJoueur(pseudo1, total1, resultats1)
        HorizontalDivider()
        BlocJoueur(pseudo2, total2, resultats2)

        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text("Terminer") }
    }
}

@Composable
private fun BlocJoueur(pseudo: String, total: Int, resultats: List<ResultatManche>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(pseudo, style = MaterialTheme.typography.titleMedium)
        Text("Score total : $total", style = MaterialTheme.typography.titleLarge)
        for ((index, resultat) in resultats.withIndex()) {
            val libelleMode = if (resultat.mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
            Text("Manche ${index + 1} ($libelleMode) : ${resultat.score} points")
        }
    }
}
