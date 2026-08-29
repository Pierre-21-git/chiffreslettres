package fr.pierre.chiffreslettres.ui.defi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.BaremeLettres
import fr.pierre.chiffreslettres.letters.ObjectifPoints
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.GrilleMotsGroupee
import fr.pierre.chiffreslettres.ui.theme.TuileJeton
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

private const val LETTRES_PAR_LIGNE = 5

/**
 * Défi Points (retour utilisateur) : même structure que `DefiMotsMaxScreen`, avec la liste des
 * objectifs de points en haut, la valeur en points sur chaque tuile de lettre, et le score du mot
 * en cours de saisie affiché en direct.
 */
@Composable
fun DefiObjectifsPointsScreen(
    viewModel: DefiObjectifsPointsViewModel,
    pseudo: String?,
    couleurRang: Color?,
    onRetour: (() -> Unit)?,
    actionsFin: @Composable () -> Unit,
) {
    val etat by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.defi_type_points), onRetour)
        if (pseudo != null) {
            PucePseudo(pseudo, couleurRang = couleurRang)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Afficheur(
                stringResource(R.string.defi_points_objectifs_titre),
                "${etat.objectifs.count { it.atteint }}/${etat.objectifs.size}",
                modifier = Modifier.weight(1f),
                centre = true,
            )
            Afficheur(
                label = if (!etat.tirageTermine) stringResource(R.string.lettres_tirage_label) else stringResource(R.string.afficheur_temps),
                valeur = if (!etat.tirageTermine) {
                    stringResource(R.string.lettres_tirage_valeur, etat.lettresTirees.size, etat.nombreLettres)
                } else {
                    stringResource(R.string.afficheur_temps_valeur, etat.tempsRestantSecondes)
                },
                modifier = Modifier.weight(1f),
                centre = true,
            )
        }

        if (etat.objectifs.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (objectif in etat.objectifs) {
                    PuceObjectifPoints(objectif)
                }
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = LETTRES_PAR_LIGNE,
        ) {
            for (index in 0 until etat.nombreLettres) {
                val lettre = etat.lettresTirees.getOrNull(index)
                if (lettre == null) {
                    Spacer(Modifier.size(56.dp, 60.dp))
                } else {
                    val utilisee = index in etat.indicesUtilises
                    TuileJeton(
                        texte = "$lettre",
                        selectionne = false,
                        enabled = etat.tirageTermine && !etat.termine && !utilisee,
                        monospace = false,
                        grand = true,
                        tresGrand = true,
                        points = BaremeLettres.valeurLettre(lettre, etat.bareme),
                        onClick = { viewModel.cliquerLettre(index) },
                    )
                }
            }
        }

        Afficheur(
            stringResource(R.string.lettres_votre_mot),
            etat.motSaisi.ifEmpty { stringResource(R.string.attente_hote_valeur_provisoire) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (etat.motSaisi.isNotEmpty()) {
            Text(
                stringResource(R.string.defi_points_score_en_cours, etat.scoreMotSaisi),
                color = TextMuted,
                fontSize = 13.sp,
            )
        }

        val motRejete = etat.motRejeteTransitoire
        if (motRejete != null && !etat.termine) {
            val message = when (etat.raisonRejetTransitoire) {
                RaisonRejetMotDefiObjectifsPoints.INVALIDE ->
                    stringResource(R.string.defi_mots_max_fin_mot_invalide, motRejete)
                RaisonRejetMotDefiObjectifsPoints.SCORE_SANS_OBJECTIF ->
                    stringResource(R.string.defi_points_rejet_score_sans_objectif, motRejete, BaremeLettres.scoreMot(motRejete, etat.bareme))
                null -> null
            }
            if (message != null) {
                Text(message, color = TextMuted, fontSize = 13.sp)
            }
        }

        if (!etat.tirageTermine) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.lettres_nombre_voyelles), color = TextMuted, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (n in TirageLettres.VOYELLES_MINIMUM..TirageLettres.VOYELLES_MAXIMUM) {
                        TuilePrincipale(
                            "$n",
                            onClick = { viewModel.choisirNombreVoyelles(n) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BoutonSecondaireContour(
                    stringResource(R.string.action_annuler),
                    onClick = { viewModel.annulerLettre() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
                BoutonSecondaireContour(
                    stringResource(R.string.action_effacer),
                    onClick = { viewModel.effacerMot() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
            }
            TuilePrincipale(stringResource(R.string.action_valider), onClick = { viewModel.valider() }, enabled = !etat.termine)
        }

        if (etat.motsTrouves.isNotEmpty()) {
            PanneauResultat {
                Text(stringResource(R.string.defi_points_mots_trouves_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (mot in etat.motsTrouves) {
                        Text(mot, color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        }

        if (etat.termine) {
            PanneauResultat {
                Text(
                    stringResource(R.string.defi_points_recap_score, etat.objectifs.count { it.atteint }, etat.objectifs.size),
                    color = BrassBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                val explication = when (etat.raisonFin) {
                    RaisonFinDefiObjectifsPoints.TEMPS_ECOULE ->
                        stringResource(R.string.defi_mots_max_fin_temps_ecoule)
                    RaisonFinDefiObjectifsPoints.TOUS_OBJECTIFS_ATTEINTS ->
                        stringResource(R.string.defi_points_fin_tous_objectifs_atteints)
                    RaisonFinDefiObjectifsPoints.VOLONTAIRE, null -> null
                }
                if (explication != null) {
                    Text(explication, color = TextMuted, fontSize = 13.sp)
                }
            }
            if (etat.motsPossibles.isNotEmpty()) {
                PanneauResultat {
                    Text(stringResource(R.string.defi_mots_max_mots_possibles_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    GrilleMotsGroupee(mots = etat.motsPossibles) { mot ->
                        val trouve = mot in etat.motsTrouves
                        Text(
                            if (trouve) stringResource(R.string.defi_mots_max_mot_possible_trouve, mot) else mot,
                            color = if (trouve) TextMuted else Ivory,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
            actionsFin()
        }
    }
}

@Composable
private fun PuceObjectifPoints(objectif: ObjectifPoints) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (objectif.atteint) BrassBright.copy(alpha = 0.3f) else PanelDeep)
            .border(1.dp, if (objectif.atteint) BrassBright else Ivory.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            stringResource(R.string.revelation_score, objectif.points),
            color = if (objectif.atteint) BrassBright else Ivory,
            fontWeight = if (objectif.atteint) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}
