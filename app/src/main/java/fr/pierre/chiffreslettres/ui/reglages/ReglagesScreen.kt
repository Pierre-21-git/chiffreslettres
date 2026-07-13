package fr.pierre.chiffreslettres.ui.reglages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.ReglagesStore
import kotlinx.coroutines.launch

@Composable
fun ReglagesScreen(
    profilRepository: ProfilRepository,
    reglagesStore: ReglagesStore,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val dureeChiffres by reglagesStore.dureeChiffresSecondes.collectAsState(initial = 45)
    val dureeLettres by reglagesStore.dureeLettresSecondes.collectAsState(initial = 40)
    var profilARenommer by remember { mutableStateOf<ProfilEntity?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Réglages", style = MaterialTheme.typography.headlineSmall)

        Text("Profils", style = MaterialTheme.typography.titleMedium)
        for (profil in profils) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(profil.pseudo, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { profilARenommer = profil }) { Text("Renommer") }
                OutlinedButton(onClick = { scope.launch { profilRepository.supprimerProfil(profil) } }) {
                    Text("Supprimer")
                }
            }
        }

        Text("Durée du chrono", style = MaterialTheme.typography.titleMedium)
        Text("Chiffres : ${dureeChiffres}s")
        DureeStepper(valeur = dureeChiffres, onChange = { scope.launch { reglagesStore.definirDureeChiffres(it) } })
        Text("Lettres : ${dureeLettres}s")
        DureeStepper(valeur = dureeLettres, onChange = { scope.launch { reglagesStore.definirDureeLettres(it) } })
    }

    profilARenommer?.let { profil ->
        var nouveauPseudo by remember(profil.id) { mutableStateOf(profil.pseudo) }
        AlertDialog(
            onDismissRequest = { profilARenommer = null },
            title = { Text("Renommer le profil") },
            text = { OutlinedTextField(value = nouveauPseudo, onValueChange = { nouveauPseudo = it }) },
            confirmButton = {
                TextButton(onClick = {
                    val nom = nouveauPseudo.trim()
                    if (nom.isNotEmpty()) {
                        scope.launch { profilRepository.renommerProfil(profil.id, nom) }
                    }
                    profilARenommer = null
                }) { Text("Valider") }
            },
            dismissButton = {
                TextButton(onClick = { profilARenommer = null }) { Text("Annuler") }
            },
        )
    }
}

@Composable
private fun DureeStepper(valeur: Int, onChange: (Int) -> Unit, min: Int = 10, pas: Int = 5) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { if (valeur - pas >= min) onChange(valeur - pas) }) { Text("−") }
        OutlinedButton(onClick = { onChange(valeur + pas) }) { Text("+") }
    }
}
