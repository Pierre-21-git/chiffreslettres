package fr.pierre.chiffreslettres.data

import java.text.Normalizer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.SortedSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_MOTS_NIVEAU_MATHIEU = listOf(7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
private val LONGUEURS_MOTS_TROPHEE = 4..10
private val NIVEAUX_MONIQUE_OU_PLUS = listOf("MONIQUE", "MATHIEU")
private val NIVEAUX_MATHIEU = listOf("MATHIEU")

/**
 * Masque binaire des 4 opérations chiffres toutes utilisées (bits 0-3, un par valeur de l'enum
 * `Operation` du module `core-numbers`, inaccessible ici — `data` ne dépend jamais de `app` ni de
 * `core-numbers`) — easter egg "Boîte à outils".
 */
private const val TOUS_OPERATEURS_MASK = 0b1111

// --- Helpers pour les easter eggs (refonte 2026-08), en mémoire plutôt qu'en SQL : trop
// spécifiques pour mériter chacun leur propre requête, et les volumes de données par joueur
// restent modestes (un jeu familial, pas des millions de lignes).

private fun heureLocale(epochMillis: Long): Int = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).hour

private fun maxPartiesMemeJour(dates: List<Long>): Int =
    dates.map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
        .groupingBy { it }.eachCount().values.maxOrNull() ?: 0

/** Existe-t-il une fenêtre de 5 parties consécutives (triées par date) tenant en moins d'une heure ? */
private fun cinqPartiesEnUneHeure(datesTriees: List<Long>): Boolean {
    if (datesTriees.size < 5) return false
    for (indice in 0..datesTriees.size - 5) {
        if (datesTriees[indice + 4] - datesTriees[indice] <= 3_600_000L) return true
    }
    return false
}

/** Écart entre la meilleure et la moins bonne des 10 parties les plus RÉCENTES (retour utilisateur). */
private fun ecartDixDernieresPartiesFaible(scoresDuPlusRecentAuPlusAncien: List<Int>): Boolean {
    val dixDernieres = scoresDuPlusRecentAuPlusAncien.take(10)
    if (dixDernieres.size < 10) return false
    return (dixDernieres.max() - dixDernieres.min()) < 10
}

private val LETTRES_RARES = setOf('Q', 'X', 'W', 'Y', 'Z')

/** Lettres de base d'un mot (accents ignorés, comme `DictionnaireIndex.normaliser`), en majuscules. */
private fun lettresDeBase(mot: String): String =
    Normalizer.normalize(mot, Normalizer.Form.NFD).filter { it.isLetter() }.uppercase()

private fun estMotRare(mot: String): Boolean = lettresDeBase(mot).let { it.length >= 8 && it.any { c -> c in LETTRES_RARES } }

private fun estPalindrome(mot: String): Boolean = lettresDeBase(mot).let { it.length >= 2 && it == it.reversed() }

private fun estMotSymetrique(mot: String): Boolean = lettresDeBase(mot).let { it.length >= 2 && it.zipWithNext().all { (a, b) -> a <= b } }

private fun estPremier(n: Int): Boolean {
    if (n < 2) return false
    if (n < 4) return true
    if (n % 2 == 0) return false
    var diviseur = 3
    while (diviseur * diviseur <= n) {
        if (n % diviseur == 0) return false
        diviseur += 2
    }
    return true
}

/** Nombre de lettres distinctes de l'alphabet (A-Z) utilisées, cumulé sur tous les mots joués — trophée "Alphabet complet". */
private fun nombreLettresAlphabetUtilisees(mots: List<String>): Int =
    mots.flatMap { lettresDeBase(it).toList() }.filter { it in 'A'..'Z' }.toSet().size

private fun dimanchesJoues(dates: List<Long>): SortedSet<LocalDate> =
    dates.map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
        .filter { it.dayOfWeek == java.time.DayOfWeek.SUNDAY }
        .toSortedSet()

