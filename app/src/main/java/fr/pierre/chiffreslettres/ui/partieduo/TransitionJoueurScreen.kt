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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.numbers.Expression
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.Ivory
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
    /** Mode de la manche qui vient de se terminer ; null si [resultats] est vide. */
    mode: ModeJeu? = null,
    /** Meilleur mot possible sur le tirage de cette manche (mode Lettres uniquement), affiché une seule fois (retour utilisateur : même tirage pour les deux joueurs). */
    meilleurMot: String? = null,
    /** Solution possible sur le tirage de cette manche (mode Chiffres uniquement), affichée une seule fois, détail étape par étape comme en solo. */
    solutionPossible: Expression? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (prochainPseudo != null) {
            Text(
                stringResource(R.string.transition_passez_telephone),
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
            TuilePrincipale(stringResource(R.string.transition_pret), onClick = onPret)
        } else {
            TuilePrincipale(stringResource(R.string.revelation_voir_resultats), onClick = onPret)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Afficheur(label = pseudo1, valeur = "$scorePartie1", modifier = Modifier.weight(1f), centre = true)
            Afficheur(label = pseudo2, valeur = "$scorePartie2", modifier = Modifier.weight(1f), centre = true)
        }

        if (resultats.isNotEmpty()) {
            Text(stringResource(R.string.revelation_manche_titre), style = MaterialTheme.typography.titleMedium)
            Text(messageVainqueur(resultats), color = BrassBright, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (resultat in resultats) {
                    ColonneResultat(resultat, modifier = Modifier.weight(1f))
                }
            }
            if (mode != null) {
                PanneauMeilleureReponse(mode, meilleurMot, solutionPossible)
            }
        }
    }
}

@Composable
private fun messageVainqueur(resultats: List<ResultatAffichage>): String {
    val vainqueur = resultats.singleOrNull { it.estVainqueur }
    return if (vainqueur != null) stringResource(R.string.revelation_manche_vainqueur, vainqueur.pseudo) else stringResource(R.string.revelation_manche_egalite)
}

/**
 * Même contenu que la révélation en solo (`ChiffresRoundScreen`/`LettresRoundScreen`), affiché
 * une seule fois par manche ici (retour utilisateur : pas par joueur, le tirage est commun). En
 * chiffres, une opération par ligne quand la solution en compte plusieurs (retour utilisateur),
 * comme en solo.
 */
@Composable
internal fun PanneauMeilleureReponse(mode: ModeJeu, meilleurMot: String?, solutionPossible: Expression?) {
    PanneauResultat {
        when (mode) {
            ModeJeu.LETTRES -> Text(
                stringResource(
                    R.string.lettres_meilleur_mot_trouve,
                    meilleurMot?.let { stringResource(R.string.lettres_meilleur_mot_detail, it, it.length) }
                        ?: stringResource(R.string.lettres_meilleur_mot_aucun),
                ),
                color = TextMuted,
                fontSize = 13.sp,
            )
            ModeJeu.CHIFFRES -> {
                val etapesSolution = solutionPossible?.etapes().orEmpty()
                if (etapesSolution.isEmpty()) {
                    Text(
                        stringResource(
                            R.string.chiffres_solution_possible_ligne,
                            solutionPossible?.texte() ?: stringResource(R.string.chiffres_solution_aucune),
                        ),
                        color = TextMuted,
                        fontSize = 13.sp,
                    )
                } else {
                    Text(stringResource(R.string.chiffres_solution_possible_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    for (ligne in etapesSolution) {
                        Text(ligne, color = Ivory, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    }
                }
            }
        }
    }
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
