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
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo

/**
 * Choix du mode/niveau du défi, même pattern que `ChoixNiveauEntrainementScreen`. Un écran
 * distinct par type de défi (retour utilisateur : deux boutons "Défi série"/"Défi chrono" sur
 * l'accueil, pas d'onglets) — [afficherDuree] ajoute le budget de temps du défi chrono sur
 * chaque bouton de niveau.
 */
@Composable
fun ChoixDefiScreen(
    titre: String,
    pseudoActif: String,
    afficherDuree: Boolean,
    onNiveauChiffresChoisi: (Niveau) -> Unit,
    onNiveauLettresChoisi: (NiveauLettres) -> Unit,
    onChangerProfil: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(titre, onRetour)
        PucePseudo(pseudoActif, onClick = onChangerProfil)

        Text("Chiffres", style = MaterialTheme.typography.titleMedium)
        for (niveau in Niveau.entries) {
            Button(onClick = { onNiveauChiffresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (afficherDuree) "${niveau.label} — ${budgetSecondesDefiChrono(niveau) / 60} min" else niveau.label)
            }
        }

        Text("Lettres", style = MaterialTheme.typography.titleMedium)
        for (niveau in NiveauLettres.entries) {
            Button(onClick = { onNiveauLettresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                Text(if (afficherDuree) "${niveau.label} — ${budgetSecondesDefiChrono(niveau) / 60} min" else niveau.label)
            }
        }
    }
}
