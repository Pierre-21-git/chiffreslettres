package fr.pierre.chiffreslettres.ui.lettres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LettresRoundScreen(
    viewModel: LettresRoundViewModel,
    scoreCumule: Int,
    onMancheTerminee: (score: Int, motValide: String?) -> Unit,
    actionsFinManche: @Composable () -> Unit,
) {
    val etat by viewModel.uiState.collectAsState()

    LaunchedEffect(etat.termine) {
        if (etat.termine) {
            val motValide = if (etat.motJoueurValide == true) etat.motSaisi else null
            onMancheTerminee(etat.scoreObtenu ?: 0, motValide)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Score cumulé : $scoreCumule", style = MaterialTheme.typography.titleMedium)
        Text("Lettres : ${etat.lettresTirees.joinToString(" ")}", style = MaterialTheme.typography.headlineSmall)

        if (!etat.tirageTermine) {
            Text("Tirage : ${etat.lettresTirees.size} / 9")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.tirerLettre(true) }, enabled = etat.consonneAutorisee) {
                    Text("Consonne")
                }
                Button(onClick = { viewModel.tirerLettre(false) }) {
                    Text("Voyelle")
                }
            }
        } else {
            Text("Temps restant : ${etat.tempsRestantSecondes}s")
            OutlinedTextField(
                value = etat.motSaisi,
                onValueChange = { viewModel.saisirMot(it) },
                enabled = !etat.termine,
                label = { Text("Votre mot") },
            )
            Button(onClick = { viewModel.valider() }, enabled = !etat.termine) {
                Text("Valider")
            }
        }

        if (etat.termine) {
            Text("Score obtenu : ${etat.scoreObtenu}", style = MaterialTheme.typography.titleLarge)
            val validite = if (etat.motJoueurValide == true) "valide" else "invalide ou absent du dictionnaire"
            Text("Votre mot (\"${etat.motSaisi}\") : $validite")
            Text("Meilleur mot trouvé : ${etat.meilleurMot ?: "aucun"}")
            actionsFinManche()
        }
    }
}
