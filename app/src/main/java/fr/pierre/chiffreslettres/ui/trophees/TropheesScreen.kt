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
import fr.pierre.chiffreslettres.data.NiveauVisibilite
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.data.TropheeStats
import fr.pierre.chiffreslettres.data.UniteProgression
import fr.pierre.chiffreslettres.data.libelleCourtRes
import fr.pierre.chiffreslettres.data.libelleJoueurRes
import fr.pierre.chiffreslettres.ui.statistiques.formatDate
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.PalierEasterEgg
import fr.pierre.chiffreslettres.ui.theme.couleurPalier

/**
 * Écran unique pour les deux points d'entrée (retour utilisateur) : liste catalogue depuis
 * "À propos" ([tropheesDebloques] = null, aucun état débloqué/verrouillé affiché) et fiche
 * d'un joueur depuis Statistiques > Joueurs ([tropheesDebloques] = trophyId -> date d'obtention).
 */
/** Résout un titre/description de trophée : substitue les [ArgRes] (nom de mode, etc.) par leur texte avant le format. */
@Composable
internal fun texteTrophee(res: Int, args: List<Any>): String {
    val argsResolus = args.map { if (it is ArgRes) stringResource(it.res) else it }
    return stringResource(res, *argsResolus.toTypedArray())
}

/**
 * Titre affiché d'un trophée : masqué en "???????" tant qu'un trophée INVISIBLE (5 easter eggs
 * secrets, retour utilisateur) n'est pas débloqué — y compris en simple consultation du
 * catalogue, où [debloque] est toujours faux. Une fois débloqué, toujours le vrai titre.
 */
@Composable
internal fun titreAffiche(trophee: Trophee, debloque: Boolean): String =
    if (!debloque && trophee.niveauVisibilite == NiveauVisibilite.INVISIBLE) {
        stringResource(R.string.trophee_titre_cache)
    } else {
        texteTrophee(trophee.titreRes, trophee.titreArgs)
    }

/**
 * Texte "X / objectif" affiché dans le détail d'un trophée non débloqué (retour utilisateur) :
 * [UniteProgression.NOMBRE] affiche les valeurs brutes, [UniteProgression.DUREE] les formate en
 * heures/minutes/secondes (ex. "12h 30min 46s / 100h") — [valeur] et [objectif] sont alors
 * exprimés en secondes.
 */
@Composable
private fun texteProgression(valeur: Int, objectif: Int, unite: UniteProgression): String = when (unite) {
    UniteProgression.NOMBRE -> stringResource(R.string.trophees_progression, valeur, objectif)
    UniteProgression.DUREE -> stringResource(
        R.string.trophees_progression_duree,
        valeur / 3600,
        (valeur % 3600) / 60,
        valeur % 60,
        objectif / 3600,
    )
}

/** Un "grand bloc" de l'écran (Parties et duels / Défis / Secrets), avant filtrage. */
private data class GrandBloc(val titreRes: Int, val categories: List<CategorieTrophee>, val meta: Trophee?)

