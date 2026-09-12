package fr.pierre.chiffreslettres.numbers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolveurTest {

    /** Recalcule récursivement la valeur d'une expression, indépendamment du champ [Expression.resultat]. */
    private fun evaluer(expression: Expression): Int = when (expression) {
        is Expression.Valeur -> expression.resultat
        is Expression.Calcul -> {
            val g = evaluer(expression.gauche)
            val d = evaluer(expression.droite)
            when (expression.operation) {
                Operation.PLUS -> g + d
                Operation.MOINS -> g - d
                Operation.FOIS -> g * d
                Operation.DIVISE -> g / d
            }
        }
    }

    @Test
    fun `somme simple atteignable avec toutes les operations`() {
        val atteignables = Solveur.valeursAtteignables(listOf(2, 3, 4), TOUTES_OPERATIONS)
        assertTrue(9 in atteignables)
        assertEquals(9, evaluer(atteignables.getValue(9)))
    }

    @Test
    fun `multiplication non atteignable si seuls plus et moins sont autorises`() {
        val ops = setOf(Operation.PLUS, Operation.MOINS)
        val atteignables = Solveur.valeursAtteignables(listOf(2, 3), ops)
        assertFalse(6 in atteignables) // 2 x 3, impossible sans FOIS
        assertTrue(5 in atteignables) // 2 + 3
        assertTrue(1 in atteignables) // 3 - 2
    }

    @Test
    fun `aucune expression retournee n'utilise une operation interdite`() {
        val ops = setOf(Operation.PLUS, Operation.MOINS)
        val atteignables = Solveur.valeursAtteignables(listOf(1, 2, 3, 4, 5, 6), ops)
        fun operationsUtilisees(e: Expression): Set<Operation> = when (e) {
            is Expression.Valeur -> emptySet()
            is Expression.Calcul -> operationsUtilisees(e.gauche) + operationsUtilisees(e.droite) + e.operation
        }
        for (expr in atteignables.values) {
            assertTrue(operationsUtilisees(expr).all { it in ops })
        }
    }

    @Test
    fun `division exacte seulement`() {
        val atteignables = Solveur.valeursAtteignables(listOf(6, 3), TOUTES_OPERATIONS)
        assertTrue(2 in atteignables) // 6 / 3
        assertEquals(2, evaluer(atteignables.getValue(2)))

        val nonExact = Solveur.valeursAtteignables(listOf(7, 3), TOUTES_OPERATIONS)
        // 7/3 n'est pas entier : seules les valeurs 7, 3, 10, 4, 21 doivent être atteignables.
        assertEquals(setOf(7, 3, 10, 4, 21), nonExact.keys)
    }

    @Test
    fun `soustraction interdit resultat nul ou negatif`() {
        val atteignables = Solveur.valeursAtteignables(listOf(4, 4), setOf(Operation.MOINS))
        assertEquals(setOf(4), atteignables.keys) // 4-4=0 exclu, seule la valeur de départ (visible) reste
    }

    @Test
    fun `valeur intermediaire directement validable sans tout combiner`() {
        // Avec 6 nombres, une des 6 valeurs de départ doit rester atteignable telle quelle.
        val atteignables = Solveur.valeursAtteignables(listOf(1, 2, 3, 4, 5, 6), TOUTES_OPERATIONS)
        for (v in listOf(1, 2, 3, 4, 5, 6)) {
            assertTrue("$v devrait être atteignable directement", v in atteignables)
        }
    }

    @Test
    fun `tableMultiplicationMax limite la multiplication a un facteur dans 1 point max, l'autre dans 1 point 10`() {
        val ops = setOf(Operation.PLUS, Operation.FOIS)
        // 4 x 5 = 20 : autorisé (4 dans 1..5, 5 dans 1..10).
        assertEquals(20, Solveur.combiner(Expression.Valeur(4), Operation.FOIS, Expression.Valeur(5), tableMultiplicationMax = 5)?.resultat)
        // 5 x 9 = 45 : autorisé (5 dans 1..5, 9 dans 1..10), peu importe l'ordre des opérandes.
        assertEquals(45, Solveur.combiner(Expression.Valeur(9), Operation.FOIS, Expression.Valeur(5), tableMultiplicationMax = 5)?.resultat)
        // 7 x 9 : aucun des deux facteurs n'est <= 5, donc refusé.
        assertEquals(null, Solveur.combiner(Expression.Valeur(7), Operation.FOIS, Expression.Valeur(9), tableMultiplicationMax = 5))
        // 3 x 15 : le second facteur dépasse 10, donc refusé même si 3 <= 5.
        assertEquals(null, Solveur.combiner(Expression.Valeur(3), Operation.FOIS, Expression.Valeur(15), tableMultiplicationMax = 5))
        // Sans restriction (null), tout est permis.
        assertEquals(105, Solveur.combiner(Expression.Valeur(7), Operation.FOIS, Expression.Valeur(15))?.resultat)

        val atteignables = Solveur.valeursAtteignables(listOf(7, 9), ops, tableMultiplicationMax = 5)
        assertFalse(63 in atteignables) // 7 x 9, hors table de 1 à 5
        assertTrue(16 in atteignables) // 7 + 9 reste permis
    }

    @Test
    fun `exemple classique du compte est bon`() {
        // 25, 50, 75, 100, 3, 6 -> cible 952 : (100 x (6 + 3)) + (75 / 25) x ... on vérifie juste
        // une cible plus simple et sûre à la main : 100 - 75 + 50 - 25 + 6 - 3 = 53.
        val atteignables = Solveur.valeursAtteignables(listOf(25, 50, 75, 100, 3, 6), TOUTES_OPERATIONS)
        assertTrue(53 in atteignables)
        assertEquals(53, evaluer(atteignables.getValue(53)))
    }
}
