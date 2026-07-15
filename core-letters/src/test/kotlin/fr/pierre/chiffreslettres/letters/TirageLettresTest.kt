package fr.pierre.chiffreslettres.letters

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TirageLettresTest {

    @Test
    fun `consonne autorisee tant qu'il reste assez de marge`() {
        // Aucune lettre tirée : 10 restantes, 2 voyelles nécessaires -> marge de 8, consonne libre.
        assertTrue(TirageLettres.consonneAutorisee(emptyList(), nombreLettres = 10))
    }

    @Test
    fun `consonne bloquee quand il ne reste que la marge exacte pour les voyelles`() {
        // 8 lettres déjà tirées, aucune voyelle parmi elles : il reste 2 tirages
        // pour 2 voyelles nécessaires -> plus de marge, consonne doit être bloquée.
        val dejaTirees = List(8) { 'B' } // 8 consonnes fictives
        assertFalse(TirageLettres.consonneAutorisee(dejaTirees, nombreLettres = 10))
    }

    @Test
    fun `consonne de nouveau libre une fois 2 voyelles atteintes`() {
        val dejaTirees = listOf('A', 'E') + List(5) { 'B' }
        assertTrue(TirageLettres.consonneAutorisee(dejaTirees, nombreLettres = 10))
    }

    @Test
    fun `le Y compte comme voyelle pour la regle du minimum`() {
        val dejaTirees = listOf('Y') + List(6) { 'B' } // 1 voyelle (Y) + 6 consonnes = 7 tirées
        // Il reste 3 tirages, 1 voyelle encore nécessaire -> marge de 2, consonne encore autorisée.
        assertTrue(TirageLettres.consonneAutorisee(dejaTirees, nombreLettres = 10))
        val dejaTireesLimite = listOf('Y') + List(8) { 'B' } // 9 tirées, encore 1 voyelle requise, 1 tirage restant
        assertFalse(TirageLettres.consonneAutorisee(dejaTireesLimite, nombreLettres = 10))
    }

    @Test
    fun `tirage force une voyelle meme si consonne demandee quand la regle l'impose`() {
        val sac = SacLettres.creer(NiveauLettres.MATHIEU)
        val dejaTirees = List(8) { 'B' }
        val lettre = TirageLettres.tirerProchaineLettre(
            sac,
            dejaTirees,
            consonneDemandee = true,
            nombreLettres = 10,
            random = Random(0),
        )
        assertTrue(lettre in SacLettres.VOYELLES)
    }

    @Test
    fun `un tirage complet respecte toujours le minimum de 2 voyelles`() {
        val random = Random(99)
        repeat(50) { seed ->
            val sac = SacLettres.creer(NiveauLettres.MATHIEU)
            val tirees = mutableListOf<Char>()
            repeat(TirageLettres.NOMBRE_LETTRES) {
                // Le joueur "essaie" toujours de prendre une consonne pour forcer le pire cas.
                val lettre = TirageLettres.tirerProchaineLettre(sac, tirees, consonneDemandee = true, random = random)
                tirees.add(lettre)
            }
            val nbVoyelles = tirees.count { it in SacLettres.VOYELLES }
            assertTrue("tirage $seed: seulement $nbVoyelles voyelles parmi $tirees", nbVoyelles >= TirageLettres.VOYELLES_MINIMUM)
            assertEquals(TirageLettres.NOMBRE_LETTRES, tirees.size)
        }
    }
}
