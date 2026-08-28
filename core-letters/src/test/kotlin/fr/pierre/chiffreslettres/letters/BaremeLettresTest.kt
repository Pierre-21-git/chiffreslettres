package fr.pierre.chiffreslettres.letters

import org.junit.Assert.assertEquals
import org.junit.Test

class BaremeLettresTest {

    @Test
    fun `valeurLettre suit le bareme francais`() {
        assertEquals(1, BaremeLettres.valeurLettre('E', BaremeLettres.FRANCAIS))
        assertEquals(1, BaremeLettres.valeurLettre('L', BaremeLettres.FRANCAIS))
        assertEquals(2, BaremeLettres.valeurLettre('D', BaremeLettres.FRANCAIS))
        assertEquals(3, BaremeLettres.valeurLettre('C', BaremeLettres.FRANCAIS))
        assertEquals(4, BaremeLettres.valeurLettre('H', BaremeLettres.FRANCAIS))
        assertEquals(8, BaremeLettres.valeurLettre('J', BaremeLettres.FRANCAIS))
        assertEquals(10, BaremeLettres.valeurLettre('Z', BaremeLettres.FRANCAIS))
    }

    @Test
    fun `valeurLettre est insensible a la casse`() {
        assertEquals(
            BaremeLettres.valeurLettre('k', BaremeLettres.FRANCAIS),
            BaremeLettres.valeurLettre('K', BaremeLettres.FRANCAIS),
        )
    }

    @Test
    fun `scoreMot additionne la valeur de chaque lettre`() {
        // C(3) H(4) A(1) T(1) = 9
        assertEquals(9, BaremeLettres.scoreMot("CHAT", BaremeLettres.FRANCAIS))
        assertEquals(9, BaremeLettres.scoreMot("chat", BaremeLettres.FRANCAIS))
    }

    @Test
    fun `scoreMot renvoie 0 pour un mot vide`() {
        assertEquals(0, BaremeLettres.scoreMot("", BaremeLettres.FRANCAIS))
    }

    @Test
    fun `scoreMot utilise le bareme fourni, pas un bareme fige`() {
        val baremeSimplifie = mapOf('A' to 5, 'B' to 1)
        assertEquals(6, BaremeLettres.scoreMot("AB", baremeSimplifie))
    }
}
