package fr.pierre.chiffreslettres.numbers

import kotlin.math.abs
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
        nombreJetons: Int = ReservoirChiffres.NOMBRE_JETONS_DEFAUT,
        maxTentativesCible: Int = 200,
        maxTentativesTirage: Int = 20,
    ): Resultat {
        repeat(maxTentativesTirage) {
            val nombres = ReservoirChiffres.tirerNombres(nombreJetons, random)
            val atteignables = Solveur.valeursAtteignables(nombres, niveau.operations)

            if (!niveau.garantieSolution) {
                val cible = random.nextInt(niveau.cibleMin, niveau.cibleMax + 1)
                // Pas de solution exacte garantie : à défaut, on retient la valeur atteignable
                // la plus proche de la cible (retour utilisateur) pour l'affichage en fin de
                // manche et pour le calcul du barème (§3.3).
                val solution = atteignables[cible] ?: atteignables.entries.minByOrNull { abs(it.key - cible) }?.value
                return Resultat(nombres, cible, solution)
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
