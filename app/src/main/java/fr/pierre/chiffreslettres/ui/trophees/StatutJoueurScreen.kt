package fr.pierre.chiffreslettres.ui.trophees

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.Palier
import fr.pierre.chiffreslettres.data.libelleCourtRes
import fr.pierre.chiffreslettres.data.libelleJoueurRes
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.couleurPalier

/**
 * Page dédiée au statut joueur (retour utilisateur) : l'échelle complète des 8 paliers avec le
 * rang courant en évidence, puis les trophées encore manquants pour atteindre le rang suivant.
 * Accessible depuis le libellé de rang de [TropheesScreen] (`onVoirStatutJoueur`).
 */
@Composable
fun StatutJoueurScreen(
    tropheesDebloques: Map<String, Long>,
    onRetour: (() -> Unit)? = null,
) {
    val idsDebloques = tropheesDebloques.keys
    val rangActuel = CatalogueTrophees.rangJoueur(idsDebloques)
    val prochainPalier = Palier.entries.getOrNull((rangActuel?.ordinal ?: -1) + 1)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.statut_joueur_titre), onRetour)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(stringResource(R.string.statut_joueur_rang_actuel), style = MaterialTheme.typography.titleMedium)
            Text(
                if (rangActuel != null) stringResource(rangActuel.libelleJoueurRes) else stringResource(R.string.statut_joueur_aucun_rang),
                style = MaterialTheme.typography.headlineSmall,
                color = if (rangActuel != null) couleurPalier(rangActuel) else TextMuted,
            )
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.statut_joueur_echelle_titre), style = MaterialTheme.typography.titleMedium)
            for (palier in Palier.entries) {
                val atteint = rangActuel != null && palier.ordinal <= rangActuel.ordinal
                LignePalier(palier, atteint = atteint, actuel = palier == rangActuel)
            }
        }

        HorizontalDivider()

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (prochainPalier == null) {
                Text(stringResource(R.string.statut_joueur_rang_maximum), style = MaterialTheme.typography.titleMedium)
            } else {
                Text(
                    stringResource(R.string.statut_joueur_prochain_titre, stringResource(prochainPalier.libelleJoueurRes)),
                    style = MaterialTheme.typography.titleMedium,
                )
                val manquants = CatalogueTrophees.TOUS.filter { trophee ->
                    val palier = trophee.palier
                    palier != null && palier.ordinal <= prochainPalier.ordinal && trophee.id !in idsDebloques
                }
                if (manquants.isEmpty()) {
                    Text(stringResource(R.string.statut_joueur_prochain_aucun), color = TextMuted)
                } else {
                    for (trophee in manquants) {
                        Text(
                            "•  " + titreAffiche(trophee, debloque = false),
                            color = Ivory,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LignePalier(palier: Palier, atteint: Boolean, actuel: Boolean) {
    val couleur = couleurPalier(palier)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (actuel) PanelDeep else Color.Transparent)
            .border(if (actuel) 1.dp else 0.dp, couleur.copy(alpha = if (actuel) 0.6f else 0f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = if (actuel) 10.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(couleur.copy(alpha = if (atteint) 0.9f else 0.2f))
                .border(1.dp, couleur, CircleShape),
        )
        Text(
            stringResource(palier.libelleCourtRes),
            color = if (atteint) Ivory else TextMuted,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
