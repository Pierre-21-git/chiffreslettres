package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import kotlinx.coroutines.launch

@Composable
fun ChangerProfilScreen(
    profilRepository: ProfilRepository,
    profilActifStore: ProfilActifStore,
    onProfilChoisi: () -> Unit,
    onCreerNouveauProfil: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var profilARenommer by remember { mutableStateOf<ProfilEntity?>(null) }
    var profilASupprimer by remember { mutableStateOf<ProfilEntity?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnTeteEcran("Choisir un profil", onRetour)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            for (profil in profils) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                profilActifStore.definirProfilActif(profil.id)
                                onProfilChoisi()
                            }
                        },
                    ) {
                        Text("${profil.avatar}  ${profil.pseudo}")
                    }
                    IconButton(onClick = { profilARenommer = profil }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Renommer")
                    }
                    IconButton(onClick = { profilASupprimer = profil }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                    }
                }
            }
            Button(onClick = onCreerNouveauProfil, modifier = Modifier.fillMaxWidth()) { Text("Créer un nouveau profil") }
        }
    }

    profilARenommer?.let { profil ->
        var nouveauPseudo by remember(profil.id) { mutableStateOf(profil.pseudo) }
        var nouvelAvatar by remember(profil.id) { mutableStateOf(profil.avatar) }
        AlertDialog(
            onDismissRequest = { profilARenommer = null },
            title = { Text("Renommer le profil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = nouveauPseudo, onValueChange = { nouveauPseudo = it })
                    SelecteurAvatar(avatarSelectionne = nouvelAvatar, onAvatarChoisi = { nouvelAvatar = it })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val nom = nouveauPseudo.trim()
                    if (nom.isNotEmpty()) {
                        scope.launch {
                            profilRepository.renommerProfil(profil.id, nom)
                            profilRepository.definirAvatar(profil.id, nouvelAvatar)
                        }
                    }
                    profilARenommer = null
                }) { Text("Valider") }
            },
            dismissButton = {
                TextButton(onClick = { profilARenommer = null }) { Text("Annuler") }
            },
        )
    }

    profilASupprimer?.let { profil ->
        AlertDialog(
            onDismissRequest = { profilASupprimer = null },
            title = { Text("Supprimer le profil") },
            text = { Text("Le profil \"${profil.pseudo}\" et tout son historique seront définitivement supprimés. Continuer ?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { profilRepository.supprimerProfil(profil) }
                    profilASupprimer = null
                }) { Text("Supprimer") }
            },
            dismissButton = {
                TextButton(onClick = { profilASupprimer = null }) { Text("Annuler") }
            },
        )
    }
}
