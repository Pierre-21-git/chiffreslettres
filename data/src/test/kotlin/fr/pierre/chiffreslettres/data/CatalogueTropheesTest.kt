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
    partiesDuelPointsJouees = 0,
    partiesDuelPointsGagnees = 0,
    duelPointsEcartVictoireMax = 0,
    duelPointsEcartDefaiteMax = 0,
    duelPointsCompteRondObtenu = false,
    meilleuresSeriesDefi = emptyMap(),
    meilleuresSeriesDefiNiveauMonique = emptyMap(),
    meilleuresSeriesDefiNiveauMathieu = emptyMap(),
    meilleuresReussitesDefiChrono = emptyMap(),
    meilleuresReussitesDefiChronoNiveauMonique = emptyMap(),
    meilleuresReussitesDefiChronoNiveauMathieu = emptyMap(),
    meilleurScoreDefiMotsMax = 0,
    meilleurScoreDefiMotsMaxNiveauMonique = 0,
    meilleurScoreDefiMotsMaxNiveauMathieu = 0,
    meilleurScoreDefiObjectifsPoints = 0,
    defiObjectifsPointsComplete = false,
    defiObjectifsPointsCompleteNiveauMonique = false,
    defiObjectifsPointsCompleteNiveauMathieu = false,
    meilleureSerieSansFaute = 0,
    meilleureSerieSansFauteNiveauMonique = 0,
    meilleureSerieSansFauteNiveauMathieu = 0,
    meilleureSerieJoursDefiQuotidien = 0,
    meilleureSerieJoursDefiQuotidienNiveauMonique = 0,
    meilleureSerieJoursDefiQuotidienNiveauMathieu = 0,
    serieEnCoursJoursDefiQuotidien = 0,
    serieEnCoursJoursDefiQuotidienNiveauMonique = 0,
    serieEnCoursJoursDefiQuotidienNiveauMathieu = 0,
    partiesSoloStructureeJouees = 0,
    defisJouesTotal = 0,
    ancienneteJoursProfil = 0,
    nombreNiveauxDistinctsJoues = 0,
    maxPartiesMemeJour = 0,
    cinqPartiesEnUneHeure = false,
    ecartDixDernieresPartiesFaible = false,
    premierePartieEntre5et7h = false,
    unePartieEntreMinuitEt5h = false,
    motRareJoue = false,
    palindromeJoue = false,
    motSymetriqueJoue = false,
    alphabetComplet = false,
    dimancheQuatreSemainesDeSuite = false,
    reglesDejaVues = false,
    nombreVisitesStats = 0,
    motInvalideDixLettresTente = false,
    egaliteDuelDejaObtenue = false,
    scoreSoloRepete = false,
    compteExactCibleNombrePremier = false,
    compteExactCalculMental = false,
    compteExactCheminMinimal = false,
    compteExactChirurgical = false,
    compteExactSpeedrun = false,
    compteExactVaTout = false,
    aucuneIdeeProposee = false,
    secondesJoueesTotal = 0,
)

class CatalogueTropheesTest {

    private fun trophee(id: String) = CatalogueTrophees.TOUS.first { it.id == id }

    @Test
    fun `165 trophees au total (155 + 7 duel points + 3 easter eggs)`() {
        assertEquals(165, CatalogueTrophees.TOUS.size)
    }

    @Test
    fun `defi Points a son propre bareme, adapte a un score maximal de 3 a 6`() {
        assertFalse(trophee("defi_points_1").estDebloque(statsVides()))
        assertTrue(trophee("defi_points_1").estDebloque(statsVides().copy(meilleurScoreDefiObjectifsPoints = 1)))
        assertFalse(trophee("defi_points_3").estDebloque(statsVides().copy(meilleurScoreDefiObjectifsPoints = 2)))
        assertTrue(trophee("defi_points_3").estDebloque(statsVides().copy(meilleurScoreDefiObjectifsPoints = 3)))
        assertEquals(Palier.BRONZE, trophee("defi_points_1").palier)
        assertEquals(Palier.ARGENT, trophee("defi_points_3").palier)

        assertFalse(trophee("defi_points_complet").estDebloque(statsVides()))
        assertTrue(trophee("defi_points_complet").estDebloque(statsVides().copy(defiObjectifsPointsComplete = true)))
        assertEquals(Palier.OR, trophee("defi_points_complet").palier)

        assertFalse(trophee("defi_points_complet_monique").estDebloque(statsVides()))
        assertTrue(trophee("defi_points_complet_monique").estDebloque(statsVides().copy(defiObjectifsPointsCompleteNiveauMonique = true)))
        assertEquals(Palier.PLATINE, trophee("defi_points_complet_monique").palier)

        assertFalse(trophee("defi_points_complet_mathieu").estDebloque(statsVides()))
        assertTrue(trophee("defi_points_complet_mathieu").estDebloque(statsVides().copy(defiObjectifsPointsCompleteNiveauMathieu = true)))
        assertEquals(Palier.EMERAUDE, trophee("defi_points_complet_mathieu").palier)
    }

