package fr.pierre.chiffreslettres.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private fun statsVides() = TropheeStats(
    comptesExacts = 0,
    motsParLongueur = (4..10).associateWith { 0 },
    partieTousComptesExacts = false,
    partiesMotsMin = mapOf(4 to false, 5 to false, 6 to false, 7 to false, 8 to false),
    partiesParSeuilScore = mapOf(20 to 0, 30 to 0, 40 to 0, 50 to 0, 60 to 0, 70 to 0, 80 to 0, 90 to 0),
    partiesSoloTotal = 0,
    defisTotal = 0,
    meilleuresSeriesDefi = emptyMap(),
    meilleuresReussitesDefiChrono = emptyMap(),
)

class CatalogueTropheesTest {

    private fun trophee(id: String) = CatalogueTrophees.TOUS.first { it.id == id }

    @Test
    fun `69 trophees au total`() {
        assertEquals(69, CatalogueTrophees.TOUS.size)
    }

    @Test
    fun `ids uniques`() {
        val ids = CatalogueTrophees.TOUS.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `paliers comptes exacts`() {
        assertFalse(trophee("compte_exact_1").estDebloque(statsVides().copy(comptesExacts = 0)))
        assertTrue(trophee("compte_exact_1").estDebloque(statsVides().copy(comptesExacts = 1)))
        assertFalse(trophee("compte_exact_10").estDebloque(statsVides().copy(comptesExacts = 9)))
        assertTrue(trophee("compte_exact_10").estDebloque(statsVides().copy(comptesExacts = 10)))
        assertFalse(trophee("compte_exact_100").estDebloque(statsVides().copy(comptesExacts = 99)))
        assertTrue(trophee("compte_exact_100").estDebloque(statsVides().copy(comptesExacts = 100)))
    }

    @Test
    fun `partie mots min ne se declenche que pour le seuil concerne`() {
        val stats = statsVides().copy(partiesMotsMin = mapOf(4 to true, 5 to false, 6 to false, 7 to false, 8 to false))
        assertTrue(trophee("partie_mots_min_4").estDebloque(stats))
        assertFalse(trophee("partie_mots_min_5").estDebloque(stats))
    }

    @Test
    fun `seuils de score sont stricts et separes par palier`() {
        val stats = statsVides().copy(partiesParSeuilScore = mapOf(20 to 12, 30 to 0))
        assertTrue(trophee("score_20_1").estDebloque(stats))
        assertTrue(trophee("score_20_10").estDebloque(stats))
        assertFalse(trophee("score_30_1").estDebloque(stats))
    }

    @Test
    fun `series de defi a paliers independants, par mode`() {
        val stats = statsVides().copy(meilleuresSeriesDefi = mapOf("CHIFFRES" to 4))
        assertTrue(trophee("defi_serie_chiffres_3").estDebloque(stats))
        assertFalse(trophee("defi_serie_chiffres_5").estDebloque(stats))
        // Un autre mode n'est pas affecté.
        assertFalse(trophee("defi_serie_lettres_3").estDebloque(stats))
    }

    @Test
    fun `series de defi va jusqu'a 50`() {
        val stats = statsVides().copy(meilleuresSeriesDefi = mapOf("LETTRES" to 32))
        assertTrue(trophee("defi_serie_lettres_30").estDebloque(stats))
        assertFalse(trophee("defi_serie_lettres_50").estDebloque(stats))
    }

    @Test
    fun `mots par longueur ont des paliers independants, de 4 a 10 lettres`() {
        val stats = statsVides().copy(motsParLongueur = mapOf(4 to 1, 10 to 12))
        assertTrue(trophee("mot_4_1").estDebloque(stats))
        assertFalse(trophee("mot_4_10").estDebloque(stats))
        assertTrue(trophee("mot_10_1").estDebloque(stats))
        assertTrue(trophee("mot_10_10").estDebloque(stats))
        assertFalse(trophee("mot_5_1").estDebloque(stats))
    }

    @Test
    fun `defi chrono a paliers independants par mode, tous niveaux confondus`() {
        val stats = statsVides().copy(meilleuresReussitesDefiChrono = mapOf("CHIFFRES" to 4))
        assertTrue(trophee("defi_chrono_chiffres_3").estDebloque(stats))
        assertFalse(trophee("defi_chrono_chiffres_5").estDebloque(stats))
        // Un autre mode n'est pas affecté.
        assertFalse(trophee("defi_chrono_lettres_3").estDebloque(stats))
    }

    @Test
    fun `defi chrono a 6 paliers par mode`() {
        val stats = statsVides().copy(meilleuresReussitesDefiChrono = mapOf("LETTRES" to 14))
        assertTrue(trophee("defi_chrono_lettres_12").estDebloque(stats))
        assertFalse(trophee("defi_chrono_lettres_15").estDebloque(stats))
    }
}
