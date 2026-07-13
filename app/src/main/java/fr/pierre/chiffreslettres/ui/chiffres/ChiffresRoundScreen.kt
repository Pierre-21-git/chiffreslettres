package fr.pierre.chiffreslettres.ui.chiffres

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.numbers.Operation

@Composable
fun ChiffresRoundScreen(
    viewModel: ChiffresRoundViewModel,
    scoreCumule: Int,
    onMancheTerminee: (score: Int) -> Unit,
    actionsFinManche: @Composable () -> Unit,
) {
    val etat by viewModel.uiState.collectAsState()

    LaunchedEffect(etat.termine) {
        if (etat.termine) onMancheTerminee(etat.scoreObtenu ?: 0)
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Score cumulé : $scoreCumule", style = MaterialTheme.typography.titleMedium)
        Text("Cible : ${etat.cible}", style = MaterialTheme.typography.headlineMedium)
        Text("Temps restant : ${etat.tempsRestantSecondes}s")

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (jeton in etat.jetons) {
                val selectionne = jeton.id == etat.premierSelectionne?.id
                if (selectionne) {
                    Button(onClick = { viewModel.cliquerJeton(jeton) }) { Text("${jeton.expression.resultat}") }
                } else {
                    OutlinedButton(onClick = { viewModel.cliquerJeton(jeton) }, enabled = !etat.termine) {
                        Text("${jeton.expression.resultat}")
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (operation in listOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)) {
                val autorisee = operation in etat.niveau.operations
                val selectionnee = operation == etat.operateurSelectionne
                if (selectionnee) {
                    Button(onClick = { viewModel.cliquerOperateur(operation) }) { Text(operation.symbole) }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.cliquerOperateur(operation) },
                        enabled = autorisee && !etat.termine,
                        modifier = Modifier.wrapContentWidth(),
                    ) { Text(operation.symbole) }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { viewModel.annulerDerniereOperation() }, enabled = !etat.termine) {
                Text("Annuler")
            }
            Button(onClick = { viewModel.valider() }, enabled = !etat.termine) {
                Text("Valider")
            }
        }

        if (etat.termine) {
            Text("Score obtenu : ${etat.scoreObtenu}", style = MaterialTheme.typography.titleLarge)
            Text("Une solution possible : ${etat.solutionSolveur?.texte() ?: "aucune"}")
            actionsFinManche()
        }
    }
}
