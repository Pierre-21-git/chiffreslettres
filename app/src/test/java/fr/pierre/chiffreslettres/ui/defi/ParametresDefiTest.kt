package fr.pierre.chiffreslettres.ui.defi

import fr.pierre.chiffreslettres.letters.NiveauLettres
import org.junit.Assert.assertEquals
import org.junit.Test

class ParametresDefiTest {
    @Test
    fun `seuil de longueur des mots croit avec la difficulte du niveau`() {
        assertEquals(4, seuilLongueurDefiLettres(NiveauLettres.EMILE))
        assertEquals(5, seuilLongueurDefiLettres(NiveauLettres.NESTOR))
        assertEquals(6, seuilLongueurDefiLettres(NiveauLettres.MONIQUE))
        assertEquals(7, seuilLongueurDefiLettres(NiveauLettres.MATHIEU))
    }
}
