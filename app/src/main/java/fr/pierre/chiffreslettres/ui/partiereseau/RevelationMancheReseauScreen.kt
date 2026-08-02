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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.ui.partieduo.ResultatAffichage
import fr.pierre.chiffreslettres.ui.partieduo.texteMeilleureReponse
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
    mode: ModeJeu,
    /** Meilleure réponse possible sur le tirage de cette manche, affichée une seule fois (retour utilisateur : même tirage pour les deux joueurs). Null si aucune réponse n'existe sur ce tirage. */
    meilleureReponse: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(stringResource(R.string.revelation_manche_titre), style = MaterialTheme.typography.titleMedium)
        Text(messageVainqueur(resultats), color = BrassBright, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            for (resultat in resultats) {
                ColonneResultat(resultat, modifier = Modifier.weight(1f))
            }
        }
        Text(texteMeilleureReponse(mode, meilleureReponse), color = TextMuted, fontSize = 13.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Afficheur(label = pseudoMoi, valeur = "$scoreMoi", modifier = Modifier.weight(1f), centre = true)
            Afficheur(label = pseudoAdversaire, valeur = "$scoreAdversaire", modifier = Modifier.weight(1f), centre = true)
        }
        TuilePrincipale(
            if (dernierManche) stringResource(R.string.revelation_voir_resultats) else stringResource(R.string.revelation_manche_suivante),
            onClick = onSuivant,
        )
    }
}

@Composable
private fun messageVainqueur(resultats: List<ResultatAffichage>): String {
    val vainqueur = resultats.singleOrNull { it.estVainqueur }
    return if (vainqueur != null) stringResource(R.string.revelation_manche_vainqueur, vainqueur.pseudo) else stringResource(R.string.revelation_manche_egalite)
}

@Composable
private fun ColonneResultat(resultat: ResultatAffichage, modifier: Modifier = Modifier) {
    PanneauResultat(modifier = modifier) {
        Text(resultat.pseudo, color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
        Text(
            if (resultat.estVainqueur) {
                stringResource(R.string.revelation_score_vainqueur, resultat.score)
            } else {
                stringResource(R.string.revelation_score, resultat.score)
            },
            color = if (resultat.estVainqueur) BrassBright else TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(resultat.detail, color = TextMuted, fontSize = 13.sp)
    }
}
