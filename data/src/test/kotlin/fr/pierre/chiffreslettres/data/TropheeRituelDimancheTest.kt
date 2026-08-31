package fr.pierre.chiffreslettres.data

import java.time.LocalDate
import java.util.TreeSet
import org.junit.Assert.assertEquals
import org.junit.Test

class TropheeRituelDimancheTest {

    private fun dimanche(iso: String) = LocalDate.parse(iso)

    @Test
    fun `plusLongueSerieDeDimanches renvoie 0 sur un ensemble vide`() {
        assertEquals(0, plusLongueSerieDeDimanches(TreeSet()))
    }

    @Test
    fun `plusLongueSerieDeDimanches compte les dimanches consecutifs`() {
        val dimanches = TreeSet(listOf("2026-08-02", "2026-08-09", "2026-08-16", "2026-08-23").map(::dimanche))
        assertEquals(4, plusLongueSerieDeDimanches(dimanches))
    }

    @Test
    fun `plusLongueSerieDeDimanches garde la plus longue serie malgre un trou`() {
        val dimanches = TreeSet(
            listOf("2026-07-05", "2026-07-12", "2026-08-02", "2026-08-09", "2026-08-16").map(::dimanche),
        )
        assertEquals(3, plusLongueSerieDeDimanches(dimanches))
    }

    @Test
    fun `serieEnCoursDeDimanches compte en remontant depuis le dimanche de la semaine courante`() {
        val dimanches = setOf("2026-08-09", "2026-08-16", "2026-08-23").map(::dimanche).toSet()
        // "aujourd'hui" est le dimanche 23 lui-même.
        assertEquals(3, serieEnCoursDeDimanches(dimanches, dimanche("2026-08-23")))
    }

    @Test
    fun `serieEnCoursDeDimanches regarde la semaine precedente si celle-ci n'a pas encore de partie`() {
        val dimanches = setOf("2026-08-09", "2026-08-16").map(::dimanche).toSet()
        // "aujourd'hui" (mercredi 26) : le dimanche de cette semaine (23) n'a pas encore de
        // partie, on ne casse pas la série, on regarde le dimanche précédent (16).
        assertEquals(2, serieEnCoursDeDimanches(dimanches, dimanche("2026-08-26")))
    }

    @Test
    fun `serieEnCoursDeDimanches tombe a 0 si un dimanche a ete rate avant la semaine precedente`() {
        val dimanches = setOf("2026-07-05", "2026-07-12").map(::dimanche).toSet()
        assertEquals(0, serieEnCoursDeDimanches(dimanches, dimanche("2026-08-26")))
    }
}
