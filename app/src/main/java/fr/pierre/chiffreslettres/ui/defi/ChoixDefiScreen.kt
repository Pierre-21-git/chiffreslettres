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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo

/**
 * Choix du type de défi (onglets Série / Chrono, retour utilisateur : le défi chrono s'ajoute au
 * défi série existant, pas de remplacement), puis du mode/niveau — même pattern que
 * `ChoixNiveauEntrainementScreen`.
 */
@Composable
fun ChoixDefiScreen(
    pseudoActif: String,
    onNiveauChiffresSerieChoisi: (Niveau) -> Unit,
    onNiveauLettresSerieChoisi: (NiveauLettres) -> Unit,
    onNiveauChiffresChronoChoisi: (Niveau) -> Unit,
    onNiveauLettresChronoChoisi: (NiveauLettres) -> Unit,
    onChangerProfil: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    var ongletSelectionne by remember { mutableIntStateOf(0) }
    val onglets = listOf("Série", "Chrono")

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Défi", onRetour)
        PucePseudo(pseudoActif, onClick = onChangerProfil)

        TabRow(selectedTabIndex = ongletSelectionne) {
            for ((index, titre) in onglets.withIndex()) {
                Tab(
                    selected = ongletSelectionne == index,
                    onClick = { ongletSelectionne = index },
                    text = { Text(titre) },
                )
            }
        }

        if (ongletSelectionne == 0) {
            Text("Chiffres", style = MaterialTheme.typography.titleMedium)
            for (niveau in Niveau.entries) {
                Button(onClick = { onNiveauChiffresSerieChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.label)
                }
            }

            Text("Lettres", style = MaterialTheme.typography.titleMedium)
            for (niveau in NiveauLettres.entries) {
                Button(onClick = { onNiveauLettresSerieChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.label)
                }
            }
        } else {
            Text("Chiffres — le plus de comptes exacts", style = MaterialTheme.typography.titleMedium)
            for (niveau in Niveau.entries) {
                Button(onClick = { onNiveauChiffresChronoChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text("${niveau.label} — ${budgetSecondesDefiChrono(niveau) / 60} min")
                }
            }

            Text("Lettres — le plus de mots", style = MaterialTheme.typography.titleMedium)
            for (niveau in NiveauLettres.entries) {
                Button(onClick = { onNiveauLettresChronoChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text("${niveau.label} — ${budgetSecondesDefiChrono(niveau) / 60} min")
                }
            }
        }
    }
}