/** Plus longue série de dimanches consécutifs (7 jours d'écart pile) dans un ensemble de dimanches triés — trophée "Rituel du dimanche". */
internal fun plusLongueSerieDeDimanches(dimanchesTries: SortedSet<LocalDate>): Int {
    if (dimanchesTries.isEmpty()) return 0
    var meilleure = 1
    var courante = 1
    var precedent: LocalDate? = null
    for (dimanche in dimanchesTries) {
        courante = if (precedent != null && dimanche == precedent.plusWeeks(1)) courante + 1 else 1
        if (courante > meilleure) meilleure = courante
        precedent = dimanche
    }
    return meilleure
}

/**
 * Série de dimanches consécutifs en cours, en remontant depuis le dimanche de la semaine
 * courante (ou celui de la semaine précédente si celui de cette semaine n'a pas encore de
 * partie, pour ne pas casser la série avant que le dimanche en cours soit joué) — même
 * principe que [serieEnCoursDeJours] pour le défi quotidien, mais au pas hebdomadaire.
 */
internal fun serieEnCoursDeDimanches(dimanches: Set<LocalDate>, aujourdHui: LocalDate): Int {
    val dimancheDeCetteSemaine = aujourdHui.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
    var courant = if (dimancheDeCetteSemaine in dimanches) dimancheDeCetteSemaine else dimancheDeCetteSemaine.minusWeeks(1)
    var serie = 0
    while (courant in dimanches) {
        serie++
        courant = courant.minusWeeks(1)
    }
    return serie
}

/**
 * Évalue et débloque les trophées d'un joueur à partir de son historique (parties solo + défis,
 * jamais l'entraînement libre — retour utilisateur). Appelé après chaque partie solo/défi
 * enregistré, et à l'ouverture de l'écran Trophées (filet de sécurité qui rattrape l'historique
 * antérieur à l'introduction de cette fonctionnalité).
 */
