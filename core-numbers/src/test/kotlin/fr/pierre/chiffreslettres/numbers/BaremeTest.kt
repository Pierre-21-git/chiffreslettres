package fr.pierre.chiffreslettres.numbers

import org.junit.Assert.assertEquals
import org.junit.Test

class BaremeTest {
    @Test
    fun `compte exact rapporte 10 points quel que soit le niveau`() {
        for (niveau in Niveau.entries) {
            assertEquals(10, Bareme.score(niveau, cible = 42, propose = 42))
        }
    }

    @Test
    fun `niveaux Monique et Mathieu rapportent 7 points pour un compte approchant a 100 ou moins`() {
        // Même quand le compte exact était atteignable et que le joueur ne l'a pas trouvé
        // (retour utilisateur : le palier de 7 points n'est pas conditionné à l'absence de
        // solution exacte pour ce tirage), et pour tout écart <= 100 (retour utilisateur :
        // cible=852, proposé=840, écart=12 doit rapporter 7 points).
        assertEquals(7, Bareme.score(Niveau.MONIQUE, cible = 42, propose = 43))
        assertEquals(7, Bareme.score(Niveau.MATHIEU, cible = 355, propose = 356))
        assertEquals(7, Bareme.score(Niveau.MONIQUE, cible = 852, propose = 840))
        assertEquals(7, Bareme.score(Niveau.MATHIEU, cible = 42, propose = 100))
        assertEquals(7, Bareme.score(Niveau.MONIQUE, cible = 42, propose = 142))
    }

    @Test
    fun `niveaux Monique et Mathieu rapportent 0 point au-dela d'un ecart de 100`() {
        assertEquals(0, Bareme.score(Niveau.MONIQUE, cible = 42, propose = 143))
        assertEquals(0, Bareme.score(Niveau.MATHIEU, cible = 900, propose = 1001))
    }

    @Test
    fun `niveaux Emile et Nestor rapportent 5 points pour toute proposition non exacte`() {
        assertEquals(5, Bareme.score(Niveau.EMILE, cible = 42, propose = 45))
        assertEquals(5, Bareme.score(Niveau.NESTOR, cible = 42, propose = 100))
    }

    @Test
    fun `aucune proposition vaut 0 point quel que soit le niveau`() {
        for (niveau in Niveau.entries) {
            assertEquals(0, Bareme.score(niveau, cible = 42, propose = null))
        }
    }
}
