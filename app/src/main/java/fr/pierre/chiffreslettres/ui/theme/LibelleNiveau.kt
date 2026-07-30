package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

/**
 * Libellé affiché d'un niveau (ex. "Assez facile, Émile"), lu depuis strings.xml (retour
 * utilisateur : externalisé pour permettre une déclinaison par langue). Les 4 niveaux
 * chiffres et lettres partagent les mêmes libellés.
 */
private fun libelleRes(niveau: Niveau): Int = when (niveau) {
    Niveau.EMILE -> R.string.niveau_emile
    Niveau.NESTOR -> R.string.niveau_nestor
    Niveau.MONIQUE -> R.string.niveau_monique
    Niveau.MATHIEU -> R.string.niveau_mathieu
}

private fun libelleRes(niveau: NiveauLettres): Int = when (niveau) {
    NiveauLettres.EMILE -> R.string.niveau_emile
    NiveauLettres.NESTOR -> R.string.niveau_nestor
    NiveauLettres.MONIQUE -> R.string.niveau_monique
    NiveauLettres.MATHIEU -> R.string.niveau_mathieu
}

@Composable
fun Niveau.libelle(): String = stringResource(libelleRes(this))

@Composable
fun NiveauLettres.libelle(): String = stringResource(libelleRes(this))
