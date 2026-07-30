package fr.pierre.chiffreslettres.letters

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TirageLettresTest {

    @Test
    fun `le tirage respecte le nombre de voyelles demande`() {
        val random = Random(42)
        for (nombreVoyelles in TirageLettres.VOYELLES_MINIMUM..TirageLettres.VOYELLES_MAXIMUM) {
            val sac = AlphabetTestFixture.creerSac(NiveauLettres.MATHIEU)
            val tirees = TirageLettres.tirer(sac, nombreVoyelles, random = random)
            val nbVoyelles = tirees.count { it in AlphabetTestFixture.VOYELLES }
            assertEquals(nombreVoyelles, nbVoyelles)
            assertEquals(TirageLettres.NOMBRE_LETTRES, tirees.size)
        }
    }

    @Test
    fun `le tirage refuse un nombre de voyelles hors bornes`() {
        val sac = AlphabetTestFixture.creerSac(NiveauLettres.MATHIEU)
        assertThrows(IllegalArgumentException::class.java) {
            TirageLettres.tirer(sac, TirageLettres.VOYELLES_MINIMUM - 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            TirageLettres.tirer(sac, TirageLettres.VOYELLES_MAXIMUM + 1)
        }
    }
}
