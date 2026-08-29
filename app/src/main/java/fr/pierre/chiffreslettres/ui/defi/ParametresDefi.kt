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

/**
 * Une manche de défi lettres est réussie si le mot proposé atteint le seuil du niveau, ou
 * (retour utilisateur, Monique/Mathieu uniquement) si aucun mot du tirage n'atteint ce seuil et
 * que le joueur a trouvé le mot le plus long possible pour ce tirage — comme le palier "au plus
 * près" du défi chiffres, appliqué au seul cas où le tirage ne permet objectivement pas d'atteindre
 * le seuil.
 */
fun motEstReussiDefiLettres(niveau: NiveauLettres, motPropose: String, seuil: Int, meilleurMot: String?): Boolean {
    if (motPropose.length >= seuil) return true
    val toleranceMeilleureApproche = niveau == NiveauLettres.MONIQUE || niveau == NiveauLettres.MATHIEU
    val meilleurLongueur = meilleurMot?.length ?: 0
    return toleranceMeilleureApproche && meilleurLongueur < seuil && motPropose.length == meilleurLongueur
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

/** Durée fixe (retour utilisateur : 5 minutes), identique quel que soit le niveau, du défi mots max. */
const val DUREE_SECONDES_DEFI_MOTS_MAX = 300

/** Nombre maximal de mots affichés dans "mots possibles" en fin de défi (retour utilisateur : au-delà, la grille devient illisible). Le calcul du score/de la fin du défi reste basé sur la liste complète, seul l'affichage est plafonné. */
const val MAX_MOTS_POSSIBLES_AFFICHES = 100

/** Nombre d'objectifs de points à atteindre en défi Points, selon le niveau (retour utilisateur). */
fun nombreObjectifsDefiPoints(niveau: NiveauLettres): Int = when (niveau) {
    NiveauLettres.EMILE -> 3
    NiveauLettres.NESTOR -> 4
    NiveauLettres.MONIQUE -> 5
    NiveauLettres.MATHIEU -> 6
}
