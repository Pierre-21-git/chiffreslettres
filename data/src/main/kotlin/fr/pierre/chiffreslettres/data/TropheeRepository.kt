package fr.pierre.chiffreslettres.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_MOTS_NIVEAU_MATHIEU = listOf(7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
private val LONGUEURS_MOTS_TROPHEE = 4..10
private val NIVEAUX_MONIQUE_OU_PLUS = listOf("MONIQUE", "MATHIEU")
private val NIVEAUX_MATHIEU = listOf("MATHIEU")

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
) {
    fun tropheesDebloques(profilId: Long): Flow<List<TropheeEntity>> = tropheeDao.tropheesDebloques(profilId)

    /** Stats agrégées d'un joueur pour l'évaluation des trophées — aussi utilisé pour afficher la progression ("X / objectif") d'un trophée non débloqué. */
    suspend fun stats(profilId: Long): TropheeStats = TropheeStats(
        comptesExacts = historiqueDao.compterComptesExacts(profilId),
        motsParLongueur = LONGUEURS_MOTS_TROPHEE.associateWith { historiqueDao.compterMotsLongueur(profilId, it) },
        partieTousComptesExacts = historiqueDao.compterPartiesTousComptesExacts(profilId) >= 1,
        partiesMotsMin = SEUILS_MOTS.associateWith { historiqueDao.compterPartiesMotsMin(profilId, it) >= 1 },
        partiesMotsMinNiveauMathieu = SEUILS_MOTS_NIVEAU_MATHIEU.associateWith {
            historiqueDao.compterPartiesMotsMinNiveau(profilId, it, "MATHIEU") >= 1
        },
        partiesParSeuilScore = SEUILS_SCORE.associateWith { historiqueDao.compterPartiesScoreAuMoins(profilId, it) },
        partiesSoloTotal = historiqueDao.compterPartiesSoloTotal(profilId),
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
        defisTotal = defiDao.compterDefisTotal(profilId),
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
        meilleureSerieSansFaute = defiDao.meilleureSerieSansFaute(profilId) ?: 0,
        meilleureSerieSansFauteNiveauMonique = defiDao.meilleureSerieSansFauteNiveaux(profilId, NIVEAUX_MONIQUE_OU_PLUS) ?: 0,
        meilleureSerieSansFauteNiveauMathieu = defiDao.meilleureSerieSansFauteNiveaux(profilId, NIVEAUX_MATHIEU) ?: 0,
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
    )

    suspend fun reevaluer(profilId: Long) {
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
