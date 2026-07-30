package fr.pierre.chiffreslettres.ui.partiereseau

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.ui.partieduo.ResultatAffichage
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

/** Révélation du résultat d'une manche réseau, affichée une fois que les 2 joueurs ont fini. */
@Composable
fun RevelationMancheReseauScreen(
    resultats: List<ResultatAffichage>,
    pseudoMoi: String,
    pseudoAdversaire: String,
    scoreMoi: Int,
    scoreAdversaire: Int,
    dernierManche: Boolean,
    onSuivant: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("Résultat de la manche", style = MaterialTheme.typography.titleMedium)
        Text(messageVainqueur(resultats), color = BrassBright, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for (resultat in resultats) {
                ColonneResultat(resultat, modifier = Modifier.weight(1f))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Afficheur(label = pseudoMoi, valeur = "$scoreMoi", modifier = Modifier.weight(1f), centre = true)
            Afficheur(label = pseudoAdversaire, valeur = "$scoreAdversaire", modifier = Modifier.weight(1f), centre = true)
        }
        TuilePrincipale(if (dernierManche) "Voir les résultats" else "Manche suivante", onClick = onSuivant)
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
