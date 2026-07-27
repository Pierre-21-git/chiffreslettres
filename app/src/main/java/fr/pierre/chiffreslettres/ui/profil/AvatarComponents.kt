package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.TuileJeton

/** Set d'avatars proposés à la création d'un profil (retour utilisateur, réglage simple). */
val AVATARS_DISPONIBLES = listOf(
    "🦊", "🐱", "🐶", "🦁", "🐸", "🐼", "🦄", "🐨",
    "🐵", "🦉", "🐧", "🐢", "🦋", "🐝", "🌟", "🎲",
)

@Composable
fun SelecteurAvatar(avatarSelectionne: String, onAvatarChoisi: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (avatar in AVATARS_DISPONIBLES) {
            TuileJeton(
                texte = avatar,
                selectionne = avatar == avatarSelectionne,
                onClick = { onAvatarChoisi(avatar) },
                monospace = false,
            )
        }
    }
}
