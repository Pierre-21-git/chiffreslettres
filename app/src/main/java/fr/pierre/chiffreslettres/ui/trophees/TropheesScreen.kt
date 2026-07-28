package fr.pierre.chiffreslettres.ui.trophees

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.CategorieTrophee
import fr.pierre.chiffreslettres.data.Palier
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.ui.statistiques.formatDate
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PalierArgent
import fr.pierre.chiffreslettres.ui.theme.PalierBronze
import fr.pierre.chiffreslettres.ui.theme.PalierDiamant
import fr.pierre.chiffreslettres.ui.theme.PalierPlatine
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted

private fun couleurPalier(palier: Palier): Color = when (palier) {
    Palier.BRONZE -> PalierBronze
    Palier.ARGENT -> PalierArgent
    Palier.OR -> BrassBright
    Palier.PLATINE -> PalierPlatine
    Palier.DIAMANT -> PalierDiamant
}

/**
 * Écran unique pour les deux points d'entrée (retour utilisateur) : liste catalogue depuis
 * "À propos" ([tropheesDebloques] = null, aucun état débloqué/verrouillé affiché) et fiche
 * d'un joueur depuis Statistiques > Joueurs ([tropheesDebloques] = trophyId -> date d'obtention).
 */
@Composable
fun TropheesScreen(
    titre: String,
    tropheesDebloques: Map<String, Long>?,
    onRetour: (() -> Unit)? = null,
) {
    var tropheeSelectionne by remember { mutableStateOf<Trophee?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(titre, onRetour)

        if (tropheesDebloques != null) {
            Text(
                "${tropheesDebloques.size} / ${CatalogueTrophees.TOUS.size} débloqués",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        for ((position, categorie) in CategorieTrophee.entries.withIndex()) {
            val tropheesCategorie = CatalogueTrophees.TOUS.filter { it.categorie == categorie }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(categorie.titre, style = MaterialTheme.typography.titleSmall)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    var sousTitrePrecedent: String? = null
                    for (trophee in tropheesCategorie) {
                        val sousTitre = trophee.sousTitre
                        if (sousTitre != null && sousTitre != sousTitrePrecedent) {
                            sousTitrePrecedent = sousTitre
                            Text(
                                sousTitre,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted,
                            )
                        }
                        val debloque = tropheesDebloques?.containsKey(trophee.id) == true
                        TuileTrophee(trophee, debloque, onClick = { tropheeSelectionne = trophee })
                    }
                }
            }
            if (position != CategorieTrophee.entries.lastIndex) HorizontalDivider()
        }
    }

    tropheeSelectionne?.let { trophee ->
        val date = tropheesDebloques?.get(trophee.id)
        AlertDialog(
            onDismissRequest = { tropheeSelectionne = null },
            title = { Text(trophee.titre) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(trophee.description)
                    if (tropheesDebloques != null) {
                        Text(
                            if (date != null) "Obtenu le ${formatDate(date)}" else "Pas encore obtenu",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { tropheeSelectionne = null }) { Text("Fermer") }
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
                .background(couleur.copy(alpha = 0.25f))
                .border(1.5.dp, couleur, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("🏆", fontSize = 18.sp)
        }
        Text(
            trophee.titre,
            color = if (debloque) Ivory else TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
        )
    }
}
