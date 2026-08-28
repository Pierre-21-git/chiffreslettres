package fr.pierre.chiffreslettres.letters

import kotlin.random.Random

/**
 * Sac de lettres à tirage sans remise, avec exclusions selon le niveau (§4.2). La
 * distribution de base et l'ensemble des voyelles sont fournis par l'appelant (retour
 * utilisateur : externalisés dans strings.xml côté app pour permettre une déclinaison par
 * langue — ce module reste du Kotlin pur sans dépendance Android). En français,
 * distribution classique de lettres (spec §4.1), Y compte comme voyelle pour la règle du
 * minimum 2 voyelles.
 */
class SacLettres private constructor(private val comptes: MutableMap<Char, Int>, private val voyelles: Set<Char>) {

    fun restant(lettre: Char): Int = comptes[lettre] ?: 0

    fun total(): Int = comptes.values.sum()

    fun tirerConsonne(random: Random = Random): Char = tirerParmi(random) { it !in voyelles }

    fun tirerVoyelle(random: Random = Random): Char = tirerParmi(random) { it in voyelles }

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
        fun creer(distributionBase: Map<Char, Int>, voyelles: Set<Char>, exclusions: Set<Char>): SacLettres {
            val comptes = distributionBase.toMutableMap()
            for (lettre in exclusions) comptes.remove(lettre)
            return SacLettres(comptes, voyelles)
        }
    }
}
