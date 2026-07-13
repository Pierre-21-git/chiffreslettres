package fr.pierre.chiffreslettres.letters

import kotlin.random.Random

/**
 * Sac de lettres à tirage sans remise, distribution Scrabble française
 * (spec §4.1), avec exclusions selon le niveau (§4.2). Le Y compte comme
 * voyelle pour la règle du minimum 2 voyelles.
 */
class SacLettres private constructor(private val comptes: MutableMap<Char, Int>) {

    fun restant(lettre: Char): Int = comptes[lettre] ?: 0

    fun total(): Int = comptes.values.sum()

    fun tirerConsonne(random: Random = Random): Char = tirerParmi(random) { it !in VOYELLES }

    fun tirerVoyelle(random: Random = Random): Char = tirerParmi(random) { it in VOYELLES }

    private fun tirerParmi(random: Random, filtre: (Char) -> Boolean): Char {
        val pool = comptes.filter { (lettre, poids) -> filtre(lettre) && poids > 0 }
        val totalPoids = pool.values.sum()
        require(totalPoids > 0) { "Aucune lettre disponible dans cet ensemble du sac" }

        var tirage = random.nextInt(totalPoids)
        for ((lettre, poids) in pool) {
            if (tirage < poids) {
                comptes[lettre] = poids - 1
                return lettre
            }
            tirage -= poids
        }
        error("Tirage pondéré incohérent")
    }

    companion object {
        val VOYELLES = setOf('A', 'E', 'I', 'O', 'U', 'Y')

        private val DISTRIBUTION_BASE = mapOf(
            'A' to 9, 'B' to 2, 'C' to 2, 'D' to 3, 'E' to 15, 'F' to 2, 'G' to 2,
            'H' to 2, 'I' to 8, 'J' to 1, 'K' to 1, 'L' to 5, 'M' to 3, 'N' to 6,
            'O' to 6, 'P' to 2, 'Q' to 1, 'R' to 6, 'S' to 6, 'T' to 6, 'U' to 6,
            'V' to 2, 'W' to 1, 'X' to 1, 'Y' to 1, 'Z' to 1,
        )

        fun creer(niveau: NiveauLettres): SacLettres {
            val comptes = DISTRIBUTION_BASE.toMutableMap()
            for (lettre in niveau.lettresExclues) comptes.remove(lettre)
            return SacLettres(comptes)
        }
    }
}
