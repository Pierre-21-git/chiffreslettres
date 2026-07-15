package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Identité visuelle unique du plateau, volontairement indépendante du thème
 * clair/sombre du système (comme l'écran d'un plateau télé — cf. maquette validée).
 */
private val ChiffresLettresColorScheme = darkColorScheme(
    primary = Brass,
    onPrimary = InkOnIvory,
    primaryContainer = BrassBright,
    onPrimaryContainer = InkOnIvory,
    secondary = Amber,
    onSecondary = InkOnIvory,
    background = Navy900,
    onBackground = Ivory,
    surface = Navy700,
    onSurface = Ivory,
    surfaceVariant = PanelDeep,
    onSurfaceVariant = TextMuted,
    error = Rouge,
    onError = RougeEncre,
    outline = TextFaint,
)

private val ChiffresLettresTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold),
)

@Composable
fun ChiffresLettresTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ChiffresLettresColorScheme,
        typography = ChiffresLettresTypography,
        content = content,
    )
}
