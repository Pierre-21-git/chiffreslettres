package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.TuileJeton

/** Langues disponibles (retour utilisateur : un drapeau par langue), code ISO -> drapeau. */
val LANGUES_DISPONIBLES = listOf(
    "fr" to "🇫🇷",
    "en" to "🇬🇧",
    "de" to "🇩🇪",
    "es" to "🇪🇸",
)

@Composable
fun SelecteurLangue(langueSelectionnee: String, onLangueChoisie: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for ((code, drapeau) in LANGUES_DISPONIBLES) {
            TuileJeton(
                texte = drapeau,
                selectionne = code == langueSelectionnee,
                onClick = { onLangueChoisie(code) },
                monospace = false,
            )
        }
    }
}
