package fr.pierre.chiffreslettres.ui.duelmots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.GrilleMotsGroupee
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

private fun trie(mots: List<String>): List<String> =
    mots.distinct().sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))

/**
 * Résultats du duel mots, sous-mode Duo (retour utilisateur) : deux colonnes côte à côte (une
 * par joueur), triées par longueur décroissante puis ordre alphabétique, comme les mots
 * possibles ; ces derniers sont affichés en dessous une fois le résultat de l'adversaire reçu.
 */
@Composable
fun DuelMotsResultatsScreen(
    pseudoMoi: String,
    pseudoAdversaire: String,
    motsTrouvesMoi: List<String>,
    motsTrouvesAdversaire: List<String>,
    resultatAdversaireRecu: Boolean,
    motsPossibles: List<String>,
    peutRejouer: Boolean,
    onRetour: () -> Unit,
    onRejouer: () -> Unit,
) {
    val moiTries = trie(motsTrouvesMoi)
    val adversaireTries = trie(motsTrouvesAdversaire)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.duel_mots_titre))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PanneauResultat(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("$pseudoMoi (${moiTries.size})", style = MaterialTheme.typography.titleMedium)
                GrilleMotsGroupee(mots = moiTries, colonnes = 1) { mot -> Text(mot, color = TextMuted, fontSize = 13.sp) }
            }
            PanneauResultat(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (resultatAdversaireRecu) {
                    Text("$pseudoAdversaire (${adversaireTries.size})", style = MaterialTheme.typography.titleMedium)
                    GrilleMotsGroupee(mots = adversaireTries, colonnes = 1) { mot -> Text(mot, color = TextMuted, fontSize = 13.sp) }
                } else {
                    Text(pseudoAdversaire, style = MaterialTheme.typography.titleMedium)
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Text(stringResource(R.string.duel_mots_en_attente_adversaire), color = TextMuted, fontSize = 13.sp)
                }
            }
        }

        if (resultatAdversaireRecu) {
            PanneauResultat {
                val jaiGagne = moiTries.size >= adversaireTries.size
                Text(
                    stringResource(if (jaiGagne) R.string.duel_mots_victoire else R.string.duel_mots_defaite),
                    color = BrassBright,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (motsPossibles.isNotEmpty()) {
                PanneauResultat {
                    Text(stringResource(R.string.defi_mots_max_mots_possibles_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    GrilleMotsGroupee(mots = motsPossibles) { mot ->
                        val trouve = mot in moiTries || mot in adversaireTries
                        Text(mot, color = if (trouve) TextMuted else Ivory, fontSize = 13.sp)
                    }
                }
            }
            if (peutRejouer) {
                TuilePrincipale(stringResource(R.string.action_rejouer), onClick = onRejouer)
            } else {
                Text(stringResource(R.string.reseau_attente_nouvelle_partie), color = TextMuted, fontSize = 13.sp)
            }
            BoutonSecondaireContour(stringResource(R.string.action_retour), onClick = onRetour, modifier = Modifier.fillMaxWidth())
        }
    }
}
