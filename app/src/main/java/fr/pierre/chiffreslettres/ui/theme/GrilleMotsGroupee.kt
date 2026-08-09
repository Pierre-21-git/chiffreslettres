package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R

private const val COLONNES_PAR_DEFAUT = 3

/**
 * Grille de mots répartie sur [colonnes] colonnes remplies colonne par colonne (la première
 * colonne intégralement avant la deuxième, etc., retour utilisateur), groupée par nombre de
 * lettres avec un titre "Mots de X lettres" entre chaque groupe. [mots] doit déjà être trié par
 * longueur décroissante puis ordre alphabétique (ex. via `dixMeilleursMots`/le tri du défi mots) :
 * cette fonction se contente de grouper par longueur et de répartir l'affichage, elle ne trie pas.
 */
@Composable
fun GrilleMotsGroupee(mots: List<String>, colonnes: Int = COLONNES_PAR_DEFAUT, rendu: @Composable (String) -> Unit) {
    val groupes = mots.groupBy { it.length }.toSortedMap(compareByDescending { it })
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        for ((longueur, motsGroupe) in groupes) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(R.string.grille_mots_titre_longueur, longueur),
                    color = TextMuted,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
                val nombreLignes = (motsGroupe.size + colonnes - 1) / colonnes
                for (ligne in 0 until nombreLignes) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (colonne in 0 until colonnes) {
                            val mot = motsGroupe.getOrNull(colonne * nombreLignes + ligne)
                            Box(modifier = Modifier.weight(1f)) {
                                if (mot != null) rendu(mot)
                            }
                        }
                    }
                }
            }
        }
    }
}
