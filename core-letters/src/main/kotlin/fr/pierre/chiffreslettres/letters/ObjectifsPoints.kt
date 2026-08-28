package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex

/** Un objectif de points à atteindre en défi Points, cf. [genererObjectifs]. */
data class ObjectifPoints(val points: Int, val atteint: Boolean = false)

/** Longueur minimale d'un mot pris en compte pour un objectif de points — aucune (mot court à faible valeur légitime), seulement le minimum du dictionnaire lui-même. */
private const val LONGUEUR_MINIMALE_DICTIONNAIRE = 2

/** Score minimal d'un objectif (retour utilisateur) : en dessous, l'objectif est trop trivial. */
private const val SCORE_MINIMAL_OBJECTIF = 3

/**
 * Choisit jusqu'à [nombreObjectifs] valeurs de points distinctes atteignables sur [tirage] (selon
 * [bareme], le barème de points par lettre de la langue courante), réparties en quantiles du
 * score le plus faible au plus élevé (difficulté progressive au sein d'un même défi), en excluant
 * les scores en dessous de [SCORE_MINIMAL_OBJECTIF] (retour utilisateur : un objectif à 1 ou 2
 * points est trop trivial). Utilise [DictionnaireIndex.rechercherAuMoins] (pas
 * [DictionnaireIndex.rechercher], qui s'arrête à la première longueur non vide et ignorerait donc
 * les mots courts à faible score) pour couvrir toutes les longueurs jouables, courtes comme
 * longues. Peut renvoyer moins d'objectifs que demandé si le tirage n'offre pas assez de valeurs
 * distinctes — c'est à l'appelant de retirer un nouveau tirage dans ce cas (même principe que
 * `TirageLettres`/le garde-fou de `DefiMotsMaxViewModel`, cette fonction reste pure et ne retire
 * jamais elle-même).
 */
fun genererObjectifs(
    tirage: List<Char>,
    dictionnaire: DictionnaireIndex,
    nombreObjectifs: Int,
    bareme: Map<Char, Int>,
): List<ObjectifPoints> {
    val scores = dictionnaire.rechercherAuMoins(tirage, LONGUEUR_MINIMALE_DICTIONNAIRE)
        .map { BaremeLettres.scoreMot(it, bareme) }
        .filter { it >= SCORE_MINIMAL_OBJECTIF }
        .distinct()
        .sorted()
    if (scores.isEmpty()) return emptyList()
    val nb = nombreObjectifs.coerceAtMost(scores.size)
    val indices = (0 until nb).map { i -> i * (scores.size - 1) / (nb - 1).coerceAtLeast(1) }
    return indices.map { ObjectifPoints(scores[it]) }.distinctBy { it.points }
}
