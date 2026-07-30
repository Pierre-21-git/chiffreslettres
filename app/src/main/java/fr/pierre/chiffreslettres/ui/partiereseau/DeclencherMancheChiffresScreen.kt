package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

/**
 * Manche chiffres, côté déclencheur (cf. `premierJoueurManche`) : pas de choix préalable comme en
 * lettres, juste un bouton pour démarrer la manche en même temps sur les 2 téléphones.
 */
@Composable
fun DeclencherMancheChiffresScreen(progressionManche: String, onCommencer: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.declencher_manche_progression, progressionManche), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.mode_chiffres), style = MaterialTheme.typography.headlineMedium)
        TuilePrincipale(stringResource(R.string.declencher_manche_commencer), onClick = onCommencer)
    }
}
