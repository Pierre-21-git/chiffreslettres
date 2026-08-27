package fr.pierre.chiffreslettres.letters

import org.junit.Assert.assertEquals
import org.junit.Test

class BaremeLettresTest {

    @Test
    fun `valeurLettre suit le bareme Scrabble francais`() {
        assertEquals(1, BaremeLettres.valeurLettre('E'))
        assertEquals(1, BaremeLettres.valeurLettre('L'))
        assertEquals(2, BaremeLettres.valeurLettre('D'))
        assertEquals(3, BaremeLettres.valeurLettre('C'))
        assertEquals(4, BaremeLettres.valeurLettre('H'))
        assertEquals(8, BaremeLettres.valeurLettre('J'))
        assertEquals(10, BaremeLettres.valeurLettre('Z'))
    }

    @Test
    fun `valeurLettre est insensible a la casse`() {
        assertEquals(BaremeLettres.valeurLettre('k'), BaremeLettres.valeurLettre('K'))
    }

    @Test
    fun `scoreMot additionne la valeur de chaque lettre`() {
        // C(3) H(4) A(1) T(1) = 9
        assertEquals(9, BaremeLettres.scoreMot("CHAT"))
        assertEquals(9, BaremeLettres.scoreMot("chat"))
    }

    @Test
    fun `scoreMot renvoie 0 pour un mot vide`() {
        assertEquals(0, BaremeLettres.scoreMot(""))
    }
}
