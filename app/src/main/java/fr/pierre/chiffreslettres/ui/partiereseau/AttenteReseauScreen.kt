package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import kotlinx.coroutines.delay

/**
 * Écran générique d'attente, réutilisé pour "en attente de la configuration", "en attente que
 * l'adversaire déclenche la manche" et "en attente du résultat de l'adversaire" une fois mon
 * propre résultat envoyé. [secondesInitiales] affiche un chrono qui continue de défiler à partir
 * du temps restant au moment de ma validation (retour utilisateur : rester sur cet écran en
 * voyant le temps s'écouler plutôt qu'un indicateur figé) ; null si non applicable (attente avant
 * le début de la manche, pas de chrono à afficher).
 */
@Composable
fun AttenteReseauScreen(message: String, secondesInitiales: Int? = null) {
    var secondesRestantes by remember(secondesInitiales) { mutableStateOf(secondesInitiales) }
    LaunchedEffect(secondesInitiales) {
        while ((secondesRestantes ?: 0) > 0) {
            delay(1000)
            secondesRestantes = secondesRestantes?.minus(1)?.coerceAtLeast(0)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(message, style = MaterialTheme.typography.titleMedium)
        secondesRestantes?.let {
            Afficheur(stringResource(R.string.afficheur_temps), stringResource(R.string.afficheur_temps_valeur, it), centre = true)
        }
    }
}
