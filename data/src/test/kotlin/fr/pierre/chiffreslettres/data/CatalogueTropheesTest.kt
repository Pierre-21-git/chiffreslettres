package fr.pierre.chiffreslettres.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private fun statsVides() = TropheeStats(
    comptesExacts = 0,
    motsDixLettres = 0,
    partieTousComptesExacts = false,
    partiesMotsMin = mapOf(4 to false, 5 to false, 6 to false, 7 to false, 8 to false),
    partiesParSeuilScore = mapOf(20 to 0, 30 to 0, 40 to 0, 50 to 0, 60 to 0, 70 to 0, 80 to 0, 90 to 0),
    partiesSoloTotal = 0,
    niveauxSoloCouverts = 0,
    defisTotal = 0,
    meilleureSerieDefi = 0,
    combinaisonsDefiCouvertes = 0,
    meilleuresReussitesDefiChrono = emptyMap(),
)

class CatalogueTropheesTest {

    private fun trophee(id: String) = CatalogueTrophees.TOUS.first { it.id == id }

    @Test
    fun `46 trophees au total`() {
        assertEquals(46, CatalogueTrophees.TOUS.size)
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
    fun `defi niveaux complets exige les 8 combinaisons`() {
        assertFalse(trophee("defi_niveaux_complets").estDebloque(statsVides().copy(combinaisonsDefiCouvertes = 7)))
        assertTrue(trophee("defi_niveaux_complets").estDebloque(statsVides().copy(combinaisonsDefiCouvertes = 8)))
    }

    @Test
    fun `series de defi a paliers independants`() {
        val stats = statsVides().copy(meilleureSerieDefi = 4)
        assertTrue(trophee("defi_serie_3").estDebloque(stats))
        assertFalse(trophee("defi_serie_5").estDebloque(stats))
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
    fun `defi chrono a 5 paliers par mode`() {
        val stats = statsVides().copy(meilleuresReussitesDefiChrono = mapOf("LETTRES" to 11))
        assertTrue(trophee("defi_chrono_lettres_10").estDebloque(stats))
        assertFalse(trophee("defi_chrono_lettres_12").estDebloque(stats))
    }
}
