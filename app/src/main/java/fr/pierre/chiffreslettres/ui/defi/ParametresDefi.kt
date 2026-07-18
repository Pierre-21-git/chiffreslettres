package fr.pierre.chiffreslettres.ui.defi

import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

/**
 * Longueur minimale (le mot doit compter au moins cette longueur) d'un mot réussi en défi
 * lettres, selon le niveau — règle commune au défi série et au défi chrono (retour utilisateur :
 * uniformisée entre les deux types de défi).
 */
fun seuilLongueurDefiLettres(niveau: NiveauLettres): Int = when (niveau) {
    NiveauLettres.EMILE -> 4
    NiveauLettres.NESTOR -> 5
    NiveauLettres.MONIQUE -> 6
    NiveauLettres.MATHIEU -> 7
}

/** Budget de temps global (en secondes) d'un défi chrono chiffres, selon le niveau (retour utilisateur : 2/3/4/5 min). */
fun budgetSecondesDefiChrono(niveau: Niveau): Int = when (niveau) {
    Niveau.EMILE -> 120
    Niveau.NESTOR -> 180
    Niveau.MONIQUE -> 240
    Niveau.MATHIEU -> 300
}

/** Budget de temps global (en secondes) d'un défi chrono lettres, selon le niveau (retour utilisateur : 2/3/4/5 min). */
fun budgetSecondesDefiChrono(niveau: NiveauLettres): Int = when (niveau) {
    NiveauLettres.EMILE -> 120
    NiveauLettres.NESTOR -> 180
    NiveauLettres.MONIQUE -> 240
    NiveauLettres.MATHIEU -> 300
}
