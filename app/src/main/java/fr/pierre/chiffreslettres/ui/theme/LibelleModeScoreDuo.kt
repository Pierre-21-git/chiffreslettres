package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.partieduo.ModeScoreDuo

/** Libellé affiché d'un mode de score duo ("Duo"/"Confrontation"), lu depuis strings.xml. */
@Composable
fun ModeScoreDuo.libelle(): String = stringResource(
    when (this) {
        ModeScoreDuo.DUO -> R.string.mode_duo_libelle
        ModeScoreDuo.CONFRONTATION -> R.string.mode_confrontation_libelle
    },
)

/** Description de la règle de score, affichée une fois le mode choisi. */
@Composable
fun ModeScoreDuo.description(): String = stringResource(
    when (this) {
        ModeScoreDuo.DUO -> R.string.mode_duo_description
        ModeScoreDuo.CONFRONTATION -> R.string.mode_confrontation_description
    },
)