class TropheeRepository(
    private val tropheeDao: TropheeDao,
    private val historiqueDao: HistoriqueDao,
    private val defiDao: DefiDao,
    private val defiQuotidienDao: DefiQuotidienDao,
    private val profilDao: ProfilDao,
    private val visitesEcranStore: VisitesEcranStore,
) {
    fun tropheesDebloques(profilId: Long): Flow<List<TropheeEntity>> = tropheeDao.tropheesDebloques(profilId)

    /**
     * Stats agrégées d'un joueur pour l'évaluation des trophées — aussi utilisé pour afficher la
     * progression ("X / objectif") d'un trophée non débloqué. Découpé en plusieurs fonctions
     * privées par thème (retour mainteneur : une seule fonction suspend avec ~80 appels DAO en
     * ligne dépasse la limite de taille de méthode de la JVM — "Method too large" — chaque appel
     * suspend ajoutant un point de reprise à la machine à états générée).
     */
    suspend fun stats(profilId: Long, aujourdHui: LocalDate = LocalDate.now()): TropheeStats {
        // Récupérées une seule fois (retour utilisateur : plusieurs easter eggs en dérivent, pas
        // la peine de réinterroger la base à chaque fois).
        val datesEtScores = historiqueDao.datesEtScoresParties(profilId)
        val mots = historiqueDao.motsJoues(profilId)
        val comptesExactsChiffres = historiqueDao.comptesExactsChiffresDetail(profilId)

        val motsEtScores = statsMotsEtScores(profilId)
        val duo = statsDuo(profilId)
        val defis = statsDefis(profilId)
        val defiQuotidien = statsDefiQuotidien(profilId, aujourdHui)
        val easter = statsEasterGeneral(profilId, datesEtScores, mots, aujourdHui)
        val easterChiffres = statsEasterChiffres(profilId, comptesExactsChiffres)

        return TropheeStats(
        comptesExacts = motsEtScores.comptesExacts,
        motsParLongueur = motsEtScores.motsParLongueur,
        partieTousComptesExacts = motsEtScores.partieTousComptesExacts,
        partiesMotsMin = motsEtScores.partiesMotsMin,
        partiesMotsMinNiveauMathieu = motsEtScores.partiesMotsMinNiveauMathieu,
        partiesParSeuilScore = motsEtScores.partiesParSeuilScore,
        partiesSoloTotal = motsEtScores.partiesSoloTotal,
        partiesDuoJouees = duo.partiesDuoJouees,
        partiesDuoGagnees = duo.partiesDuoGagnees,
        partiesConfrontationJouees = duo.partiesConfrontationJouees,
        partiesConfrontationGagnees = duo.partiesConfrontationGagnees,
        partiesDuelMotsJouees = duo.partiesDuelMotsJouees,
        partiesDuelMotsGagnees = duo.partiesDuelMotsGagnees,
        partiesDuelMotsConfrontationJouees = duo.partiesDuelMotsConfrontationJouees,
        partiesDuelMotsConfrontationGagnees = duo.partiesDuelMotsConfrontationGagnees,
        partiesDuelPointsJouees = duo.partiesDuelPointsJouees,
        partiesDuelPointsGagnees = duo.partiesDuelPointsGagnees,
        duelPointsEcartVictoireMax = duo.duelPointsEcartVictoireMax,
        duelPointsEcartDefaiteMax = duo.duelPointsEcartDefaiteMax,
        duelPointsCompteRondObtenu = duo.duelPointsCompteRondObtenu,
        meilleuresSeriesDefi = defis.meilleuresSeriesDefi,
        meilleuresSeriesDefiNiveauMonique = defis.meilleuresSeriesDefiNiveauMonique,
        meilleuresSeriesDefiNiveauMathieu = defis.meilleuresSeriesDefiNiveauMathieu,
        meilleuresReussitesDefiChrono = defis.meilleuresReussitesDefiChrono,
        meilleuresReussitesDefiChronoNiveauMonique = defis.meilleuresReussitesDefiChronoNiveauMonique,
        meilleuresReussitesDefiChronoNiveauMathieu = defis.meilleuresReussitesDefiChronoNiveauMathieu,
        meilleurScoreDefiMotsMax = defis.meilleurScoreDefiMotsMax,
        meilleurScoreDefiMotsMaxNiveauMonique = defis.meilleurScoreDefiMotsMaxNiveauMonique,
        meilleurScoreDefiMotsMaxNiveauMathieu = defis.meilleurScoreDefiMotsMaxNiveauMathieu,
        meilleurScoreDefiObjectifsPoints = defis.meilleurScoreDefiObjectifsPoints,
        meilleureSerieSansFaute = defis.meilleureSerieSansFaute,
        meilleureSerieSansFauteNiveauMonique = defis.meilleureSerieSansFauteNiveauMonique,
        meilleureSerieSansFauteNiveauMathieu = defis.meilleureSerieSansFauteNiveauMathieu,
        meilleureSerieJoursDefiQuotidien = defiQuotidien.meilleureSerieJoursDefiQuotidien,
        meilleureSerieJoursDefiQuotidienNiveauMonique = defiQuotidien.meilleureSerieJoursDefiQuotidienNiveauMonique,
        meilleureSerieJoursDefiQuotidienNiveauMathieu = defiQuotidien.meilleureSerieJoursDefiQuotidienNiveauMathieu,
        serieEnCoursJoursDefiQuotidien = defiQuotidien.serieEnCoursJoursDefiQuotidien,
        serieEnCoursJoursDefiQuotidienNiveauMonique = defiQuotidien.serieEnCoursJoursDefiQuotidienNiveauMonique,
        serieEnCoursJoursDefiQuotidienNiveauMathieu = defiQuotidien.serieEnCoursJoursDefiQuotidienNiveauMathieu,
        partiesSoloStructureeJouees = easter.partiesSoloStructureeJouees,
        defisJouesTotal = defis.defisJouesTotal,
        ancienneteJoursProfil = easter.ancienneteJoursProfil,
        nombreNiveauxDistinctsJoues = easter.nombreNiveauxDistinctsJoues,
        maxPartiesMemeJour = easter.maxPartiesMemeJour,
        cinqPartiesEnUneHeure = easter.cinqPartiesEnUneHeure,
        ecartDixDernieresPartiesFaible = easter.ecartDixDernieresPartiesFaible,
        partieJoueeEntre5et7h = easter.partieJoueeEntre5et7h,
        unePartieEntreMinuitEt5h = easter.unePartieEntreMinuitEt5h,
        motRareJoue = easter.motRareJoue,
        palindromeJoue = easter.palindromeJoue,
        motSymetriqueJoue = easter.motSymetriqueJoue,
        nombreLettresAlphabetUtilisees = easter.nombreLettresAlphabetUtilisees,
        meilleureSerieDimanchesConsecutifs = easter.meilleureSerieDimanchesConsecutifs,
        serieEnCoursDimanches = easter.serieEnCoursDimanches,
        reglesDejaVues = easter.reglesDejaVues,
        nombreVisitesStats = easter.nombreVisitesStats,
        motInvalideDixLettresTente = easter.motInvalideDixLettresTente,
        egaliteDuelDejaObtenue = easter.egaliteDuelDejaObtenue,
        scoreSoloRepete = easter.scoreSoloRepete,
        compteExactCibleNombrePremier = easterChiffres.compteExactCibleNombrePremier,
        compteExactCalculMental = easterChiffres.compteExactCalculMental,
        compteExactCheminMinimal = easterChiffres.compteExactCheminMinimal,
        compteExactChirurgical = easterChiffres.compteExactChirurgical,
        compteExactSpeedrun = easterChiffres.compteExactSpeedrun,
        compteExactVaTout = easterChiffres.compteExactVaTout,
        ecartEnormeChiffres = easterChiffres.ecartEnormeChiffres,
        compteExactBoiteAOutils = easterChiffres.compteExactBoiteAOutils,
        aucuneIdeeProposee = easterChiffres.aucuneIdeeProposee,
        secondesJoueesTotal = defis.secondesJoueesDefis + historiqueDao.sommeSecondesJouees(profilId),
        )
    }

    private data class StatsMotsEtScores(
        val comptesExacts: Int,
        val motsParLongueur: Map<Int, Int>,
        val partieTousComptesExacts: Boolean,
        val partiesMotsMin: Map<Int, Boolean>,
        val partiesMotsMinNiveauMathieu: Map<Int, Boolean>,
        val partiesParSeuilScore: Map<Int, Int>,
        val partiesSoloTotal: Int,
    )

    private suspend fun statsMotsEtScores(profilId: Long) = StatsMotsEtScores(
        comptesExacts = historiqueDao.compterComptesExacts(profilId),
        motsParLongueur = LONGUEURS_MOTS_TROPHEE.associateWith { historiqueDao.compterMotsLongueur(profilId, it) },
        partieTousComptesExacts = historiqueDao.compterPartiesTousComptesExacts(profilId) >= 1,
        partiesMotsMin = SEUILS_MOTS.associateWith { historiqueDao.compterPartiesMotsMin(profilId, it) >= 1 },
        partiesMotsMinNiveauMathieu = SEUILS_MOTS_NIVEAU_MATHIEU.associateWith {
            historiqueDao.compterPartiesMotsMinNiveau(profilId, it, "MATHIEU") >= 1
        },
        partiesParSeuilScore = SEUILS_SCORE.associateWith { historiqueDao.compterPartiesScoreAuMoins(profilId, it) },
        partiesSoloTotal = historiqueDao.compterPartiesSoloTotal(profilId),
    )

    private data class StatsDuo(
        val partiesDuoJouees: Int,
        val partiesDuoGagnees: Int,
        val partiesConfrontationJouees: Int,
        val partiesConfrontationGagnees: Int,
        val partiesDuelMotsJouees: Int,
        val partiesDuelMotsGagnees: Int,
        val partiesDuelMotsConfrontationJouees: Int,
        val partiesDuelMotsConfrontationGagnees: Int,
        val partiesDuelPointsJouees: Int,
        val partiesDuelPointsGagnees: Int,
        val duelPointsEcartVictoireMax: Int,
        val duelPointsEcartDefaiteMax: Int,
        val duelPointsCompteRondObtenu: Boolean,
    )

    private suspend fun statsDuo(profilId: Long) = StatsDuo(
        partiesDuoJouees = historiqueDao.compterPartiesParTypes(profilId, listOf(TypePartie.DUO.name, TypePartie.DUO_RESEAU.name)),
        partiesDuoGagnees = historiqueDao.compterPartiesGagneesParTypes(profilId, listOf(TypePartie.DUO.name, TypePartie.DUO_RESEAU.name)),
        partiesConfrontationJouees = historiqueDao.compterPartiesParTypes(
            profilId,
            listOf(TypePartie.DUO_CONFRONTATION.name, TypePartie.DUO_CONFRONTATION_RESEAU.name),
        ),
        partiesConfrontationGagnees = historiqueDao.compterPartiesGagneesParTypes(
            profilId,
            listOf(TypePartie.DUO_CONFRONTATION.name, TypePartie.DUO_CONFRONTATION_RESEAU.name),
        ),
        partiesDuelMotsJouees = historiqueDao.compterPartiesParTypes(profilId, listOf(TypePartie.DUEL_MOTS_RESEAU.name)),
        partiesDuelMotsGagnees = historiqueDao.compterPartiesGagneesParTypes(profilId, listOf(TypePartie.DUEL_MOTS_RESEAU.name)),
        partiesDuelMotsConfrontationJouees = historiqueDao.compterPartiesParTypes(
            profilId,
            listOf(TypePartie.DUEL_MOTS_CONFRONTATION_RESEAU.name),
        ),
        partiesDuelMotsConfrontationGagnees = historiqueDao.compterPartiesGagneesParTypes(
            profilId,
            listOf(TypePartie.DUEL_MOTS_CONFRONTATION_RESEAU.name),
        ),
        partiesDuelPointsJouees = historiqueDao.compterPartiesParTypes(profilId, listOf(TypePartie.DUEL_MOTS_POINTS_RESEAU.name)),
        partiesDuelPointsGagnees = historiqueDao.compterPartiesGagneesParTypes(profilId, listOf(TypePartie.DUEL_MOTS_POINTS_RESEAU.name)),
        duelPointsEcartVictoireMax = historiqueDao.maxEcartVictoireDuelPoints(profilId),
        duelPointsEcartDefaiteMax = historiqueDao.maxEcartDefaiteDuelPoints(profilId),
        duelPointsCompteRondObtenu = historiqueDao.compterCompteRondDuelPoints(profilId) >= 1,
    )

    private data class StatsDefis(
        val meilleuresSeriesDefi: Map<String, Int>,
        val meilleuresSeriesDefiNiveauMonique: Map<String, Int>,
        val meilleuresSeriesDefiNiveauMathieu: Map<String, Int>,
        val meilleuresReussitesDefiChrono: Map<String, Int>,
        val meilleuresReussitesDefiChronoNiveauMonique: Map<String, Int>,
        val meilleuresReussitesDefiChronoNiveauMathieu: Map<String, Int>,
        val meilleurScoreDefiMotsMax: Int,
        val meilleurScoreDefiMotsMaxNiveauMonique: Int,
        val meilleurScoreDefiMotsMaxNiveauMathieu: Int,
        val meilleurScoreDefiObjectifsPoints: Int,
        val meilleureSerieSansFaute: Int,
        val meilleureSerieSansFauteNiveauMonique: Int,
        val meilleureSerieSansFauteNiveauMathieu: Int,
        val defisJouesTotal: Int,
        val secondesJoueesDefis: Int,
    )

    private suspend fun statsDefis(profilId: Long): StatsDefis {
        val objectifsPointsDetail = defiDao.defisObjectifsPointsDetail(profilId)
        return StatsDefis(
            meilleuresSeriesDefi = defiDao.meilleuresSeriesDefiParMode(profilId)
                .associate { it.mode.name to it.meilleur },
            meilleuresSeriesDefiNiveauMonique = defiDao.meilleuresSeriesDefiParModeEtNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS)
                .associate { it.mode.name to it.meilleur },
            meilleuresSeriesDefiNiveauMathieu = defiDao.meilleuresSeriesDefiParModeEtNiveaux(profilId, NIVEAUX_MATHIEU)
                .associate { it.mode.name to it.meilleur },
            meilleuresReussitesDefiChrono = defiDao.meilleuresReussitesChronoParCombinaison(profilId)
                .groupBy { it.mode.name }
                .mapValues { (_, combinaisons) -> combinaisons.maxOf { it.meilleur } },
            meilleuresReussitesDefiChronoNiveauMonique = defiDao.meilleuresReussitesChronoParModeEtNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS)
                .associate { it.mode.name to it.meilleur },
            meilleuresReussitesDefiChronoNiveauMathieu = defiDao.meilleuresReussitesChronoParModeEtNiveaux(profilId, NIVEAUX_MATHIEU)
                .associate { it.mode.name to it.meilleur },
            meilleurScoreDefiMotsMax = defiDao.meilleurScoreDefiMotsMax(profilId) ?: 0,
            meilleurScoreDefiMotsMaxNiveauMonique = defiDao.meilleurScoreDefiMotsMaxNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS) ?: 0,
            meilleurScoreDefiMotsMaxNiveauMathieu = defiDao.meilleurScoreDefiMotsMaxNiveaux(profilId, NIVEAUX_MATHIEU) ?: 0,
            meilleurScoreDefiObjectifsPoints = objectifsPointsDetail.maxOfOrNull { it.serie } ?: 0,
            meilleureSerieSansFaute = defiDao.meilleureSerieSansFaute(profilId) ?: 0,
            meilleureSerieSansFauteNiveauMonique = defiDao.meilleureSerieSansFauteNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS) ?: 0,
            meilleureSerieSansFauteNiveauMathieu = defiDao.meilleureSerieSansFauteNiveaux(profilId, NIVEAUX_MATHIEU) ?: 0,
            defisJouesTotal = defiDao.compterDefisTotal(profilId),
            secondesJoueesDefis = defiDao.sommeSecondesDefis(profilId),
        )
    }

    private data class StatsDefiQuotidien(
        val meilleureSerieJoursDefiQuotidien: Int,
        val meilleureSerieJoursDefiQuotidienNiveauMonique: Int,
        val meilleureSerieJoursDefiQuotidienNiveauMathieu: Int,
        val serieEnCoursJoursDefiQuotidien: Int,
        val serieEnCoursJoursDefiQuotidienNiveauMonique: Int,
        val serieEnCoursJoursDefiQuotidienNiveauMathieu: Int,
    )

    private suspend fun statsDefiQuotidien(profilId: Long, aujourdHui: LocalDate) = StatsDefiQuotidien(
        meilleureSerieJoursDefiQuotidien = plusLongueSerieDeJours(
            defiQuotidienDao.joursReussis(profilId).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted(),
        ),
        meilleureSerieJoursDefiQuotidienNiveauMonique = plusLongueSerieDeJours(
            defiQuotidienDao.joursReussisNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS)
                .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted(),
        ),
        meilleureSerieJoursDefiQuotidienNiveauMathieu = plusLongueSerieDeJours(
            defiQuotidienDao.joursReussisNiveaux(profilId, NIVEAUX_MATHIEU)
                .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted(),
        ),
        serieEnCoursJoursDefiQuotidien = serieEnCoursDeJours(
            defiQuotidienDao.joursReussis(profilId).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet(),
            aujourdHui,
        ),
        serieEnCoursJoursDefiQuotidienNiveauMonique = serieEnCoursDeJours(
            defiQuotidienDao.joursReussisNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS)
                .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet(),
            aujourdHui,
        ),
        serieEnCoursJoursDefiQuotidienNiveauMathieu = serieEnCoursDeJours(
            defiQuotidienDao.joursReussisNiveaux(profilId, NIVEAUX_MATHIEU)
                .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet(),
            aujourdHui,
        ),
    )

    private data class StatsEasterGeneral(
        val partiesSoloStructureeJouees: Int,
        val ancienneteJoursProfil: Long,
        val nombreNiveauxDistinctsJoues: Int,
        val maxPartiesMemeJour: Int,
        val cinqPartiesEnUneHeure: Boolean,
        val ecartDixDernieresPartiesFaible: Boolean,
        val partieJoueeEntre5et7h: Boolean,
        val unePartieEntreMinuitEt5h: Boolean,
        val motRareJoue: Boolean,
        val palindromeJoue: Boolean,
        val motSymetriqueJoue: Boolean,
        val nombreLettresAlphabetUtilisees: Int,
        val meilleureSerieDimanchesConsecutifs: Int,
        val serieEnCoursDimanches: Int,
        val reglesDejaVues: Boolean,
        val nombreVisitesStats: Int,
        val motInvalideDixLettresTente: Boolean,
        val egaliteDuelDejaObtenue: Boolean,
        val scoreSoloRepete: Boolean,
    )

    private suspend fun statsEasterGeneral(
        profilId: Long,
        datesEtScores: List<HistoriqueDao.DateEtScore>,
        mots: List<String>,
        aujourdHui: LocalDate,
    ) = StatsEasterGeneral(
        partiesSoloStructureeJouees = historiqueDao.compterPartiesParType(profilId, "STRUCTUREE"),
        ancienneteJoursProfil = profilDao.parId(profilId)?.let { (System.currentTimeMillis() - it.dateCreation) / 86_400_000L } ?: 0L,
        nombreNiveauxDistinctsJoues = historiqueDao.compterNiveauxDistinctsJoues(profilId),
        maxPartiesMemeJour = maxPartiesMemeJour(datesEtScores.map { it.date }),
        cinqPartiesEnUneHeure = cinqPartiesEnUneHeure(datesEtScores.map { it.date }),
        ecartDixDernieresPartiesFaible = ecartDixDernieresPartiesFaible(datesEtScores.sortedByDescending { it.date }.map { it.score }),
        partieJoueeEntre5et7h = datesEtScores.any { heureLocale(it.date) in 5..6 },
        unePartieEntreMinuitEt5h = datesEtScores.any { heureLocale(it.date) in 0..4 },
        motRareJoue = mots.any { estMotRare(it) },
        palindromeJoue = mots.any { estPalindrome(it) },
        motSymetriqueJoue = mots.any { estMotSymetrique(it) },
        nombreLettresAlphabetUtilisees = nombreLettresAlphabetUtilisees(mots),
        meilleureSerieDimanchesConsecutifs = plusLongueSerieDeDimanches(dimanchesJoues(datesEtScores.map { it.date })),
        serieEnCoursDimanches = serieEnCoursDeDimanches(dimanchesJoues(datesEtScores.map { it.date }), aujourdHui),
        reglesDejaVues = visitesEcranStore.reglesDejaVues(profilId),
        nombreVisitesStats = visitesEcranStore.nombreVisitesStats(profilId),
        motInvalideDixLettresTente = historiqueDao.compterMotsInvalidesDixLettresOuPlus(profilId) >= 1,
        egaliteDuelDejaObtenue = historiqueDao.compterEgalitesDuel(profilId) >= 1,
        scoreSoloRepete = historiqueDao.compterScoresSoloRepetes(profilId) >= 1,
    )

    private data class StatsEasterChiffres(
        val compteExactCibleNombrePremier: Boolean,
        val compteExactCalculMental: Boolean,
        val compteExactCheminMinimal: Boolean,
        val compteExactChirurgical: Boolean,
        val compteExactSpeedrun: Boolean,
        val compteExactVaTout: Boolean,
        val ecartEnormeChiffres: Boolean,
        val compteExactBoiteAOutils: Boolean,
        val aucuneIdeeProposee: Boolean,
    )

    private suspend fun statsEasterChiffres(profilId: Long, comptesExactsChiffres: List<HistoriqueDao.DetailCompteExact>) = StatsEasterChiffres(
        compteExactCibleNombrePremier = comptesExactsChiffres.any { it.cible != null && estPremier(it.cible) },
        compteExactCalculMental = comptesExactsChiffres.any {
            (it.maxEtapeIntermediaire ?: 0) <= 99 && it.niveauCode in NIVEAUX_MONIQUE_OU_PLUS
        },
        compteExactCheminMinimal = comptesExactsChiffres.any { it.nombreOperations == 1 },
        compteExactChirurgical = comptesExactsChiffres.any { it.nombreOperations == 5 },
        compteExactSpeedrun = comptesExactsChiffres.any { (it.dureeSecondesManche ?: Int.MAX_VALUE) <= 5 },
        compteExactVaTout = comptesExactsChiffres.any { (it.tempsRestantSecondesValidation ?: Int.MAX_VALUE) <= 1 },
        ecartEnormeChiffres = historiqueDao.compterEcartEnormeChiffres(profilId) >= 1,
        compteExactBoiteAOutils = comptesExactsChiffres.any { it.operateursUtilises == TOUS_OPERATEURS_MASK },
        aucuneIdeeProposee = historiqueDao.compterManchesSansRienPropose(profilId) >= 1,
    )

    /**
     * Évalue et débloque les trophées d'un joueur, et retourne ceux qui viennent d'être
     * fraîchement débloqués (retour utilisateur : écran dédié affiché en fin de manche/partie
     * quand cette liste n'est pas vide) — jamais ceux déjà débloqués avant cet appel.
     */
    suspend fun reevaluer(profilId: Long): List<Trophee> {
        val idsAvant = tropheeDao.tropheesDebloques(profilId).first().map { it.trophyId }.toSet()
        val stats = stats(profilId)
        for (trophee in CatalogueTrophees.TOUS) {
            if (trophee.estDebloque(stats)) {
                tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, trophee.id, System.currentTimeMillis()))
            }
        }
        // Retire les trophées débloqués sous un ancien id que les refontes de seuils ont
        // renommé (retour utilisateur : sinon ils restent comptés dans "x/y débloquées" sans
        // exister nulle part dans le catalogue actuel).
        tropheeDao.supprimerOrphelins(profilId, CatalogueTrophees.TOUS.map { it.id })

        // Méta-trophées "tous les trophées de la section X" (refonte 2026-08) : leur condition
        // dépend des AUTRES trophées déjà débloqués, pas des stats de jeu (cf. `estDebloque`
        // sentinel dans CatalogueTrophees) — évalués séparément une fois la boucle ci-dessus et
        // le nettoyage des orphelins terminés.
        val idsDebloques = tropheeDao.tropheesDebloques(profilId).first().map { it.trophyId }.toSet()
        if (idsDebloques.containsAll(CatalogueTrophees.idsSectionDefi())) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "section_defi_complete", System.currentTimeMillis()))
        }
        if (idsDebloques.containsAll(CatalogueTrophees.idsSectionPartie())) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "section_partie_complete", System.currentTimeMillis()))
        }

        // Easter eggs dépendant eux aussi de l'état des trophées, pas des stats de jeu.
        val debloqueOrOuMieux = { categorie: CategorieTrophee ->
            CatalogueTrophees.TOUS.any {
                it.categorie == categorie && (it.palier?.ordinal ?: -1) >= Palier.OR.ordinal && it.id in idsDebloques
            }
        }
        if (CatalogueTrophees.CATEGORIES_JEU.all(debloqueOrOuMieux)) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "easter_polyvalent", System.currentTimeMillis()))
        }
        val categorieEntierementDebloquee = CatalogueTrophees.CATEGORIES_JEU.any { categorie ->
            val idsCategorie = CatalogueTrophees.TOUS.filter { it.categorie == categorie }.map { it.id }
            idsDebloques.containsAll(idsCategorie)
        }
        if (categorieEntierementDebloquee) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "easter_specialiste_complet", System.currentTimeMillis()))
        }
        if (idsDebloques.size >= 80) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "easter_noce_de_chene", System.currentTimeMillis()))
        }
        val idsAutresQueToitDuMonde = CatalogueTrophees.TOUS.filterNot { it.id == "easter_toit_du_monde" }.map { it.id }
        if (idsDebloques.containsAll(idsAutresQueToitDuMonde)) {
            tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, "easter_toit_du_monde", System.currentTimeMillis()))
        }

        val idsApres = tropheeDao.tropheesDebloques(profilId).first().map { it.trophyId }.toSet()
        val idsNouveaux = idsApres - idsAvant
        return CatalogueTrophees.TOUS.filter { it.id in idsNouveaux }
    }

    suspend fun reinitialiserJoueur(profilId: Long) = tropheeDao.reinitialiserJoueur(profilId)

    /** Tous les trophées débloqués d'un joueur — "Exporter mes statistiques". */
    suspend fun exporterTrophees(profilId: Long): List<TropheeEntity> = tropheeDao.tropheesDebloques(profilId).first()

    /**
     * Réinsère des trophées pour un profil cible — "Importer mes statistiques". La date de
     * déblocage d'origine est conservée (contrairement à [reevaluer], qui daterait un trophée
     * fraîchement rattrapé au moment de l'évaluation).
     */
    suspend fun importerTrophees(profilId: Long, trophees: List<TropheeEntity>) {
        for (trophee in trophees) {
            tropheeDao.debloquerSiAbsent(trophee.copy(profilId = profilId))
        }
    }
}
