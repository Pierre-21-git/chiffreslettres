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
    partiesMotsMinNiveauMathieu = mapOf(7 to false, 8 to false),
    partiesParSeuilScore = mapOf(20 to 0, 30 to 0, 40 to 0, 50 to 0, 60 to 0, 70 to 0, 80 to 0, 90 to 0),
    partiesSoloTotal = 0,
    partiesDuoJouees = 0,
    partiesDuoGagnees = 0,
    partiesConfrontationJouees = 0,
    partiesConfrontationGagnees = 0,
    partiesDuelMotsJouees = 0,
    partiesDuelMotsGagnees = 0,
    partiesDuelMotsConfrontationJouees = 0,
    partiesDuelMotsConfrontationGagnees = 0,
    defisTotal = 0,
    meilleuresSeriesDefi = emptyMap(),
    meilleuresSeriesDefiNiveauMonique = emptyMap(),
    meilleuresSeriesDefiNiveauMathieu = emptyMap(),
    meilleuresReussitesDefiChrono = emptyMap(),
    meilleuresReussitesDefiChronoNiveauMonique = emptyMap(),
    meilleuresReussitesDefiChronoNiveauMathieu = emptyMap(),
    meilleurScoreDefiMotsMax = 0,
    meilleurScoreDefiMotsMaxNiveauMonique = 0,
    meilleurScoreDefiMotsMaxNiveauMathieu = 0,
    meilleureSerieSansFaute = 0,
    meilleureSerieSansFauteNiveauMonique = 0,
    meilleureSerieSansFauteNiveauMathieu = 0,
    meilleureSerieJoursDefiQuotidien = 0,
    meilleureSerieJoursDefiQuotidienNiveauMonique = 0,
    meilleureSerieJoursDefiQuotidienNiveauMathieu = 0,
)

class CatalogueTropheesTest {

    private fun trophee(id: String) = CatalogueTrophees.TOUS.first { it.id == id }

    @Test
    fun `88 trophees au total`() {
        assertEquals(88, CatalogueTrophees.TOUS.size)
    }

