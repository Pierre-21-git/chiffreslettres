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
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/**
 * Résultat d'un joueur sur la manche en cours, à afficher sur l'écran de transition, une fois que
 * les deux joueurs ont joué (retour utilisateur : jamais affiché avant, pour ne pas avantager le
 * second joueur qui verrait le score à battre).
 */
data class ResultatAffichage(val pseudo: String, val score: Int, val detail: String, val estVainqueur: Boolean = false)

/**
 * Écran de passation entre les deux joueurs (retour utilisateur) : affiché après chaque tour,
 * même si le même joueur enchaîne sur la manche suivante, ainsi qu'avant la toute première manche
 * pour annoncer qui commence ([resultats] est alors vide). [prochainPseudo] à null en toute fin
 * de partie (plus personne à qui passer le téléphone) : le bouton mène alors directement au récap
 * final. Ordre d'affichage (retour utilisateur) : l'action (passation + bouton "Prêt") d'abord, le
 * score de la partie en cours ensuite (toujours visible), puis le détail de la manche qui vient de
 * se terminer.
 */
@Composable
fun TransitionJoueurScreen(
    prochainPseudo: String?,
    resultats: List<ResultatAffichage>,
    pseudo1: String,
    pseudo2: String,
    scorePartie1: Int,
    scorePartie2: Int,
    onPret: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Afficheur(label = pseudo1, valeur = "$scorePartie1", modifier = Modifier.weight(1f), centre = true)
            Afficheur(label = pseudo2, valeur = "$scorePartie2", modifier = Modifier.weight(1f), centre = true)
        }

        if (resultats.isNotEmpty()) {
            Text("Résultat de la manche", style = MaterialTheme.typography.titleMedium)
            Text(messageVainqueur(resultats), color = BrassBright, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (resultat in resultats) {
                    ColonneResultat(resultat, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun messageVainqueur(resultats: List<ResultatAffichage>): String {
    val vainqueur = resultats.singleOrNull { it.estVainqueur }
    return if (vainqueur != null) "🏆 ${vainqueur.pseudo} remporte la manche" else "Égalité sur cette manche"
}

@Composable
private fun ColonneResultat(resultat: ResultatAffichage, modifier: Modifier = Modifier) {
    PanneauResultat(modifier = modifier) {
        Text(resultat.pseudo, color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text(
            if (resultat.estVainqueur) "🏆 ${resultat.score} pts" else "${resultat.score} pts",
            color = if (resultat.estVainqueur) BrassBright else TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(resultat.detail, color = TextMuted, fontSize = 13.sp)
    }
}
