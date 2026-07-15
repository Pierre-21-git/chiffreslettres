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
}
