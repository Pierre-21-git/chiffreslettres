package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ModeJeu

/** Libellé affiché d'un mode de jeu ("Chiffres"/"Lettres"), lu depuis strings.xml. */
@Composable
fun ModeJeu.libelle(): String = stringResource(
    when (this) {
        ModeJeu.CHIFFRES -> R.string.mode_chiffres
        ModeJeu.LETTRES -> R.string.mode_lettres
    },
)
