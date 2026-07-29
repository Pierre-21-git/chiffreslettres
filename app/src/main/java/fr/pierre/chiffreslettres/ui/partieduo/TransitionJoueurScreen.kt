package fr.pierre.chiffreslettres.ui.partieduo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/** Résultat d'un joueur sur la manche en cours, à afficher sur l'écran de transition. */
data class ResultatAffichage(val pseudo: String, val score: Int, val detail: String)

/**
 * Écran de passation entre les deux joueurs (retour utilisateur) : affiché après chaque tour,
 * même si le même joueur enchaîne sur la manche suivante — avec le score et le détail (calcul
 * ou mot joué) de la manche qui vient de se terminer, en colonnes, un joueur par colonne au fur
 * et à mesure qu'ils jouent. [prochainPseudo] à null en toute fin de partie (plus personne à qui
 * passer le téléphone) : le bouton mène alors directement au récap final.
 */
@Composable
fun TransitionJoueurScreen(
    prochainPseudo: String?,
    resultats: List<ResultatAffichage>,
    onPret: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (resultats.isNotEmpty()) {
            Text("Résultat de la manche", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (resultat in resultats) {
                    ColonneResultat(resultat, modifier = Modifier.weight(1f))
                }
            }
        }

        if (prochainPseudo != null) {
            Text(
                "Passez le téléphone à",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                prochainPseudo,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            TuilePrincipale("Prêt", onClick = onPret)
        } else {
            TuilePrincipale("Voir les résultats", onClick = onPret)
        }
    }
}

@Composable
private fun ColonneResultat(resultat: ResultatAffichage, modifier: Modifier = Modifier) {
    PanneauResultat(modifier = modifier) {
        Text(resultat.pseudo, color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text("${resultat.score} pts", color = BrassBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(resultat.detail, color = TextMuted, fontSize = 13.sp)
    }
}
