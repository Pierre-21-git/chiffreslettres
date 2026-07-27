package fr.pierre.chiffreslettres.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
private val LONGUEURS_MOTS_TROPHEE = 4..10

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

    suspend fun reevaluer(profilId: Long) {
        val stats = TropheeStats(
            comptesExacts = historiqueDao.compterComptesExacts(profilId),
            motsParLongueur = LONGUEURS_MOTS_TROPHEE.associateWith { historiqueDao.compterMotsLongueur(profilId, it) },
            partieTousComptesExacts = historiqueDao.compterPartiesTousComptesExacts(profilId) >= 1,
            partiesMotsMin = SEUILS_MOTS.associateWith { historiqueDao.compterPartiesMotsMin(profilId, it) >= 1 },
            partiesParSeuilScore = SEUILS_SCORE.associateWith { historiqueDao.compterPartiesScoreAuMoins(profilId, it) },
            partiesSoloTotal = historiqueDao.compterPartiesSoloTotal(profilId),
            defisTotal = defiDao.compterDefisTotal(profilId),
            meilleuresSeriesDefi = defiDao.meilleuresSeriesDefiParMode(profilId)
                .associate { it.mode.name to it.meilleur },
            meilleuresReussitesDefiChrono = defiDao.meilleuresReussitesChronoParCombinaison(profilId)
                .groupBy { it.mode.name }
                .mapValues { (_, combinaisons) -> combinaisons.maxOf { it.meilleur } },
            meilleureSerieJoursDefiQuotidien = plusLongueSerieDeJours(
                defiQuotidienDao.joursReussis(profilId).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted(),
            ),
        )
        for (trophee in CatalogueTrophees.TOUS) {
            if (trophee.estDebloque(stats)) {
                tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, trophee.id, System.currentTimeMillis()))
            }
        }
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
