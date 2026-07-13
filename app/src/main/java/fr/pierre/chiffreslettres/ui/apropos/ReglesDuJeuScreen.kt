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

@Composable
fun ReglesDuJeuScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Règles du jeu", style = MaterialTheme.typography.headlineSmall)

        Text("Mode Chiffres", style = MaterialTheme.typography.titleMedium)
        Text(
            "6 nombres sont tirés (parmi 1 à 10 en double exemplaire, et " +
                "25/50/75/100 en simple exemplaire), ainsi qu'une cible à atteindre. " +
                "Construisez votre calcul pas à pas façon calculatrice : touchez un " +
                "premier nombre, un opérateur, puis un second nombre — le résultat " +
                "remplace les deux nombres utilisés et peut resservir. Le bouton " +
                "\"Annuler\" revient sur la dernière opération. Validez dès que vous " +
                "voulez proposer votre résultat, ou laissez le chrono s'écouler. Le " +
                "score dépend de l'écart avec la cible : 10 points si le compte est " +
                "exact, moins sinon.",
        )

        Text("Mode Lettres", style = MaterialTheme.typography.titleMedium)
        Text(
            "9 lettres sont tirées une à une : à chaque tirage, choisissez " +
                "\"Consonne\" ou \"Voyelle\" (le Y compte comme une voyelle). " +
                "L'application impose toujours au moins 2 voyelles parmi les 9 " +
                "lettres. Une fois le tirage terminé, trouvez le mot le plus long " +
                "possible avec ces lettres avant la fin du chrono. Le score " +
                "correspond au nombre de lettres du mot validé.",
        )

        Text("Mode 2 joueurs", style = MaterialTheme.typography.titleMedium)
        Text("À venir : jeu en local entre deux appareils, tirage partagé en temps réel.")
    }
}
