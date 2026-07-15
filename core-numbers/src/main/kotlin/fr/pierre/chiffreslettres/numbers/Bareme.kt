package fr.pierre.chiffreslettres.numbers

import kotlin.math.abs

/**
 * Barème de points (spec §3.3). Sur les deux niveaux les plus faciles (Émile,
 * Nestor), le barème est simplifié à but pédagogique : 10 points si le compte
 * est bon, 5 points pour toute proposition non exacte, 0 si rien n'a été
 * proposé.
 *
 * Sur Monique et Mathieu (solution exacte non garantie), le barème fidèle au
 * jeu TV s'applique : 10 points si le compte est exact, 7 points si le joueur
 * atteint la meilleure approche possible pour ce tirage (calculée par
 * [TirageChiffres] et transmise via [ecartMinimalAtteignable]), 0 sinon —
 * retour utilisateur, un "compte approchant" vaut 7, pas un score dégressif
 * selon l'écart absolu.
 */
object Bareme {
    fun score(niveau: Niveau, cible: Int, propose: Int?, ecartMinimalAtteignable: Int = 0): Int {
        if (propose == null) return 0
        val ecart = abs(cible - propose)
        return when (niveau) {
            Niveau.EMILE, Niveau.NESTOR -> if (ecart == 0) 10 else 5
            Niveau.MONIQUE, Niveau.MATHIEU -> when {
                ecart == 0 -> 10
                ecart == ecartMinimalAtteignable && ecartMinimalAtteignable > 0 -> 7
                else -> 0
            }
        }
    }
}
