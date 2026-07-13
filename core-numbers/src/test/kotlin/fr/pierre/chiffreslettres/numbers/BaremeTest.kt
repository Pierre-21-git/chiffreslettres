package fr.pierre.chiffreslettres.numbers

import org.junit.Assert.assertEquals
import org.junit.Test

class BaremeTest {
    @Test
    fun `compte exact rapporte 10 points`() {
        assertEquals(10, Bareme.score(cible = 42, propose = 42))
    }

    @Test
    fun `ecart reduit les points a due proportion`() {
        assertEquals(7, Bareme.score(cible = 42, propose = 45))
        assertEquals(7, Bareme.score(cible = 42, propose = 39))
    }

    @Test
    fun `ecart trop important plafonne a 0`() {
        assertEquals(0, Bareme.score(cible = 42, propose = 100))
    }

    @Test
    fun `aucune proposition vaut 0 point`() {
        assertEquals(0, Bareme.score(cible = 42, propose = null))
    }
}
