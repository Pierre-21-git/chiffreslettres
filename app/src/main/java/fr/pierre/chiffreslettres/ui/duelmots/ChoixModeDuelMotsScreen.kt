package fr.pierre.chiffreslettres.ui.duelmots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.network.OBJECTIFS_POINTS_DUEL
import fr.pierre.chiffreslettres.network.SousModeDuelMots
import fr.pierre.chiffreslettres.ui.apropos.LienReglesDuJeu
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDuelMots
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

private const val OBJECTIF_MOTS_MINIMUM = 5
private const val OBJECTIF_MOTS_MAXIMUM = 10

/** Sous-mode Points (retour utilisateur, 2026-08-28) : pas de niveau, alphabet complet — voir `SousModeDuelMots`. */
private val NIVEAU_DUEL_POINTS = NiveauLettres.MATHIEU

/** Configuration du duel mots, hôte uniquement (retour utilisateur : l'invité attend simplement que la partie démarre). */
@Composable
fun ChoixModeDuelMotsScreen(
    pseudoActif: String,
    onDemarrer: (sousMode: SousModeDuelMots, niveau: NiveauLettres, objectifMots: Int?, atteindreExactement: Boolean) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    var sousMode by remember { mutableStateOf<SousModeDuelMots?>(null) }
    var niveau by remember { mutableStateOf<NiveauLettres?>(null) }
    var objectifMots by remember { mutableIntStateOf(OBJECTIF_MOTS_MINIMUM) }
    var objectifPoints by remember { mutableIntStateOf(OBJECTIFS_POINTS_DUEL.first()) }
    var atteindreExactement by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.duel_mots_titre), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)
        LienReglesDuJeu { ReglesModeDuelMots() }

        Text(stringResource(R.string.duel_mots_choisir_sous_mode), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in SousModeDuelMots.entries) {
                BoutonChoixSousMode(candidat, selectionne = candidat == sousMode, onClick = { sousMode = candidat })
            }
        }

        if (sousMode == SousModeDuelMots.CONFRONTATION) {
            Text(stringResource(R.string.duel_mots_choisir_objectif), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (n in OBJECTIF_MOTS_MINIMUM..OBJECTIF_MOTS_MAXIMUM) {
                    val selectionne = n == objectifMots
                    val texte = stringResource(R.string.duel_mots_objectif_mots, n)
                    if (selectionne) {
                        Button(onClick = { objectifMots = n }, modifier = Modifier.weight(1f)) { Text(texte) }
                    } else {
                        OutlinedButton(onClick = { objectifMots = n }, modifier = Modifier.weight(1f)) { Text(texte) }
                    }
                }
            }
        }

        if (sousMode == SousModeDuelMots.POINTS) {
            Text(stringResource(R.string.duel_mots_choisir_objectif_points), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (n in OBJECTIFS_POINTS_DUEL) {
                    val selectionne = n == objectifPoints
                    val texte = stringResource(R.string.duel_mots_objectif_points, n)
                    if (selectionne) {
                        Button(onClick = { objectifPoints = n }, modifier = Modifier.weight(1f)) { Text(texte) }
                    } else {
                        OutlinedButton(onClick = { objectifPoints = n }, modifier = Modifier.weight(1f)) { Text(texte) }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Checkbox(checked = atteindreExactement, onCheckedChange = { atteindreExactement = it })
                Text(stringResource(R.string.duel_mots_atteindre_exactement))
            }
        }

        if (sousMode != SousModeDuelMots.POINTS) {
            Text(stringResource(R.string.configuration_quel_niveau), style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (candidat in NiveauLettres.entries) {
                    val selectionne = candidat == niveau
                    if (selectionne) {
                        Button(onClick = { niveau = candidat }, modifier = Modifier.fillMaxWidth()) { Text(candidat.libelle()) }
                    } else {
                        OutlinedButton(onClick = { niveau = candidat }, modifier = Modifier.fillMaxWidth()) { Text(candidat.libelle()) }
                    }
                }
            }
        }

        val sousModeChoisi = sousMode
        val niveauChoisi = niveau
        Button(
            onClick = {
                val s = sousModeChoisi ?: return@Button
                when (s) {
                    SousModeDuelMots.CONFRONTATION -> {
                        val n = niveauChoisi ?: return@Button
                        onDemarrer(s, n, objectifMots, false)
                    }
                    SousModeDuelMots.POINTS -> onDemarrer(s, NIVEAU_DUEL_POINTS, objectifPoints, atteindreExactement)
                    SousModeDuelMots.DUO -> {
                        val n = niveauChoisi ?: return@Button
                        onDemarrer(s, n, null, false)
                    }
                }
            },
            enabled = sousModeChoisi != null && (sousModeChoisi == SousModeDuelMots.POINTS || niveauChoisi != null),
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.configuration_partie_reseau_demarrer)) }
    }
}

@Composable
private fun BoutonChoixSousMode(sousMode: SousModeDuelMots, selectionne: Boolean, onClick: () -> Unit) {
    val texte = stringResource(
        when (sousMode) {
            SousModeDuelMots.DUO -> R.string.duel_mots_sous_mode_duo
            SousModeDuelMots.CONFRONTATION -> R.string.duel_mots_sous_mode_confrontation
            SousModeDuelMots.POINTS -> R.string.duel_mots_sous_mode_points
        },
    )
    if (selectionne) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    }
}
