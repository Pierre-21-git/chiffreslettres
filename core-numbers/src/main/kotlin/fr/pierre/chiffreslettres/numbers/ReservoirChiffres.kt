package fr.pierre.chiffreslettres.numbers

import kotlin.random.Random

data class Plaque(val valeur: Int)

/** Le réservoir officiel de 24 plaques : 1..10 en double, 25/50/75/100 en simple. */
object ReservoirChiffres {
    val PETITS_NOMBRES = 1..10
    val GRANDS_NOMBRES = listOf(25, 50, 75, 100)

    fun plaquesInitiales(): List<Plaque> = buildList {
        for (v in PETITS_NOMBRES) {
            add(Plaque(v))
            add(Plaque(v))
        }
        for (v in GRANDS_NOMBRES) add(Plaque(v))
    }

    const val NOMBRE_JETONS_DEFAUT = 6

    /**
     * Tire [nombreJetons] plaques sans remise (6 par défaut). Le nombre de "grands
     * nombres" (25/50/75/100) est choisi aléatoirement entre 0 et 2, le reste
     * complété par des petits nombres (1-10).
     */
    fun tirerNombres(nombreJetons: Int = NOMBRE_JETONS_DEFAUT, random: Random = Random): List<Int> {
        val plaques = plaquesInitiales()
        val grands = plaques.filter { it.valeur in GRANDS_NOMBRES }.shuffled(random)
        val petits = plaques.filter { it.valeur in PETITS_NOMBRES }.shuffled(random)

        val nbGrandsMax = minOf(2, nombreJetons)
        val nbGrands = random.nextInt(0, nbGrandsMax + 1)
        val choisis = grands.take(nbGrands) + petits.take(nombreJetons - nbGrands)
        return choisis.map { it.valeur }.shuffled(random)
    }
}
