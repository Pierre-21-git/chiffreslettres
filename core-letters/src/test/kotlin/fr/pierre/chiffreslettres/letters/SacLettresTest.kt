package fr.pierre.chiffreslettres.letters

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SacLettresTest {

    @Test
    fun `le sac mathieu contient 100 lettres`() {
        assertEquals(100, SacLettres.creer(NiveauLettres.MATHIEU).total())
    }

    @Test
    fun `niveau nestor exclut X Y Z W K Q et garde les quantites brutes`() {
        val sac = SacLettres.creer(NiveauLettres.NESTOR)
        for (l in setOf('X', 'Y', 'Z', 'W', 'K', 'Q')) {
            assertEquals(0, sac.restant(l))
        }
        // Quantité brute conservée pour les lettres restantes (pas de renormalisation à 100).
        assertEquals(9, sac.restant('A'))
        assertEquals(15, sac.restant('E'))
    }

    @Test
    fun `niveau monique exclut seulement X Y Z W`() {
        val sac = SacLettres.creer(NiveauLettres.MONIQUE)
        for (l in setOf('X', 'Y', 'Z', 'W')) assertEquals(0, sac.restant(l))
        assertTrue(sac.restant('K') > 0)
        assertTrue(sac.restant('Q') > 0)
    }

    @Test
    fun `niveau emile exclut aussi H et J`() {
        val sac = SacLettres.creer(NiveauLettres.EMILE)
        for (l in setOf('X', 'Y', 'Z', 'W', 'K', 'Q', 'H', 'J')) {
            assertEquals(0, sac.restant(l))
        }
    }

    @Test
    fun `tirage sans remise decremente le stock`() {
        val sac = SacLettres.creer(NiveauLettres.MATHIEU)
        val avant = sac.restant('E')
        var tentative = 0
        var lettre: Char
        do {
            lettre = sac.tirerVoyelle(Random(tentative))
            tentative++
        } while (lettre != 'E' && tentative < 1000)
        // Peu importe le nombre d'essais, le total du sac diminue à chaque tirage réel.
        assertTrue(sac.total() < 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `leve une exception si plus aucune consonne disponible`() {
        val sac = SacLettres.creer(NiveauLettres.MATHIEU)
        // Épuise toutes les consonnes.
        val random = Random(1)
        repeat(100) {
            runCatching { sac.tirerConsonne(random) }
        }
        sac.tirerConsonne(random)
    }
}
