package fr.pierre.chiffreslettres.ui.defi

import fr.pierre.chiffreslettres.letters.NiveauLettres

/** Longueur minimale (strictement dépassée) d'un mot réussi en défi lettres, selon le niveau. */
fun seuilLongueurDefiLettres(niveau: NiveauLettres): Int = when (niveau) {
    NiveauLettres.EMILE -> 4
    NiveauLettres.NESTOR -> 5
    NiveauLettres.MONIQUE -> 6
    NiveauLettres.MATHIEU -> 7
}
