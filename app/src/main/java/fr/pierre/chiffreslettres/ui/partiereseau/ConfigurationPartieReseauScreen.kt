package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.partieduo.ModeScoreDuo
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.description
import fr.pierre.chiffreslettres.ui.theme.libelle

/** Configuration de la partie réseau, hôte uniquement (l'adversaire est déjà déterminé par la connexion). */
@Composable
fun ConfigurationPartieReseauScreen(
    pseudoActif: String,
    onDemarrer: (niveau: Niveau, mode: ModeScoreDuo) -> Unit,
    couleurRang: Color? = null,
) {
    var niveau by remember { mutableStateOf<Niveau?>(null) }
    var mode by remember { mutableStateOf<ModeScoreDuo?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.configuration_partie_titre))
        PucePseudo(pseudoActif, couleurRang = couleurRang)

        Text(stringResource(R.string.configuration_quel_niveau), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in Niveau.entries) {
                BoutonChoix(candidat.libelle(), selectionne = candidat == niveau, onClick = { niveau = candidat })
            }
        }

        Text(stringResource(R.string.configuration_quel_mode), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in ModeScoreDuo.entries) {
                BoutonChoix(candidat.libelle(), selectionne = candidat == mode, onClick = { mode = candidat })
            }
        }
        val modeChoisi = mode
        if (modeChoisi != null) {
            Text(modeChoisi.description(), style = MaterialTheme.typography.bodySmall)
        }

        val niveauChoisi = niveau
        Button(
            onClick = {
                val n = niveauChoisi ?: return@Button
                val m = modeChoisi ?: return@Button
                onDemarrer(n, m)
            },
            enabled = niveauChoisi != null && modeChoisi != null,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.configuration_partie_reseau_demarrer)) }
    }
}

@Composable
private fun BoutonChoix(texte: String, selectionne: Boolean, onClick: () -> Unit) {
    if (selectionne) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    }
}
