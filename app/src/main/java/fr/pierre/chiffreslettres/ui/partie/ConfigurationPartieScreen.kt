package fr.pierre.chiffreslettres.ui.partie

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

private const val NOMBRE_LETTRES_DEFAUT = 4
private const val NOMBRE_CHIFFRES_DEFAUT = 3

@Composable
fun ConfigurationPartieScreen(onDemarrer: (List<ManchePlanifiee>) -> Unit) {
    var nombreLettres by remember { mutableIntStateOf(NOMBRE_LETTRES_DEFAUT) }
    var nombreChiffres by remember { mutableIntStateOf(NOMBRE_CHIFFRES_DEFAUT) }
    var niveauLettres by remember { mutableStateOf(NiveauLettres.NORMAL) }
    var niveauChiffres by remember { mutableStateOf(Niveau.NORMAL_OFFICIEL) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Configurer la partie", style = MaterialTheme.typography.headlineSmall)

        Text("Manches lettres : $nombreLettres")
        CompteurManches(valeur = nombreLettres, onChange = { nombreLettres = it })
        Text("Niveau lettres :")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (niveau in NiveauLettres.entries) {
                if (niveau == niveauLettres) {
                    Button(onClick = { niveauLettres = niveau }) { Text(niveau.label) }
                } else {
                    OutlinedButton(onClick = { niveauLettres = niveau }) { Text(niveau.label) }
                }
            }
        }

        Text("Manches chiffres : $nombreChiffres")
        CompteurManches(valeur = nombreChiffres, onChange = { nombreChiffres = it })
        Text("Niveau chiffres :")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (niveau in Niveau.entries) {
                if (niveau == niveauChiffres) {
                    Button(onClick = { niveauChiffres = niveau }) { Text(niveau.label) }
                } else {
                    OutlinedButton(onClick = { niveauChiffres = niveau }) { Text(niveau.label) }
                }
            }
        }

        Button(
            onClick = {
                val sequence = List(nombreLettres) { ManchePlanifiee.Lettres(niveauLettres) } +
                    List(nombreChiffres) { ManchePlanifiee.Chiffres(niveauChiffres) }
                onDemarrer(sequence)
            },
            enabled = nombreLettres + nombreChiffres > 0,
        ) {
            Text("Démarrer")
        }
    }
}

@Composable
private fun CompteurManches(valeur: Int, onChange: (Int) -> Unit, min: Int = 0, max: Int = 10) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { if (valeur > min) onChange(valeur - 1) }) { Text("−") }
        OutlinedButton(onClick = { if (valeur < max) onChange(valeur + 1) }) { Text("+") }
    }
}
