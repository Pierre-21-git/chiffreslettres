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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.network.SousModeDuelMots
import fr.pierre.chiffreslettres.ui.apropos.LienReglesDuJeu
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDuelMots
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

private const val OBJECTIF_MOTS_MINIMUM = 5
private const val OBJECTIF_MOTS_MAXIMUM = 10

/** Configuration du duel mots, hôte uniquement (retour utilisateur : l'invité attend simplement que la partie démarre). */
@Composable
fun ChoixModeDuelMotsScreen(
    pseudoActif: String,
    onDemarrer: (sousMode: SousModeDuelMots, niveau: NiveauLettres, objectifMots: Int?) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    var sousMode by remember { mutableStateOf<SousModeDuelMots?>(null) }
    var niveau by remember { mutableStateOf<NiveauLettres?>(null) }
    var objectifMots by remember { mutableIntStateOf(OBJECTIF_MOTS_MINIMUM) }

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

        val sousModeChoisi = sousMode
        val niveauChoisi = niveau
        Button(
            onClick = {
                val s = sousModeChoisi ?: return@Button
                val n = niveauChoisi ?: return@Button
                onDemarrer(s, n, if (s == SousModeDuelMots.CONFRONTATION) objectifMots else null)
            },
            enabled = sousModeChoisi != null && niveauChoisi != null,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.configuration_partie_reseau_demarrer)) }
    }
}

@Composable
private fun BoutonChoixSousMode(sousMode: SousModeDuelMots, selectionne: Boolean, onClick: () -> Unit) {
    val texte = stringResource(
        if (sousMode == SousModeDuelMots.DUO) R.string.duel_mots_sous_mode_duo else R.string.duel_mots_sous_mode_confrontation,
    )
    if (selectionne) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    }
}
