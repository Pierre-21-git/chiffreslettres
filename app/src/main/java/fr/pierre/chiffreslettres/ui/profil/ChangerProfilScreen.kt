package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import kotlinx.coroutines.launch

@Composable
fun ChangerProfilScreen(
    profilRepository: ProfilRepository,
    profilActifStore: ProfilActifStore,
    onProfilChoisi: () -> Unit,
    onCreerNouveauProfil: () -> Unit,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Choisir un profil", style = MaterialTheme.typography.headlineSmall)
        for (profil in profils) {
            OutlinedButton(onClick = {
                scope.launch {
                    profilActifStore.definirProfilActif(profil.id)
                    onProfilChoisi()
                }
            }) {
                Text(profil.pseudo)
            }
        }
        Button(onClick = onCreerNouveauProfil) { Text("Créer un nouveau profil") }
    }
}
