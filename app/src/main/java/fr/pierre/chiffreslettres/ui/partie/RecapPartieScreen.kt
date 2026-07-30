package fr.pierre.chiffreslettres.ui.partie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.libelle

@Composable
fun RecapPartieScreen(resultats: List<ResultatManche>, onTerminer: () -> Unit, onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnTeteEcran(stringResource(R.string.recap_partie_titre), onRetour)
        Text(stringResource(R.string.recap_partie_score_total, resultats.sumOf { it.score }), style = MaterialTheme.typography.titleLarge)
        for ((index, resultat) in resultats.withIndex()) {
            Text(stringResource(R.string.recap_partie_manche_detail, index + 1, resultat.mode.libelle(), resultat.score))
        }
        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_terminer)) }
    }
}
