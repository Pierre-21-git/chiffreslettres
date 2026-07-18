package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
private const val LONGUEUR_MOT_MAX = 10

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
) {
    fun tropheesDebloques(profilId: Long): Flow<List<TropheeEntity>> = tropheeDao.tropheesDebloques(profilId)

    suspend fun reevaluer(profilId: Long) {
        val stats = TropheeStats(
            comptesExacts = historiqueDao.compterComptesExacts(profilId),
            motsDixLettres = historiqueDao.compterMotsLongueur(profilId, LONGUEUR_MOT_MAX),
            partieTousComptesExacts = historiqueDao.compterPartiesTousComptesExacts(profilId) >= 1,
            partiesMotsMin = SEUILS_MOTS.associateWith { historiqueDao.compterPartiesMotsMin(profilId, it) >= 1 },
            partiesParSeuilScore = SEUILS_SCORE.associateWith { historiqueDao.compterPartiesScoreSuperieur(profilId, it) },
            partiesSoloTotal = historiqueDao.compterPartiesSoloTotal(profilId),
            niveauxSoloCouverts = historiqueDao.compterNiveauxSoloCouverts(profilId),
            defisTotal = defiDao.compterDefisTotal(profilId),
            meilleureSerieDefi = defiDao.meilleureSerieDefi(profilId),
            combinaisonsDefiCouvertes = defiDao.compterCombinaisonsCouvertes(profilId),
            meilleuresReussitesDefiChrono = defiDao.meilleuresReussitesChronoParCombinaison(profilId)
                .associate { "${it.mode.name}_${it.niveauCode}" to it.meilleur },
        )
        for (trophee in CatalogueTrophees.TOUS) {
            if (trophee.estDebloque(stats)) {
                tropheeDao.debloquerSiAbsent(TropheeEntity(profilId, trophee.id, System.currentTimeMillis()))
            }
        }
    }

    suspend fun reinitialiserJoueur(profilId: Long) = tropheeDao.reinitialiserJoueur(profilId)
}
