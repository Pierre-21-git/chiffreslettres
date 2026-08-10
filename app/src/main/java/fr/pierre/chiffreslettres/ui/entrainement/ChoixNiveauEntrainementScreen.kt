package fr.pierre.chiffreslettres.ui.entrainement

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.LienReglesDuJeu
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeChiffres
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeEntrainement
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeLettres
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

/** Remplace l'ancien duo "choix du mode" + "choix du niveau" par une liste unique (retour utilisateur). */
@Composable
fun ChoixNiveauEntrainementScreen(
    pseudoActif: String,
    onNiveauChiffresChoisi: (Niveau) -> Unit,
    onNiveauLettresChoisi: (NiveauLettres) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.entrainement_titre), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)
        LienReglesDuJeu {
            ReglesModeEntrainement()
            ReglesModeChiffres()
            ReglesModeLettres()
        }

        Text(stringResource(R.string.mode_chiffres), style = MaterialTheme.typography.titleMedium)
        for (niveau in Niveau.entries) {
            Button(onClick = { onNiveauChiffresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) { Text(niveau.libelle()) }
        }

        Text(stringResource(R.string.mode_lettres), style = MaterialTheme.typography.titleMedium)
        for (niveau in NiveauLettres.entries) {
            Button(onClick = { onNiveauLettresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) { Text(niveau.libelle()) }
        }
    }
}
