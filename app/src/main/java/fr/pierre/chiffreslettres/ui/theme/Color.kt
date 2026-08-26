package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.ui.graphics.Color

/** Palette « plateau télé » (spec design validée avec l'utilisateur — Antenne 2/France 3 rétro). */
val Navy900 = Color(0xFF101B33)
val Navy700 = Color(0xFF1E2C4F)
val PanelDeep = Color(0xFF0A1120)

val Ivory = Color(0xFFF1E6CF)
val IvoryTileTop = Color(0xFFF6ECD8)
val IvoryTileBottom = Color(0xFFE9DAB8)
val InkOnIvory = Color(0xFF1B1408)

val Brass = Color(0xFFC9A227)
val BrassBright = Color(0xFFE4BE4A)
val BrassShadow = Color(0xFF8A6A1F)
val BrassHighlight = Color(0xFFFFD88A)

val Amber = Color(0xFFFFB020)

val Rouge = Color(0xFFA63D38)
val RougeClair = Color(0xFFC96A5F)
val RougeOmbre = Color(0xFF7C2A26)
val RougeEncre = Color(0xFFFBEEE9)

val TextMuted = Color(0xFFCBB98A)
val TextFaint = Color(0xFF8A8267)

/**
 * Paliers de trophées (retour utilisateur) : Or réutilise Brass/BrassBright, déjà doré.
 * Argent/Platine/Diamant repoussés vers des teintes bien séparées (gris neutre / vert menthe /
 * cyan vif) — retour utilisateur : les trois se confondaient trop, toutes proches d'un même
 * bleu-gris pâle. Émeraude/Saphir/Rubis (refonte 2026-08, intercalés entre Platine et Diamant)
 * reprennent les codes hex du tableur de paliers.
 */
val PalierBronze = Color(0xFFCD7F32)
val PalierArgent = Color(0xFF9AA5B1)
val PalierPlatine = Color(0xFF7FE3C4)
val PalierEmeraude = Color(0xFF50C878)
val PalierSaphir = Color(0xFF0F52BA)
val PalierRubis = Color(0xFFE0115F)
val PalierDiamant = Color(0xFF33D2FF)