    @Test
    fun `ids uniques`() {
        val ids = CatalogueTrophees.TOUS.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `paliers comptes exacts, 1 10 50 100 200`() {
        assertFalse(trophee("compte_exact_1").estDebloque(statsVides().copy(comptesExacts = 0)))
        assertTrue(trophee("compte_exact_1").estDebloque(statsVides().copy(comptesExacts = 1)))
        assertFalse(trophee("compte_exact_10").estDebloque(statsVides().copy(comptesExacts = 9)))
        assertTrue(trophee("compte_exact_10").estDebloque(statsVides().copy(comptesExacts = 10)))
        assertFalse(trophee("compte_exact_50").estDebloque(statsVides().copy(comptesExacts = 49)))
        assertTrue(trophee("compte_exact_50").estDebloque(statsVides().copy(comptesExacts = 50)))
        assertFalse(trophee("compte_exact_100").estDebloque(statsVides().copy(comptesExacts = 99)))
        assertTrue(trophee("compte_exact_100").estDebloque(statsVides().copy(comptesExacts = 100)))
        assertFalse(trophee("compte_exact_200").estDebloque(statsVides().copy(comptesExacts = 199)))
        assertTrue(trophee("compte_exact_200").estDebloque(statsVides().copy(comptesExacts = 200)))
        assertEquals(Palier.BRONZE, trophee("compte_exact_1").palier)
        assertEquals(Palier.ARGENT, trophee("compte_exact_10").palier)
        assertEquals(Palier.OR, trophee("compte_exact_50").palier)
        assertEquals(Palier.PLATINE, trophee("compte_exact_100").palier)
        assertEquals(Palier.DIAMANT, trophee("compte_exact_200").palier)
    }

    @Test
    fun `paliers parties terminees, 1 10 50 100 200`() {
        assertTrue(trophee("parties_50").estDebloque(statsVides().copy(partiesSoloTotal = 50)))
        assertFalse(trophee("parties_50").estDebloque(statsVides().copy(partiesSoloTotal = 49)))
        assertTrue(trophee("parties_200").estDebloque(statsVides().copy(partiesSoloTotal = 200)))
        assertEquals(Palier.OR, trophee("parties_50").palier)
        assertEquals(Palier.DIAMANT, trophee("parties_200").palier)
    }

    @Test
    fun `partie mots min ne se declenche que pour le seuil concerne`() {
        val stats = statsVides().copy(partiesMotsMin = mapOf(4 to true, 5 to false, 6 to false, 7 to false, 8 to false))
        assertTrue(trophee("partie_mots_min_4").estDebloque(stats))
        assertFalse(trophee("partie_mots_min_5").estDebloque(stats))
    }

    @Test
    fun `partie mots min 7 et 8 exigent le niveau Mathieu`() {
        // Une partie mots >= 7 hors niveau Mathieu (partiesMotsMin) ne suffit pas au trophée.
        val stats = statsVides().copy(
            partiesMotsMin = mapOf(4 to false, 5 to false, 6 to false, 7 to true, 8 to true),
            partiesMotsMinNiveauMathieu = mapOf(7 to false, 8 to false),
        )
        assertFalse(trophee("partie_mots_min_7").estDebloque(stats))
        assertFalse(trophee("partie_mots_min_8").estDebloque(stats))
        val statsMathieu = stats.copy(partiesMotsMinNiveauMathieu = mapOf(7 to true, 8 to false))
        assertTrue(trophee("partie_mots_min_7").estDebloque(statsMathieu))
        assertFalse(trophee("partie_mots_min_8").estDebloque(statsMathieu))
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
    fun `serie de defi a un bareme 3 bronze, 5 argent, 8 or`() {
        val stats = statsVides().copy(meilleuresSeriesDefi = mapOf("LETTRES" to 8))
        assertTrue(trophee("defi_serie_lettres_8").estDebloque(stats))
        assertEquals(Palier.BRONZE, trophee("defi_serie_lettres_3").palier)
        assertEquals(Palier.ARGENT, trophee("defi_serie_lettres_5").palier)
        assertEquals(Palier.OR, trophee("defi_serie_lettres_8").palier)
    }

    @Test
    fun `serie de defi gagne platine a 10 reussites niveau Monique et diamant a 12 niveau Mathieu`() {
        // Une série tous niveaux confondus ne suffit pas aux jalons niveau-gatés.
        val stats = statsVides().copy(
            meilleuresSeriesDefi = mapOf("CHIFFRES" to 12),
            meilleuresSeriesDefiNiveauMonique = emptyMap(),
            meilleuresSeriesDefiNiveauMathieu = emptyMap(),
        )
        assertFalse(trophee("defi_serie_chiffres_10_monique").estDebloque(stats))
        assertFalse(trophee("defi_serie_chiffres_12_mathieu").estDebloque(stats))

        val statsMonique = stats.copy(meilleuresSeriesDefiNiveauMonique = mapOf("CHIFFRES" to 10))
        assertTrue(trophee("defi_serie_chiffres_10_monique").estDebloque(statsMonique))
        assertFalse(trophee("defi_serie_chiffres_12_mathieu").estDebloque(statsMonique))

        val statsMathieu = statsMonique.copy(meilleuresSeriesDefiNiveauMathieu = mapOf("CHIFFRES" to 12))
        assertFalse(trophee("defi_serie_chiffres_12_mathieu").estDebloque(statsMathieu.copy(meilleuresSeriesDefiNiveauMathieu = mapOf("CHIFFRES" to 11))))
        assertTrue(trophee("defi_serie_chiffres_12_mathieu").estDebloque(statsMathieu))
        assertEquals(Palier.PLATINE, trophee("defi_serie_chiffres_10_monique").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_serie_chiffres_12_mathieu").palier)
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
    fun `defi chrono a le meme bareme que defi serie`() {
        val stats = statsVides().copy(meilleuresReussitesDefiChrono = mapOf("LETTRES" to 8))
        assertTrue(trophee("defi_chrono_lettres_8").estDebloque(stats))
        assertEquals(Palier.OR, trophee("defi_chrono_lettres_8").palier)

        val statsMathieu = statsVides().copy(meilleuresReussitesDefiChronoNiveauMathieu = mapOf("LETTRES" to 12))
        assertTrue(trophee("defi_chrono_lettres_12_mathieu").estDebloque(statsMathieu))
        assertFalse(trophee("defi_chrono_lettres_10_monique").estDebloque(statsVides()))
        assertEquals(Palier.PLATINE, trophee("defi_chrono_lettres_10_monique").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_chrono_lettres_12_mathieu").palier)
    }

    @Test
    fun `defi mots max a le bareme 3 bronze, 5 argent, 10 or, plus 2 jalons niveau a 15 et 20`() {
        assertTrue(trophee("defi_mots_max_3").estDebloque(statsVides().copy(meilleurScoreDefiMotsMax = 3)))
        assertFalse(trophee("defi_mots_max_5").estDebloque(statsVides().copy(meilleurScoreDefiMotsMax = 3)))
        assertEquals(Palier.BRONZE, trophee("defi_mots_max_3").palier)
        assertEquals(Palier.ARGENT, trophee("defi_mots_max_5").palier)
        assertEquals(Palier.OR, trophee("defi_mots_max_10").palier)
        assertEquals(Palier.PLATINE, trophee("defi_mots_max_15_monique").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_mots_max_20_mathieu").palier)
        assertTrue(trophee("defi_mots_max_15_monique").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMonique = 15)))
        assertFalse(trophee("defi_mots_max_20_mathieu").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMathieu = 19)))
        assertTrue(trophee("defi_mots_max_20_mathieu").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMathieu = 20)))
    }

