package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.AVATAR_PAR_DEFAUT
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import kotlinx.coroutines.launch

/**
 * Saisie d'un pseudo pour créer un profil (premier lancement, §7.1, ou
 * création d'un profil supplémentaire depuis le changement rapide, §9).
 */
@Composable
fun CreerProfilScreen(
    profilRepository: ProfilRepository,
    profilActifStore: ProfilActifStore,
    premierLancement: Boolean,
    onProfilCree: () -> Unit,
    modifier: Modifier = Modifier,
    onRetour: (() -> Unit)? = null,
) {
    var pseudo by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf(AVATAR_PAR_DEFAUT) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(
            if (premierLancement) "Bienvenue ! Choisis un pseudo pour commencer." else "Créer un nouveau profil",
            onRetour,
        )
        OutlinedTextField(value = pseudo, onValueChange = { pseudo = it }, label = { Text("Pseudo") })
        Text("Avatar", style = MaterialTheme.typography.labelLarge)
        SelecteurAvatar(avatarSelectionne = avatar, onAvatarChoisi = { avatar = it })
        Button(
            onClick = {
                val nom = pseudo.trim()
                if (nom.isNotEmpty()) {
                    scope.launch {
                        val id = profilRepository.creerProfil(nom, avatar)
                        profilActifStore.definirProfilActif(id)
                        onProfilCree()
                    }
                }
            },
            enabled = pseudo.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Créer")
        }
    }
}
