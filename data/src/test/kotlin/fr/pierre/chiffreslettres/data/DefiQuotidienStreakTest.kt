package fr.pierre.chiffreslettres.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DefiQuotidienStreakTest {

    private fun jour(iso: String) = LocalDate.parse(iso)

    @Test
    fun `plusLongueSerieDeJours renvoie 0 sur une liste vide`() {
        assertEquals(0, plusLongueSerieDeJours(emptyList()))
    }

    @Test
    fun `plusLongueSerieDeJours compte les jours consecutifs`() {
        val jours = listOf("2026-07-01", "2026-07-02", "2026-07-03").map(::jour)
        assertEquals(3, plusLongueSerieDeJours(jours))
    }

    @Test
    fun `plusLongueSerieDeJours garde la plus longue serie malgre un trou`() {
        val jours = listOf(
            "2026-06-01", "2026-06-02", "2026-06-03", "2026-06-04",
            "2026-07-10", "2026-07-11",
        ).map(::jour)
        assertEquals(4, plusLongueSerieDeJours(jours))
    }

    @Test
    fun `serieEnCoursDeJours compte en remontant depuis aujourd'hui`() {
        val jours = setOf("2026-07-05", "2026-07-06", "2026-07-07").map(::jour).toSet()
        assertEquals(3, serieEnCoursDeJours(jours, jour("2026-07-07")))
    }

    @Test
    fun `serieEnCoursDeJours regarde hier si le jour du defi n'est pas encore joue`() {
        val jours = setOf("2026-07-05", "2026-07-06").map(::jour).toSet()
        // "aujourd'hui" (07) n'a pas encore de réussite : on ne casse pas la série, on regarde hier (06).
        assertEquals(2, serieEnCoursDeJours(jours, jour("2026-07-07")))
    }

    @Test
    fun `serieEnCoursDeJours tombe a 0 si un jour a ete rate avant hier`() {
        val jours = setOf("2026-07-01", "2026-07-02").map(::jour).toSet()
        assertEquals(0, serieEnCoursDeJours(jours, jour("2026-07-07")))
    }

    @Test
    fun `rangNiveau croit avec la difficulte, chiffres et lettres confondus`() {
        // Retour utilisateur : rejouer un niveau supérieur le même jour doit remplacer le niveau
        // enregistré (voir DefiQuotidienRepository.enregistrerReussite), jamais le contraire.
        assertEquals(0, rangNiveau("EMILE"))
        assertEquals(1, rangNiveau("NESTOR"))
        assertEquals(2, rangNiveau("MONIQUE"))
        assertEquals(3, rangNiveau("MATHIEU"))
        assertEquals(true, rangNiveau("MATHIEU") > rangNiveau("MONIQUE"))
        assertEquals(true, rangNiveau("MONIQUE") > rangNiveau("NESTOR"))
    }

    @Test
    fun `rangNiveau renvoie -1 pour un niveau absent ou inconnu`() {
        assertEquals(-1, rangNiveau(null))
        assertEquals(-1, rangNiveau("INCONNU"))
        // -1 garantit qu'un premier niveau réussi (existante = null) est toujours "supérieur".
        assertEquals(true, rangNiveau("EMILE") > rangNiveau(null))
    }
}