    @Test
    fun `defi sans faute a le bareme 3 bronze, 5 argent, 8 or, plus 2 jalons niveau a 10 et 12`() {
        assertTrue(trophee("defi_sans_faute_3").estDebloque(statsVides().copy(meilleureSerieSansFaute = 3)))
        assertFalse(trophee("defi_sans_faute_5").estDebloque(statsVides().copy(meilleureSerieSansFaute = 3)))
        assertEquals(Palier.BRONZE, trophee("defi_sans_faute_3").palier)
        assertEquals(Palier.ARGENT, trophee("defi_sans_faute_5").palier)
        assertEquals(Palier.OR, trophee("defi_sans_faute_8").palier)
        assertEquals(Palier.PLATINE, trophee("defi_sans_faute_10_monique").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_sans_faute_12_mathieu").palier)
        assertTrue(trophee("defi_sans_faute_12_mathieu").estDebloque(statsVides().copy(meilleureSerieSansFauteNiveauMathieu = 12)))
        assertFalse(trophee("defi_sans_faute_12_mathieu").estDebloque(statsVides().copy(meilleureSerieSansFauteNiveauMathieu = 11)))
    }

    @Test
    fun `defi quotidien a des paliers a 7, 14 et 30 jours, bronze argent or`() {
        val stats = statsVides().copy(meilleureSerieJoursDefiQuotidien = 10)
        assertTrue(trophee("defi_quotidien_7").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_14").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_30").estDebloque(stats))
        assertEquals(Palier.BRONZE, trophee("defi_quotidien_7").palier)
        assertEquals(Palier.ARGENT, trophee("defi_quotidien_14").palier)
        assertEquals(Palier.OR, trophee("defi_quotidien_30").palier)
    }

    @Test
    fun `defi quotidien 30 jours niveau eleve gagne platine ou diamant`() {
        // 30 jours tous niveaux confondus ne suffit pas aux jalons niveau-gatés.
        val stats = statsVides().copy(meilleureSerieJoursDefiQuotidien = 30)
        assertFalse(trophee("defi_quotidien_30_monique").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_30_mathieu").estDebloque(stats))

        val statsMonique = stats.copy(meilleureSerieJoursDefiQuotidienNiveauMonique = 30)
        assertTrue(trophee("defi_quotidien_30_monique").estDebloque(statsMonique))
        assertFalse(trophee("defi_quotidien_30_mathieu").estDebloque(statsMonique))

        val statsMathieu = statsMonique.copy(meilleureSerieJoursDefiQuotidienNiveauMathieu = 30)
        assertTrue(trophee("defi_quotidien_30_mathieu").estDebloque(statsMathieu))
        assertEquals(Palier.PLATINE, trophee("defi_quotidien_30_monique").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_quotidien_30_mathieu").palier)
    }

    @Test
    fun `trophees duo comptent aussi les parties confrontation`() {
        // Retour utilisateur : Duo et Confrontation partagent les mêmes trophées "duo_*".
        val stats = statsVides().copy(partiesConfrontationJouees = 1, partiesConfrontationGagnees = 10)
        assertTrue(trophee("duo_1").estDebloque(stats))
        assertTrue(trophee("duo_gagnee_1").estDebloque(stats))
        assertTrue(trophee("duo_gagnee_10").estDebloque(stats))
    }

    @Test
    fun `dixieme partie duo gagnee cumule victoires duo et confrontation, palier platine`() {
        val stats = statsVides().copy(partiesDuoGagnees = 6, partiesConfrontationGagnees = 3)
        assertFalse(trophee("duo_gagnee_10").estDebloque(stats))
        assertTrue(trophee("duo_gagnee_10").estDebloque(stats.copy(partiesConfrontationGagnees = 4)))
        assertEquals(Palier.PLATINE, trophee("duo_gagnee_10").palier)
    }

    @Test
    fun `trophees duel mots cumulent duo et confrontation`() {
        val stats = statsVides().copy(partiesDuelMotsGagnees = 6, partiesDuelMotsConfrontationGagnees = 3)
        assertFalse(trophee("duel_mots_gagnee_10").estDebloque(stats))
        assertTrue(trophee("duel_mots_gagnee_10").estDebloque(stats.copy(partiesDuelMotsConfrontationGagnees = 4)))
        assertEquals(Palier.PLATINE, trophee("duel_mots_gagnee_10").palier)
    }
}
