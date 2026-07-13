package fr.pierre.chiffreslettres.numbers

import kotlin.math.abs

/** Barème de points dégressif selon l'écart (spec §3.3). */
object Bareme {
    fun score(cible: Int, propose: Int?): Int {
        if (propose == null) return 0
        return maxOf(0, 10 - abs(cible - propose))
    }
}
