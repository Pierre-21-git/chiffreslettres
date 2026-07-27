package fr.pierre.chiffreslettres.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefiQuotidienTirageTest {

    @Test
    fun `meme profil et meme jour donnent toujours le meme tirage`() {
        val t1 = DefiQuotidienTirage.pour(profilId = 1, jour = "2026-07-28")
        val t2 = DefiQuotidienTirage.pour(profilId = 1, jour = "2026-07-28")
        assertEquals(t1, t2)
    }

    @Test
    fun `des jours differents peuvent donner des tirages differents`() {
        val tirages = (1..30).map { jour -> DefiQuotidienTirage.pour(profilId = 1, jour = "2026-07-$jour") }
        assertTrue("le tirage ne doit pas être figé sur 30 jours", tirages.toSet().size > 1)
    }

    @Test
    fun `des profils differents peuvent avoir un tirage different le meme jour`() {
        val tirages = (1L..30L).map { profilId -> DefiQuotidienTirage.pour(profilId, jour = "2026-07-28") }
        assertTrue("le tirage ne doit pas être identique pour tous les profils", tirages.toSet().size > 1)
    }

    @Test
    fun `objectif serie dans la plage 5 a 10`() {
        for (jour in 1..28) {
            val tirage = DefiQuotidienTirage.pour(profilId = 42, jour = "2026-0${1 + jour % 9}-${"%02d".format(jour)}")
            if (tirage.type == TypeDefi.SERIE) {
                assertTrue("objectif=${tirage.objectif}", tirage.objectif in 5..10)
            } else {
                assertTrue("objectif=${tirage.objectif}", tirage.objectif in 4..8)
            }
        }
    }
}
