package fr.pierre.chiffreslettres.numbers

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TirageChiffresTest {

    @Test
    fun `niveaux garantis produisent toujours une solution exacte`() {
        val niveauxGarantis = Niveau.entries.filter { it.garantieSolution }
        val random = Random(123)
        for (niveau in niveauxGarantis) {
            repeat(20) {
                val resultat = TirageChiffres.tirer(niveau, random)
                assertNotNull("$niveau devrait toujours produire une solution", resultat.solution)
                assertEquals(resultat.cible, resultat.solution!!.resultat)
                assertTrue(resultat.cible in niveau.cibleMin..niveau.cibleMax)
                assertEquals(6, resultat.nombres.size)
            }
        }
    }

    @Test
    fun `niveaux non garantis peuvent ne pas avoir de solution`() {
        // Pas d'assertion sur la présence d'une solution, juste que le tirage reste cohérent.
        val random = Random(456)
        repeat(20) {
            val resultat = TirageChiffres.tirer(Niveau.MATHIEU, random)
            assertEquals(6, resultat.nombres.size)
            assertTrue(resultat.cible in 100..999)
        }
    }

    @Test
    fun `Odile est comme Emile avec la multiplication en plus, sans division`() {
        assertEquals(Niveau.EMILE.cibleMin, Niveau.ODILE.cibleMin)
        assertEquals(Niveau.EMILE.cibleMax, Niveau.ODILE.cibleMax)
        assertEquals(Niveau.EMILE.garantieSolution, Niveau.ODILE.garantieSolution)
        assertEquals(Niveau.EMILE.manchesParMode, Niveau.ODILE.manchesParMode)
        assertEquals(Niveau.EMILE.dureeSecondesPartieStructuree, Niveau.ODILE.dureeSecondesPartieStructuree)
        assertEquals(Niveau.EMILE.operations + Operation.FOIS, Niveau.ODILE.operations)
        assertTrue(Operation.FOIS in Niveau.ODILE.operations)
        assertTrue(Operation.DIVISE !in Niveau.ODILE.operations)
        assertEquals(5, Niveau.ODILE.tableMultiplicationMax)
    }

    @Test
    fun `les solutions Odile ne multiplient jamais en dehors des tables de 1 a 5`() {
        fun multiplicationsUtilisees(e: Expression): List<Pair<Int, Int>> = when (e) {
            is Expression.Valeur -> emptyList()
            is Expression.Calcul -> {
                val sousMultiplications = multiplicationsUtilisees(e.gauche) + multiplicationsUtilisees(e.droite)
                if (e.operation == Operation.FOIS) sousMultiplications + (e.gauche.resultat to e.droite.resultat) else sousMultiplications
            }
        }
        val random = Random(321)
        repeat(50) {
            val resultat = TirageChiffres.tirer(Niveau.ODILE, random)
            for ((x, y) in multiplicationsUtilisees(resultat.solution!!)) {
                assertTrue("$x x $y hors des tables de 1 à 5", (x in 1..5 && y in 1..10) || (y in 1..5 && x in 1..10))
            }
        }
    }

    @Test
    fun `garantieSolution force une solution exacte meme sur Monique et Mathieu (mode Defi)`() {
        val random = Random(789)
        for (niveau in listOf(Niveau.MONIQUE, Niveau.MATHIEU)) {
            repeat(20) {
                val resultat = TirageChiffres.tirer(niveau, random, garantieSolution = true)
                assertNotNull("$niveau avec garantieSolution=true devrait toujours produire une solution", resultat.solution)
                assertEquals(resultat.cible, resultat.solution!!.resultat)
            }
        }
    }
}