    @Test
    fun `easter eggs chiffres se declenchent sur leurs conditions`() {
        assertTrue(trophee("easter_nombre_premier").estDebloque(statsVides().copy(compteExactCibleNombrePremier = true)))
        assertTrue(trophee("easter_calcul_mental").estDebloque(statsVides().copy(compteExactCalculMental = true)))
        assertTrue(trophee("easter_chemin_minimal").estDebloque(statsVides().copy(compteExactCheminMinimal = true)))
        assertTrue(trophee("easter_chirurgical").estDebloque(statsVides().copy(compteExactChirurgical = true)))
        assertTrue(trophee("easter_speedrun").estDebloque(statsVides().copy(compteExactSpeedrun = true)))
        assertTrue(trophee("easter_va_tout").estDebloque(statsVides().copy(compteExactVaTout = true)))
        assertTrue(trophee("easter_aucune_idee").estDebloque(statsVides().copy(aucuneIdeeProposee = true)))
        assertFalse(trophee("easter_cent_heures").estDebloque(statsVides().copy(secondesJoueesTotal = 359_999)))
        assertTrue(trophee("easter_cent_heures").estDebloque(statsVides().copy(secondesJoueesTotal = 360_000)))
    }

    @Test
    fun `aucun trophee n'a de palier sauf le catalogue principal (les easter eggs sont hors echelle)`() {
        val easterEggs = CatalogueTrophees.TOUS.filter { it.id.startsWith("easter_") }
        assertEquals(33, easterEggs.size)
        assertTrue(easterEggs.all { it.palier == null })
    }

    @Test
    fun `ex-aequo et symetrie se declenchent sur leurs conditions`() {
        assertTrue(trophee("easter_ex_aequo").estDebloque(statsVides().copy(egaliteDuelDejaObtenue = true)))
        assertFalse(trophee("easter_ex_aequo").estDebloque(statsVides()))
        assertTrue(trophee("easter_symetrie").estDebloque(statsVides().copy(scoreSoloRepete = true)))
        assertFalse(trophee("easter_symetrie").estDebloque(statsVides()))
    }

    @Test
    fun `mot invalide de 10 lettres se declenche sur sa condition`() {
        assertTrue(trophee("easter_mot_invalide_dix_lettres").estDebloque(statsVides().copy(motInvalideDixLettresTente = true)))
        assertFalse(trophee("easter_mot_invalide_dix_lettres").estDebloque(statsVides()))
    }

    @Test
    fun `curieux et data-lover se declenchent sur leurs conditions`() {
        assertTrue(trophee("easter_curieux").estDebloque(statsVides().copy(reglesDejaVues = true)))
        assertFalse(trophee("easter_curieux").estDebloque(statsVides()))
        assertTrue(trophee("easter_data_lover").estDebloque(statsVides().copy(nombreVisitesStats = 100)))
        assertFalse(trophee("easter_data_lover").estDebloque(statsVides().copy(nombreVisitesStats = 99)))
    }

    @Test
    fun `easter eggs groupe 1 se declenchent sur leurs conditions`() {
        assertTrue(trophee("easter_ancien_combattant").estDebloque(statsVides().copy(ancienneteJoursProfil = 400)))
        assertFalse(trophee("easter_ancien_combattant").estDebloque(statsVides().copy(ancienneteJoursProfil = 100)))
        assertTrue(trophee("easter_multi_niveaux").estDebloque(statsVides().copy(nombreNiveauxDistinctsJoues = 4)))
        assertTrue(trophee("easter_marathon").estDebloque(statsVides().copy(maxPartiesMemeJour = 21)))
        assertFalse(trophee("easter_marathon").estDebloque(statsVides().copy(maxPartiesMemeJour = 20)))
        assertTrue(trophee("easter_palindrome").estDebloque(statsVides().copy(palindromeJoue = true)))
        assertTrue(trophee("easter_alphabet_complet").estDebloque(statsVides().copy(alphabetComplet = true)))
        // Méta-easter-eggs (sentinel, jamais déclenchés via les stats seules).
        assertFalse(trophee("easter_polyvalent").estDebloque(statsVides()))
        assertFalse(trophee("easter_specialiste_complet").estDebloque(statsVides()))
        assertFalse(trophee("easter_noce_de_chene").estDebloque(statsVides()))
        assertFalse(trophee("easter_toit_du_monde").estDebloque(statsVides()))
        // Les easter eggs n'ont pas de palier (retour utilisateur) : ce ne sont pas des jalons
        // de la progression Bronze→Diamant.
        assertEquals(null, trophee("easter_toit_du_monde").palier)
        assertEquals(null, trophee("easter_polyvalent").palier)
    }

