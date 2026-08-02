package fr.pierre.chiffreslettres.ui.trophees

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ArgRes
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.CategorieTrophee
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.data.TropheeStats
import fr.pierre.chiffreslettres.data.libelleCourtRes
import fr.pierre.chiffreslettres.data.libelleJoueurRes
import fr.pierre.chiffreslettres.ui.statistiques.formatDate
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.couleurPalier

/**
 * Écran unique pour les deux points d'entrée (retour utilisateur) : liste catalogue depuis
 * "À propos" ([tropheesDebloques] = null, aucun état débloqué/verrouillé affiché) et fiche
 * d'un joueur depuis Statistiques > Joueurs ([tropheesDebloques] = trophyId -> date d'obtention).
 */
/** Résout un titre/description de trophée : substitue les [ArgRes] (nom de mode, etc.) par leur texte avant le format. */
@Composable
private fun texteTrophee(res: Int, args: List<Any>): String {
    val argsResolus = args.map { if (it is ArgRes) stringResource(it.res) else it }
    return stringResource(res, *argsResolus.toTypedArray())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TropheesScreen(
    titre: String,
    tropheesDebloques: Map<String, Long>?,
    onRetour: (() -> Unit)? = null,
    /** Stats du joueur (retour utilisateur : affiche "X / objectif" dans le détail d'un trophée non débloqué), null en consultation du catalogue seul. */
    stats: TropheeStats? = null,
) {
    var tropheeSelectionne by remember { mutableStateOf<Trophee?>(null) }
    // Lien masquer/afficher (retour utilisateur), pertinent seulement en consultation des
    // trophées d'un joueur ([tropheesDebloques] non null) : pas de notion débloqué/verrouillé
    // en simple consultation de catalogue.
    var masquerObtenus by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // stickyHeader (retour utilisateur : le titre doit rester visible en scrollant) : fond
        // opaque nécessaire, sans quoi le contenu qui défile serait visible en transparence
        // derrière le titre une fois celui-ci épinglé en haut.
        stickyHeader {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                EnTeteEcran(titre, onRetour)
                if (tropheesDebloques != null) {
                    Text(
                        stringResource(if (masquerObtenus) R.string.trophees_afficher_obtenus else R.string.trophees_masquer_obtenus),
                        modifier = Modifier.fillMaxWidth().clickable { masquerObtenus = !masquerObtenus },
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }

        if (tropheesDebloques != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(R.string.trophees_debloques_compteur, tropheesDebloques.size, CatalogueTrophees.TOUS.size),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    val rang = CatalogueTrophees.rangJoueur(tropheesDebloques.keys)
                    if (rang != null) {
                        Text(
                            stringResource(rang.libelleJoueurRes),
                            style = MaterialTheme.typography.titleSmall,
                            color = couleurPalier(rang),
                        )
                    }
                }
            }
        }

        val categoriesAffichees = CategorieTrophee.entries.mapNotNull { categorie ->
            val tropheesCategorie = CatalogueTrophees.TOUS.filter { it.categorie == categorie }
                .let { liste ->
                    if (masquerObtenus && tropheesDebloques != null) {
                        liste.filterNot { tropheesDebloques.containsKey(it.id) }
                    } else {
                        liste
                    }
                }
            if (tropheesCategorie.isEmpty()) null else categorie to tropheesCategorie
        }
        for ((position, entry) in categoriesAffichees.withIndex()) {
            val (categorie, tropheesCategorie) = entry
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(categorie.titreRes), style = MaterialTheme.typography.titleSmall)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        var sousTitrePrecedentRes: Int? = null
                        for (trophee in tropheesCategorie) {
                            val sousTitreRes = trophee.sousTitreRes
                            if (sousTitreRes != null && sousTitreRes != sousTitrePrecedentRes) {
                                sousTitrePrecedentRes = sousTitreRes
                                Text(
                                    stringResource(sousTitreRes),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextMuted,
                                )
                            }
                            val debloque = tropheesDebloques?.containsKey(trophee.id) == true
                            TuileTrophee(trophee, debloque, onClick = { tropheeSelectionne = trophee })
                        }
                    }
                }
            }
            if (position != categoriesAffichees.lastIndex) {
                item { HorizontalDivider() }
            }
        }
    }

    tropheeSelectionne?.let { trophee ->
        val date = tropheesDebloques?.get(trophee.id)
        val objectif = trophee.objectif
        val progression = trophee.progression
        AlertDialog(
            onDismissRequest = { tropheeSelectionne = null },
            title = { Text(texteTrophee(trophee.titreRes, trophee.titreArgs)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(trophee.palier.libelleCourtRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = couleurPalier(trophee.palier),
                    )
                    Text(texteTrophee(trophee.descriptionRes, trophee.descriptionArgs))
                    if (date == null && objectif != null && progression != null && stats != null) {
                        Text(
                            stringResource(R.string.trophees_progression, progression(stats).coerceAtMost(objectif), objectif),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextMuted,
                        )
                    }
                    if (tropheesDebloques != null) {
                        Text(
                            if (date != null) stringResource(R.string.trophees_obtenu_le, formatDate(date)) else stringResource(R.string.trophees_pas_encore_obtenu),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { tropheeSelectionne = null }) { Text(stringResource(R.string.action_fermer)) }
            },
        )
    }
}

@Composable
private fun TuileTrophee(trophee: Trophee, debloque: Boolean, onClick: () -> Unit) {
    val couleur = couleurPalier(trophee.palier)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = if (debloque) 1f else 0.3f)
            .clip(RoundedCornerShape(8.dp))
            .background(PanelDeep)
            .border(1.dp, Ivory.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(couleur.copy(alpha = 0.35f))
                .border(2.dp, couleur, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("🏆", fontSize = 18.sp)
        }
        Text(
            texteTrophee(trophee.titreRes, trophee.titreArgs),
            color = if (debloque) Ivory else TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
        )
    }
}
