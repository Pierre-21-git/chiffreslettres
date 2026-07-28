package fr.pierre.chiffreslettres.ui.partieduo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/**
 * Écran de passation entre les deux joueurs (retour utilisateur) : affiché une seule fois par
 * manche, juste avant le second joueur (le premier garde le téléphone en main depuis la manche
 * précédente ou depuis la configuration — pas besoin de passation dans son cas).
 */
@Composable
fun TransitionJoueurScreen(pseudo: String, onPret: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Text(
            "Passez le téléphone à",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            pseudo,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        TuilePrincipale("Prêt", onClick = onPret)
    }
}
