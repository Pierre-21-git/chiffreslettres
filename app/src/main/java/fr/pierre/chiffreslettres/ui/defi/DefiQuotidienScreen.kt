package fr.pierre.chiffreslettres.ui.defi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.TirageDefiQuotidien
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TextMuted

/** Paliers de trophées de série de jours (cf. CatalogueTrophees), pour l'affichage de la progression. */
private val PALIERS_SERIE_JOURS = listOf(7 to "Une semaine de défi quotidien", 30 to "Un mois de défi quotidien")

@Composable
fun DefiQuotidienScreen(
    pseudoActif: String,
    tirage: TirageDefiQuotidien,
    dejaReussiAujourdhui: Boolean,
    serieActuelle: Int,
    onNiveauChiffresChoisi: (Niveau) -> Unit,
    onNiveauLettresChoisi: (NiveauLettres) -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    val nomMode = if (tirage.mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
    val nomType = if (tirage.type == TypeDefi.SERIE) "Défi série" else "Défi chrono"
    val natureObjectif = when {
        tirage.mode == ModeJeu.CHIFFRES && tirage.type == TypeDefi.SERIE -> "${tirage.objectif} comptes exacts d'affilée"
        tirage.mode == ModeJeu.CHIFFRES -> "${tirage.objectif} comptes exacts"
        tirage.type == TypeDefi.SERIE -> "${tirage.objectif} mots valides d'affilée"
        else -> "${tirage.objectif} mots valides"
    }
    val prochainPalier = PALIERS_SERIE_JOURS.firstOrNull { it.first > serieActuelle }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Défi quotidien", onRetour)
        PucePseudo(pseudoActif)

        Text("Aujourd'hui : $nomType $nomMode", style = MaterialTheme.typography.titleMedium)
        Text("Objectif : $natureObjectif", style = MaterialTheme.typography.bodyMedium)

        Text(
            if (serieActuelle == 0) "Aucune série en cours" else "Série en cours : $serieActuelle jour(s) d'affilée",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            if (prochainPalier != null) {
                "Encore ${prochainPalier.first - serieActuelle} jour(s) pour le trophée \"${prochainPalier.second}\""
            } else {
                "Tous les paliers de série débloqués !"
            },
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
        )

        if (dejaReussiAujourdhui) {
            Text(
                "Défi du jour déjà réussi, bravo ! Reviens demain pour le prochain.",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else if (tirage.mode == ModeJeu.CHIFFRES) {
            Text("Choisis ton niveau", style = MaterialTheme.typography.titleMedium)
            for (niveau in Niveau.entries) {
                Button(onClick = { onNiveauChiffresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.label)
                }
            }
        } else {
            Text("Choisis ton niveau", style = MaterialTheme.typography.titleMedium)
            for (niveau in NiveauLettres.entries) {
                Button(onClick = { onNiveauLettresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.label)
                }
            }
        }
    }
}