/** Même chose après filtrage (masquerObtenus, catégories vides retirées). */
private data class BlocAffiche(
    val titreRes: Int,
    val categories: List<Pair<CategorieTrophee, List<Trophee>>>,
    val meta: Trophee?,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TropheesScreen(
    titre: String,
    tropheesDebloques: Map<String, Long>?,
    onRetour: (() -> Unit)? = null,
    /** Stats du joueur (retour utilisateur : affiche "X / objectif" dans le détail d'un trophée non débloqué), null en consultation du catalogue seul. */
    stats: TropheeStats? = null,
    /** Navigation vers la page dédiée du statut joueur (retour utilisateur), null en consultation du catalogue seul (pas de rang affiché dans ce cas). */
    onVoirStatutJoueur: (() -> Unit)? = null,
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
                    val texteRang = if (rang != null) stringResource(rang.libelleJoueurRes) else stringResource(R.string.statut_joueur_titre)
                    Text(
                        texteRang,
                        modifier = if (onVoirStatutJoueur != null) Modifier.clickable(onClick = onVoirStatutJoueur) else Modifier,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (rang != null) couleurPalier(rang) else TextMuted,
                    )
                }
            }
        }

        // Trois "grands blocs" (retour utilisateur) au-dessus des sous-titres de catégorie déjà
        // existants : Parties et duels, Défis, Secrets (easter eggs) — dans cet ordre.
        // Les méta-trophées "Maître des parties"/"Maître des défis" (catégorie TROPHEES_SPECIAUX,
        // condition pilotée par TropheeRepository.reevaluer) sont rattachés manuellement en tout
        // dernier de leur bloc plutôt que rendus via leur propre catégorie.
        val metaPartie = CatalogueTrophees.TOUS.first { it.id == "section_partie_complete" }
        val metaDefi = CatalogueTrophees.TOUS.first { it.id == "section_defi_complete" }
        val grandsBlocs: List<GrandBloc> = listOf(
            GrandBloc(
                R.string.trophees_section_parties_duels,
                CategorieTrophee.entries.filter { it in CatalogueTrophees.CATEGORIES_SECTION_PARTIE },
                metaPartie,
            ),
            GrandBloc(
                R.string.trophees_section_defis,
                CategorieTrophee.entries.filter { it in CatalogueTrophees.CATEGORIES_SECTION_DEFI },
                metaDefi,
            ),
            GrandBloc(
                CategorieTrophee.TROPHEES_SPECIAUX.titreRes,
                CategorieTrophee.entries.filter {
                    it !in CatalogueTrophees.CATEGORIES_SECTION_PARTIE &&
                        it !in CatalogueTrophees.CATEGORIES_SECTION_DEFI &&
                        it != CategorieTrophee.TROPHEES_SPECIAUX
                },
                null,
            ),
        )

        fun tropheesAffiches(categorie: CategorieTrophee) = CatalogueTrophees.TOUS.filter { it.categorie == categorie }
            .let { liste ->
                if (masquerObtenus && tropheesDebloques != null) {
                    liste.filterNot { tropheesDebloques.containsKey(it.id) }
                } else {
                    liste
                }
            }

        val blocsAffiches = grandsBlocs.mapNotNull { grandBloc ->
            val categoriesNonVides = grandBloc.categories.mapNotNull { categorie ->
                val tropheesCategorie = tropheesAffiches(categorie)
                if (tropheesCategorie.isEmpty()) null else categorie to tropheesCategorie
            }
            val meta = grandBloc.meta
            val metaAffiche = meta != null && !(masquerObtenus && tropheesDebloques?.containsKey(meta.id) == true)
            if (categoriesNonVides.isEmpty() && !metaAffiche) null else BlocAffiche(grandBloc.titreRes, categoriesNonVides, if (metaAffiche) meta else null)
        }

        for ((positionBloc, bloc) in blocsAffiches.withIndex()) {
            val (titreBlocRes, categoriesNonVides, meta) = bloc
            item {
                Text(stringResource(titreBlocRes), style = MaterialTheme.typography.titleLarge, color = Ivory)
            }
            for ((categorie, tropheesCategorie) in categoriesNonVides) {
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
            }
            if (meta != null) {
                item {
                    val debloque = tropheesDebloques?.containsKey(meta.id) == true
                    TuileTrophee(meta, debloque, onClick = { tropheeSelectionne = meta })
                }
            }
            if (positionBloc != blocsAffiches.lastIndex) {
                item { HorizontalDivider() }
            }
        }
    }

    tropheeSelectionne?.let { trophee ->
        val date = tropheesDebloques?.get(trophee.id)
        val objectif = trophee.objectif
        val progression = trophee.progression
        // Description avant déblocage (retour utilisateur, easter eggs) : tant qu'un trophée
        // SEMI_CACHE/INVISIBLE n'est pas débloqué, on affiche la description vague plutôt que la
        // vraie (qui dévoilerait le secret). Une fois débloqué, toujours la vraie description.
        val descriptionAvantDeblocageRes = trophee.descriptionAvantDeblocageRes
        val afficherDescriptionAvantDeblocage =
            date == null && trophee.niveauVisibilite != NiveauVisibilite.VISIBLE && descriptionAvantDeblocageRes != null
        AlertDialog(
            onDismissRequest = { tropheeSelectionne = null },
            title = { Text(titreAffiche(trophee, date != null)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Un easter egg (palier == null) n'affiche pas de badge de palier : ce n'est
                    // pas un jalon de la progression Bronze→Diamant (retour utilisateur).
                    trophee.palier?.let { palier ->
                        Text(
                            stringResource(palier.libelleCourtRes),
                            style = MaterialTheme.typography.labelLarge,
                            color = couleurPalier(palier),
                        )
                    }
                    if (afficherDescriptionAvantDeblocage) {
                        Text(stringResource(descriptionAvantDeblocageRes))
                    } else {
                        Text(texteTrophee(trophee.descriptionRes, trophee.descriptionArgs))
                    }
                    if (date == null && objectif != null && progression != null && stats != null) {
                        Text(
                            texteProgression(progression(stats).coerceAtMost(objectif), objectif, trophee.uniteProgression),
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
    // Un easter egg (palier == null) prend une couleur neutre dédiée plutôt qu'une couleur de
    // palier (retour utilisateur : ce n'est pas un jalon Bronze→Diamant).
    val couleur = trophee.palier?.let { couleurPalier(it) } ?: PalierEasterEgg
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
            Text(CatalogueTrophees.iconeTrophee(trophee.id), fontSize = 18.sp)
        }
        Text(
            titreAffiche(trophee, debloque),
            color = if (debloque) Ivory else TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
        )
    }
}
