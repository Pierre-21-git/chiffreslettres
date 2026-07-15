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
    fun `niveaux Monique et Mathieu rapportent 7 points si la meilleure approche possible est atteinte`() {
        assertEquals(7, Bareme.score(Niveau.MONIQUE, cible = 42, propose = 45, ecartMinimalAtteignable = 3))
        assertEquals(7, Bareme.score(Niveau.MATHIEU, cible = 42, propose = 39, ecartMinimalAtteignable = 3))
    }

    @Test
    fun `niveaux Monique et Mathieu rapportent 0 point si la proposition n'est pas la meilleure approche possible`() {
        // La meilleure approche pour ce tirage était à 3 du compte, le joueur est à 5 : pas de points.
        assertEquals(0, Bareme.score(Niveau.MONIQUE, cible = 42, propose = 47, ecartMinimalAtteignable = 3))
        assertEquals(0, Bareme.score(Niveau.MATHIEU, cible = 42, propose = 100, ecartMinimalAtteignable = 3))
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
