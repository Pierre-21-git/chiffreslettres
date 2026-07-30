package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Composants réutilisables de l'identité « plateau télé » (maquette validée). */

fun Modifier.fondPlateau(): Modifier =
    this.background(Brush.verticalGradient(listOf(Navy700, Navy900, PanelDeep)))

@Composable
fun BandeDoree(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(Brush.horizontalGradient(listOf(BrassShadow, BrassBright, BrassShadow))),
    )
}

@Composable
fun MarqueJeu(modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        for (mot in listOf("CHIFFRES", "&", "LETTRES")) {
            Text(
                mot,
                color = BrassBright,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                letterSpacing = 2.sp,
            )
        }
    }
}

/** Sépare "🦊 Pierre" (convention utilisée partout où l'avatar est préfixé au pseudo) en (avatar, pseudo). */
private fun decouperAvatarPseudo(pseudoAvecAvatar: String): Pair<String, String> {
    val parts = pseudoAvecAvatar.split(" ", limit = 2)
    return if (parts.size == 2) parts[0] to parts[1] else "" to pseudoAvecAvatar
}

/**
 * Mini-bandeau profil (retour utilisateur : même style partout, accueil compris — avatar et
 * pseudo bien visibles). [grand] agrandit encore le texte pour l'accueil. [couleurRang] (retour
 * utilisateur) entoure le cadre de la couleur du rang joueur (bronze/argent/or/platine/diamant)
 * quand elle est connue ; sinon la bordure neutre habituelle est utilisée.
 */
@Composable
fun PucePseudo(
    pseudo: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    grand: Boolean = false,
    couleurRang: Color? = null,
) {
    val (avatar, nom) = decouperAvatarPseudo(pseudo)
    val tailleAvatarBox = if (grand) 44.dp else 36.dp
    val tailleAvatarTexte = if (grand) 34.sp else 28.sp
    val tailleNom = if (grand) 20.sp else 16.sp
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Ivory.copy(alpha = 0.06f))
            .border(if (couleurRang != null) 2.dp else 1.dp, couleurRang ?: Ivory.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        Box(
            modifier = Modifier
                .size(tailleAvatarBox)
                .clip(CircleShape)
                .background(Ivory.copy(alpha = 0.1f))
                .border(1.dp, couleurRang?.copy(alpha = 0.8f) ?: BrassBright.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (avatar.isNotEmpty()) Text(avatar, fontSize = tailleAvatarTexte)
        }
        Text(nom, color = TextMuted, fontSize = tailleNom, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
    }
}

/** Tuile avec relief (fausse ombre décalée), brique de base des boutons/jetons du plateau. */
@Composable
private fun TuileRelief(
    degrade: List<Color>,
    couleurOmbre: Color,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier.fillMaxWidth(),
    forme: Shape = RoundedCornerShape(10.dp),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    epaisseurOmbre: Dp = 3.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.graphicsLayer(alpha = if (enabled) 1f else 0.35f)) {
        Box(
            Modifier
                .matchParentSize()
                .offset(y = epaisseurOmbre)
                .clip(forme)
                .background(couleurOmbre),
        )
        Box(
            modifier = contentModifier
                .clip(forme)
                .background(Brush.verticalGradient(degrade))
                .let { if (onClick != null) it.clickable(enabled = enabled, onClick = onClick) else it },
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

@Composable
fun TuilePrincipale(texte: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    TuileRelief(
        degrade = listOf(IvoryTileTop, IvoryTileBottom),
        couleurOmbre = BrassShadow,
        modifier = modifier.fillMaxWidth(),
        // Léger retrait horizontal (retour utilisateur : l'ivoire ne doit pas prendre
        // toute la largeur) : appliqué ici, avant le .background() de TuileRelief, il
        // laisse apparaître un fin liseré doré (l'ombre) de part et d'autre.
        contentModifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        epaisseurOmbre = 4.dp,
        onClick = onClick,
        enabled = enabled,
    ) {
        // Le padding vertical se met sur le texte, pas sur contentModifier : appliqué
        // avant le .background() de TuileRelief, il agrandirait la zone d'ombre dorée
        // derrière au lieu de la zone ivoire elle-même (retour utilisateur).
        Text(
            texte.uppercase(),
            modifier = Modifier.padding(vertical = 12.dp),
            color = InkOnIvory,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 1.2.sp,
        )
    }
}

@Composable
fun BoutonSecondaireContour(texte: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        modifier = modifier
            .graphicsLayer(alpha = if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(8.dp))
            .background(Ivory.copy(alpha = 0.06f))
            .border(1.5.dp, Ivory.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(texte.uppercase(), color = Ivory, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun Afficheur(label: String, valeur: String, modifier: Modifier = Modifier, grand: Boolean = false, centre: Boolean = false) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PanelDeep)
            .border(1.dp, Ivory.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = if (grand || centre) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(label.uppercase(), color = TextFaint, fontSize = 9.sp, letterSpacing = 1.sp)
        Text(
            valeur,
            color = Amber,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = if (grand) 38.sp else 22.sp,
        )
    }
}

@Composable
fun TuileJeton(
    texte: String,
    selectionne: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    monospace: Boolean = true,
    grand: Boolean = false,
) {
    TuileRelief(
        degrade = if (selectionne) listOf(BrassHighlight, BrassBright) else listOf(IvoryTileTop, IvoryTileBottom),
        couleurOmbre = if (selectionne) Brass else BrassShadow,
        modifier = modifier,
        contentModifier = Modifier
            .defaultMinSize(minWidth = if (grand) 56.dp else 42.dp, minHeight = if (grand) 60.dp else 48.dp)
            .padding(horizontal = 8.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(
            texte,
            color = InkOnIvory,
            fontWeight = FontWeight.Bold,
            fontSize = if (grand) 24.sp else if (monospace) 17.sp else 16.sp,
            fontFamily = if (monospace) FontFamily.Monospace else FontFamily.SansSerif,
        )
    }
}

@Composable
fun BoutonOperateur(
    symbole: String,
    selectionne: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(46.dp)
            .graphicsLayer(alpha = if (enabled) 1f else 0.3f)
            .clip(CircleShape)
            .background(if (selectionne) BrassBright else Ivory.copy(alpha = 0.05f))
            .border(1.5.dp, if (selectionne) BrassBright else Ivory.copy(alpha = 0.35f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            symbole,
            color = if (selectionne) InkOnIvory else Ivory,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun PanneauResultat(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PanelDeep)
            .border(1.dp, BrassBright.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(16.dp),
        content = content,
    )
}

/**
 * En-tête de titre avec flèche de retour, pour les écrans secondaires atteints par
 * navigation (pas l'accueil). [onRetour] à null (premier lancement, pas d'écran
 * précédent) masque la flèche tout en gardant le titre aligné. [centre] (retour
 * utilisateur, écran "Choisir un profil") centre le titre sur toute la largeur, sans
 * réserver de place pour une flèche (jamais affichée dans ce cas).
 */
@Composable
fun EnTeteEcran(titre: String, onRetour: (() -> Unit)? = null, modifier: Modifier = Modifier, centre: Boolean = false) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (centre) Arrangement.Center else Arrangement.spacedBy(4.dp),
    ) {
        if (!centre && onRetour != null) {
            IconButton(onClick = onRetour) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = BrassBright,
                )
            }
        } else if (!centre) {
            Spacer(Modifier.width(48.dp))
        }
        Text(
            titre.uppercase(),
            color = Ivory,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 19.sp,
            letterSpacing = 0.5.sp,
        )
    }
}
