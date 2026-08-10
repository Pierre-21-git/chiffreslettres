package fr.pierre.chiffreslettres.ui.partieduo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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

/** Même présentation que [fr.pierre.chiffreslettres.ui.partie.RecapPartieScreen], dupliquée pour les deux joueurs, avec le vainqueur en plus (retour utilisateur). */
@Composable
fun RecapPartieDuoScreen(
    pseudo1: String,
    pseudo2: String,
    resultats1: List<ResultatManche>,
    resultats2: List<ResultatManche>,
    onTerminer: () -> Unit,
    onRetour: (() -> Unit)? = null,
    onRejouer: (() -> Unit)? = null,
    afficherAttenteRejouer: Boolean = false,
) {
    val total1 = resultats1.sumOf { it.score }
    val total2 = resultats2.sumOf { it.score }
    val messageVainqueur = when {
        total1 > total2 -> stringResource(R.string.recap_duo_vainqueur, pseudo1)
        total2 > total1 -> stringResource(R.string.recap_duo_vainqueur, pseudo2)
        else -> stringResource(R.string.recap_duo_match_nul)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnTeteEcran(stringResource(R.string.recap_duo_titre), onRetour)
        Text(messageVainqueur, style = MaterialTheme.typography.titleLarge)

        BlocJoueur(pseudo1, total1, resultats1)
        HorizontalDivider()
        BlocJoueur(pseudo2, total2, resultats2)

        if (onRejouer != null) {
            Button(onClick = onRejouer, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_rejouer)) }
        } else if (afficherAttenteRejouer) {
            Text(stringResource(R.string.reseau_attente_nouvelle_partie), style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_terminer)) }
    }
}

@Composable
private fun BlocJoueur(pseudo: String, total: Int, resultats: List<ResultatManche>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(pseudo, style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.recap_partie_score_total, total), style = MaterialTheme.typography.titleLarge)
        for ((index, resultat) in resultats.withIndex()) {
            Text(stringResource(R.string.recap_partie_manche_detail, index + 1, resultat.mode.libelle(), resultat.score))
        }
    }
}
