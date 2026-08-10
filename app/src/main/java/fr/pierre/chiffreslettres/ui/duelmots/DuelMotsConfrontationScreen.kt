package fr.pierre.chiffreslettres.ui.duelmots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.network.RaisonFinConfrontation
import fr.pierre.chiffreslettres.network.RaisonRejetMotDuelMots
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.GrilleMotsGroupee
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuileJeton
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

private const val LETTRES_PAR_LIGNE = 5

private fun trie(mots: List<String>): List<String> =
    mots.sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))

/**
 * Écran de jeu du duel mots, sous-mode Confrontation (retour utilisateur) : deux colonnes live
 * (moi/adversaire), course au premier à l'objectif de mots. Un mot invalide, trop court ou déjà
 * pris est signalé sans jamais faire perdre.
 */
@Composable
fun DuelMotsConfrontationScreen(
    pseudoMoi: String,
    pseudoAdversaire: String,
    couleurRang: Color?,
    lettresTirees: List<Char>,
    indicesUtilises: List<Int>,
    motSaisi: String,
    motRejete: String?,
    raisonRejet: RaisonRejetMotDuelMots?,
    seuilRequis: Int,
    objectifMots: Int,
    motsTrouvesMoi: List<String>,
    motsTrouvesAdversaire: List<String>,
    gagnant: Boolean?,
    tempsRestantSecondes: Int,
    motsPossibles: List<String>,
    raisonFin: RaisonFinConfrontation?,
    peutRejouer: Boolean,
    onCliquerLettre: (Int) -> Unit,
    onAnnulerLettre: () -> Unit,
    onEffacerMot: () -> Unit,
    onValider: () -> Unit,
    onRetour: () -> Unit,
    onRejouer: () -> Unit,
) {
    val termine = gagnant != null
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.duel_mots_titre), onRetour)
        PucePseudo(pseudoMoi, couleurRang = couleurRang)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Afficheur(
                pseudoMoi,
                stringResource(R.string.duel_mots_objectif_mots_progression, motsTrouvesMoi.size, objectifMots),
                modifier = Modifier.weight(1f),
                centre = true,
            )
            Afficheur(
                pseudoAdversaire,
                stringResource(R.string.duel_mots_objectif_mots_progression, motsTrouvesAdversaire.size, objectifMots),
                modifier = Modifier.weight(1f),
                centre = true,
            )
        }

        if (!termine) {
            Afficheur(
                stringResource(R.string.afficheur_temps),
                stringResource(R.string.afficheur_temps_valeur, tempsRestantSecondes),
                modifier = Modifier.fillMaxWidth(),
                centre = true,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = LETTRES_PAR_LIGNE,
            ) {
                for (index in lettresTirees.indices) {
                    val utilisee = index in indicesUtilises
                    TuileJeton(
                        texte = "${lettresTirees[index]}",
                        selectionne = false,
                        enabled = !utilisee,
                        monospace = false,
                        grand = true,
                        onClick = { onCliquerLettre(index) },
                    )
                }
            }

            Afficheur(
                stringResource(R.string.lettres_votre_mot),
                motSaisi.ifEmpty { stringResource(R.string.attente_hote_valeur_provisoire) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (motRejete != null) {
                val message = when (raisonRejet) {
                    RaisonRejetMotDuelMots.INVALIDE -> stringResource(R.string.defi_mots_max_fin_mot_invalide, motRejete)
                    RaisonRejetMotDuelMots.TROP_COURT -> stringResource(R.string.defi_mots_max_fin_mot_trop_court, motRejete, seuilRequis)
                    RaisonRejetMotDuelMots.DEJA_PRIS_MOI -> stringResource(R.string.duel_mots_deja_pris_moi, motRejete)
                    RaisonRejetMotDuelMots.DEJA_PRIS_ADVERSAIRE -> stringResource(R.string.duel_mots_deja_pris, motRejete)
                    null -> null
                }
                if (message != null) {
                    Text(message, color = TextMuted, fontSize = 13.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BoutonSecondaireContour(
                    stringResource(R.string.action_annuler),
                    onClick = onAnnulerLettre,
                    enabled = indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
                BoutonSecondaireContour(
                    stringResource(R.string.action_effacer),
                    onClick = onEffacerMot,
                    enabled = indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
            }
            TuilePrincipale(stringResource(R.string.action_valider), onClick = onValider)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ColonneMots(pseudoMoi, motsTrouvesMoi, Modifier.weight(1f))
            ColonneMots(pseudoAdversaire, motsTrouvesAdversaire, Modifier.weight(1f))
        }

        if (termine) {
            PanneauResultat {
                Text(
                    stringResource(if (gagnant == true) R.string.duel_mots_victoire else R.string.duel_mots_defaite),
                    color = BrassBright,
                    style = MaterialTheme.typography.titleMedium,
                )
                val explication = when (raisonFin) {
                    RaisonFinConfrontation.TEMPS_ECOULE -> stringResource(R.string.defi_mots_max_fin_temps_ecoule)
                    RaisonFinConfrontation.TOUS_MOTS_TROUVES -> stringResource(R.string.defi_mots_max_fin_tous_mots_trouves)
                    RaisonFinConfrontation.OBJECTIF_ATTEINT, null -> null
                }
                if (explication != null) {
                    Text(explication, color = TextMuted, fontSize = 13.sp)
                }
            }
            if (motsPossibles.isNotEmpty()) {
                PanneauResultat {
                    Text(stringResource(R.string.defi_mots_max_mots_possibles_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    GrilleMotsGroupee(mots = motsPossibles) { mot ->
                        val trouve = mot in motsTrouvesMoi || mot in motsTrouvesAdversaire
                        Text(
                            if (trouve) stringResource(R.string.defi_mots_max_mot_possible_trouve, mot) else mot,
                            color = if (trouve) TextMuted else Ivory,
                            fontSize = 13.sp,
                        )
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

@Composable
private fun ColonneMots(pseudo: String, mots: List<String>, modifier: Modifier) {
    PanneauResultat(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(pseudo, style = MaterialTheme.typography.labelLarge, color = TextMuted)
        for (mot in trie(mots)) {
            Text(mot, color = TextMuted, fontSize = 13.sp)
        }
    }
}