    @Test
    fun `touche-a-tout exige les 5 modes distincts`() {
        val presqueTout = statsVides().copy(
            partiesSoloStructureeJouees = 1,
            partiesConfrontationJouees = 1,
            partiesDuoJouees = 1,
            partiesDuelMotsJouees = 1,
            defisJouesTotal = 0,
        )
        assertFalse(trophee("easter_touche_a_tout").estDebloque(presqueTout))
        assertTrue(trophee("easter_touche_a_tout").estDebloque(presqueTout.copy(defisJouesTotal = 1)))
    }

    @Test
    fun `meta-trophees de section ne se declenchent jamais via les stats seules`() {
        // Sentinel : leur condition dépend des autres trophées débloqués, pas de TropheeStats
        // (déblocage géré à part dans TropheeRepository.reevaluer).
        assertFalse(trophee("section_defi_complete").estDebloque(statsVides()))
        assertFalse(trophee("section_partie_complete").estDebloque(statsVides()))
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
        assertEquals(Palier.EMERAUDE, trophee("compte_exact_200").palier)
        assertEquals(Palier.SAPHIR, trophee("compte_exact_500").palier)
        assertEquals(Palier.RUBIS, trophee("compte_exact_1000").palier)
        assertEquals(Palier.DIAMANT, trophee("compte_exact_2000").palier)
    }

    @Test
    fun `paliers parties terminees, 1 10 50 100 200`() {
        assertTrue(trophee("parties_50").estDebloque(statsVides().copy(partiesSoloTotal = 50)))
        assertFalse(trophee("parties_50").estDebloque(statsVides().copy(partiesSoloTotal = 49)))
        assertTrue(trophee("parties_200").estDebloque(statsVides().copy(partiesSoloTotal = 200)))
        assertEquals(Palier.OR, trophee("parties_50").palier)
        assertEquals(Palier.EMERAUDE, trophee("parties_150").palier)
        assertEquals(Palier.SAPHIR, trophee("parties_200").palier)
        assertEquals(Palier.RUBIS, trophee("parties_250").palier)
        assertEquals(Palier.DIAMANT, trophee("parties_500").palier)
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
    fun `serie de defi gagne platine a 10 reussites niveau Monique, puis emeraude-saphir-rubis-diamant niveau Mathieu`() {
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
        assertEquals(Palier.EMERAUDE, trophee("defi_serie_chiffres_12_mathieu").palier)
        assertEquals(Palier.SAPHIR, trophee("defi_serie_chiffres_15_mathieu").palier)
        assertEquals(Palier.RUBIS, trophee("defi_serie_chiffres_20_mathieu").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_serie_chiffres_25_mathieu").palier)
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
    fun `defi chrono a le meme bareme 3-5-8 que defi serie mais une echelle niveau plus courte`() {
        val stats = statsVides().copy(meilleuresReussitesDefiChrono = mapOf("LETTRES" to 8))
        assertTrue(trophee("defi_chrono_lettres_8").estDebloque(stats))
        assertEquals(Palier.OR, trophee("defi_chrono_lettres_8").palier)

        val statsMathieu = statsVides().copy(meilleuresReussitesDefiChronoNiveauMathieu = mapOf("LETTRES" to 12))
        assertTrue(trophee("defi_chrono_lettres_12_mathieu").estDebloque(statsMathieu))
        assertFalse(trophee("defi_chrono_lettres_10_monique").estDebloque(statsVides()))
        // Pas de Platine pour chrono : le jalon Monique+ saute directement à Émeraude.
        assertEquals(Palier.EMERAUDE, trophee("defi_chrono_lettres_10_monique").palier)
        assertEquals(Palier.SAPHIR, trophee("defi_chrono_lettres_12_mathieu").palier)
        assertEquals(Palier.RUBIS, trophee("defi_chrono_lettres_15_mathieu").palier)
    }

    @Test
    fun `defi mots max a desormais le meme bareme que serie sans-faute, 3-5-8 puis niveau Monique-Mathieu`() {
        assertTrue(trophee("defi_mots_max_3").estDebloque(statsVides().copy(meilleurScoreDefiMotsMax = 3)))
        assertFalse(trophee("defi_mots_max_5").estDebloque(statsVides().copy(meilleurScoreDefiMotsMax = 3)))
        assertEquals(Palier.BRONZE, trophee("defi_mots_max_3").palier)
        assertEquals(Palier.ARGENT, trophee("defi_mots_max_5").palier)
        assertEquals(Palier.OR, trophee("defi_mots_max_8").palier)
        assertEquals(Palier.PLATINE, trophee("defi_mots_max_10_monique").palier)
        assertEquals(Palier.EMERAUDE, trophee("defi_mots_max_12_mathieu").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_mots_max_25_mathieu").palier)
        assertTrue(trophee("defi_mots_max_10_monique").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMonique = 10)))
        assertFalse(trophee("defi_mots_max_25_mathieu").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMathieu = 24)))
        assertTrue(trophee("defi_mots_max_25_mathieu").estDebloque(statsVides().copy(meilleurScoreDefiMotsMaxNiveauMathieu = 25)))
    }

