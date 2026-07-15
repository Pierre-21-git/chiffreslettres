package fr.pierre.chiffreslettres.numbers

import kotlin.math.abs

/**
 * Barème de points (spec §3.3). Sur les deux niveaux les plus faciles (Émile,
 * Nestor), le barème est simplifié à but pédagogique : 10 points si le compte
 * est bon, 5 points pour toute proposition non exacte, 0 si rien n'a été
 * proposé.
 *
 * Sur Monique et Mathieu : 10 points si le compte est exact, 7 points pour un
 * compte approchant (écart de 1), 0 sinon — retour utilisateur : ce palier de
 * 7 points s'applique dès qu'on est à 1 du compte, qu'une solution exacte ait
 * existé ou non pour ce tirage (pas seulement quand c'est la meilleure
 * approche théoriquement atteignable).
 */
object Bareme {
    fun score(niveau: Niveau, cible: Int, propose: Int?): Int {
        if (propose == null) return 0
        val ecart = abs(cible - propose)
        return when (niveau) {
            Niveau.EMILE, Niveau.NESTOR -> if (ecart == 0) 10 else 5
            Niveau.MONIQUE, Niveau.MATHIEU -> when (ecart) {
                0 -> 10
                1 -> 7
                else -> 0
            }
        }
    }
}
