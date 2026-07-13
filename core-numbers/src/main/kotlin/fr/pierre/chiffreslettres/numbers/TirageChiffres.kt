package fr.pierre.chiffreslettres.numbers

import kotlin.random.Random

/**
 * Orchestre le mécanisme de garantie de solvabilité du §3.2 : tire les 6
 * nombres, calcule une seule fois les valeurs atteignables, puis retire une
 * cible tant qu'elle n'est pas atteignable. Au-delà de [maxTentativesCible]
 * essais infructueux, retire aussi de nouveaux nombres (jusqu'à
 * [maxTentativesTirage] fois), en dernier recours.
 */
object TirageChiffres {

    data class Resultat(
        val nombres: List<Int>,
        val cible: Int,
        val solution: Expression?,
    )

    fun tirer(
        niveau: Niveau,
        random: Random = Random,
        maxTentativesCible: Int = 200,
        maxTentativesTirage: Int = 20,
    ): Resultat {
        repeat(maxTentativesTirage) {
            val nombres = ReservoirChiffres.tirerNombres(random)
            val atteignables = Solveur.valeursAtteignables(nombres, niveau.operations)

            if (!niveau.garantieSolution) {
                val cible = random.nextInt(niveau.cibleMin, niveau.cibleMax + 1)
                return Resultat(nombres, cible, atteignables[cible])
            }

            repeat(maxTentativesCible) {
                val cible = random.nextInt(niveau.cibleMin, niveau.cibleMax + 1)
                atteignables[cible]?.let { expression ->
                    return Resultat(nombres, cible, expression)
                }
            }
            // Aucune cible solvable trouvée pour ce tirage de nombres : on retire tout.
        }
        error("Impossible de générer un tirage solvable pour $niveau après $maxTentativesTirage tentatives")
    }
}