    @Test
    fun `defi sans faute a le bareme 3 bronze, 5 argent, 8 or, plus jalons niveau Monique et Mathieu`() {
        assertTrue(trophee("defi_sans_faute_3").estDebloque(statsVides().copy(meilleureSerieSansFaute = 3)))
        assertFalse(trophee("defi_sans_faute_5").estDebloque(statsVides().copy(meilleureSerieSansFaute = 3)))
        assertEquals(Palier.BRONZE, trophee("defi_sans_faute_3").palier)
        assertEquals(Palier.ARGENT, trophee("defi_sans_faute_5").palier)
        assertEquals(Palier.OR, trophee("defi_sans_faute_8").palier)
        assertEquals(Palier.PLATINE, trophee("defi_sans_faute_10_monique").palier)
        assertEquals(Palier.EMERAUDE, trophee("defi_sans_faute_12_mathieu").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_sans_faute_25_mathieu").palier)
        assertTrue(trophee("defi_sans_faute_12_mathieu").estDebloque(statsVides().copy(meilleureSerieSansFauteNiveauMathieu = 12)))
        assertFalse(trophee("defi_sans_faute_12_mathieu").estDebloque(statsVides().copy(meilleureSerieSansFauteNiveauMathieu = 11)))
    }

    @Test
    fun `defi quotidien a des paliers a 7, 14 et 21 jours (1-2-3 semaines), bronze argent or`() {
        val stats = statsVides().copy(meilleureSerieJoursDefiQuotidien = 10)
        assertTrue(trophee("defi_quotidien_7").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_14").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_21").estDebloque(stats))
        assertEquals(Palier.BRONZE, trophee("defi_quotidien_7").palier)
        assertEquals(Palier.ARGENT, trophee("defi_quotidien_14").palier)
        assertEquals(Palier.OR, trophee("defi_quotidien_21").palier)
    }

    @Test
    fun `defi quotidien 28 jours niveau eleve gagne platine ou emeraude, puis jusqu'a diamant a 70 jours Mathieu`() {
        // 28 jours tous niveaux confondus ne suffit pas aux jalons niveau-gatés.
        val stats = statsVides().copy(meilleureSerieJoursDefiQuotidien = 28)
        assertFalse(trophee("defi_quotidien_28_monique").estDebloque(stats))
        assertFalse(trophee("defi_quotidien_28_mathieu").estDebloque(stats))

        val statsMonique = stats.copy(meilleureSerieJoursDefiQuotidienNiveauMonique = 28)
        assertTrue(trophee("defi_quotidien_28_monique").estDebloque(statsMonique))
        assertFalse(trophee("defi_quotidien_28_mathieu").estDebloque(statsMonique))

        val statsMathieu = statsMonique.copy(meilleureSerieJoursDefiQuotidienNiveauMathieu = 70)
        assertTrue(trophee("defi_quotidien_28_mathieu").estDebloque(statsMathieu))
        assertTrue(trophee("defi_quotidien_70_mathieu").estDebloque(statsMathieu))
        assertEquals(Palier.PLATINE, trophee("defi_quotidien_28_monique").palier)
        assertEquals(Palier.EMERAUDE, trophee("defi_quotidien_28_mathieu").palier)
        assertEquals(Palier.SAPHIR, trophee("defi_quotidien_42_mathieu").palier)
        assertEquals(Palier.RUBIS, trophee("defi_quotidien_56_mathieu").palier)
        assertEquals(Palier.DIAMANT, trophee("defi_quotidien_70_mathieu").palier)
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

    @Test
    fun `trophees duel points progressent avec les parties jouees et gagnees`() {
        assertFalse(trophee("duel_points_1").estDebloque(statsVides()))
        assertTrue(trophee("duel_points_1").estDebloque(statsVides().copy(partiesDuelPointsJouees = 1)))
        assertFalse(trophee("duel_points_gagnee_10").estDebloque(statsVides().copy(partiesDuelPointsGagnees = 9)))
        assertTrue(trophee("duel_points_gagnee_10").estDebloque(statsVides().copy(partiesDuelPointsGagnees = 10)))
        assertEquals(Palier.DIAMANT, trophee("duel_points_gagnee_100").palier)
    }

    @Test
    fun `easter eggs duel points, compte rond rouleau compresseur et deculottee`() {
        assertTrue(trophee("easter_compte_rond").estDebloque(statsVides().copy(duelPointsCompteRondObtenu = true)))
        assertFalse(trophee("easter_rouleau_compresseur").estDebloque(statsVides().copy(duelPointsEcartVictoireMax = 19)))
        assertTrue(trophee("easter_rouleau_compresseur").estDebloque(statsVides().copy(duelPointsEcartVictoireMax = 20)))
        assertFalse(trophee("easter_deculottee").estDebloque(statsVides().copy(duelPointsEcartDefaiteMax = 19)))
        assertTrue(trophee("easter_deculottee").estDebloque(statsVides().copy(duelPointsEcartDefaiteMax = 20)))
    }

    @Test
    fun `rangJoueur est cumulatif et suit les nouveaux paliers Emeraude Saphir Rubis`() {
        val tropheesAPalier = CatalogueTrophees.TOUS.filter { it.palier != null }
        assertEquals(null, CatalogueTrophees.rangJoueur(emptySet()))
        val bronzeSeulement = tropheesAPalier.filter { it.palier == Palier.BRONZE }.map { it.id }.toSet()
        assertEquals(Palier.BRONZE, CatalogueTrophees.rangJoueur(bronzeSeulement))
        val tousSaufDiamant = tropheesAPalier.filter { it.palier != Palier.DIAMANT }.map { it.id }.toSet()
        assertEquals(Palier.RUBIS, CatalogueTrophees.rangJoueur(tousSaufDiamant))
        val tous = tropheesAPalier.map { it.id }.toSet()
        assertEquals(Palier.DIAMANT, CatalogueTrophees.rangJoueur(tous))
    }

    @Test
    fun `icone dediee pour chaque easter egg, trophee generique pour le catalogue gradue`() {
        assertEquals("🏔️", CatalogueTrophees.iconeTrophee("easter_toit_du_monde"))
        assertEquals("➗", CatalogueTrophees.iconeTrophee("easter_nombre_premier"))
        assertEquals("🏆", CatalogueTrophees.iconeTrophee("compte_exact_1"))
        val easterEggs = CatalogueTrophees.TOUS.filter { it.id.startsWith("easter_") }
        assertTrue(easterEggs.all { CatalogueTrophees.iconeTrophee(it.id) != "🏆" })
    }

    @Test
    fun `les trois grands blocs de l'ecran trophees couvrent exactement toutes les categories`() {
        // Régression pour TropheesScreen.kt : chaque CategorieTrophee doit atterrir dans un des
        // trois grands blocs (parties et duels / défis / trophées spéciaux), sans doublon ni oubli.
        val couvertes = CatalogueTrophees.CATEGORIES_SECTION_PARTIE + CatalogueTrophees.CATEGORIES_SECTION_DEFI +
            setOf(
                CategorieTrophee.TROPHEES_SPECIAUX, CategorieTrophee.EASTER_CHIFFRES, CategorieTrophee.EASTER_LETTRES,
                CategorieTrophee.EASTER_GENERAL, CategorieTrophee.EASTER_ULTIME,
            )
        assertEquals(CategorieTrophee.entries.toSet(), couvertes)
    }

    @Test
    fun `TROPHEES_SPECIAUX ne contient que les deux meta-trophees de section`() {
        val ids = CatalogueTrophees.TOUS.filter { it.categorie == CategorieTrophee.TROPHEES_SPECIAUX }.map { it.id }.toSet()
        assertEquals(setOf("section_defi_complete", "section_partie_complete"), ids)
    }

    @Test
    fun `exactement 8 trophees sont INVISIBLE, masques tant qu'ils ne sont pas debloques`() {
        val invisibles = CatalogueTrophees.TOUS.filter { it.niveauVisibilite == NiveauVisibilite.INVISIBLE }
        assertEquals(8, invisibles.size)
    }
}
