package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

class DefiRepository(private val dao: DefiDao) {

    suspend fun enregistrer(profilId: Long, mode: ModeJeu, niveauCode: String, type: TypeDefi, serie: Int) {
        dao.enregistrer(
            DefiEntity(
                profilId = profilId,
                mode = mode,
                niveauCode = niveauCode,
                type = type,
                serie = serie,
                date = System.currentTimeMillis(),
            ),
        )
    }

    fun meilleursDefisParNiveau(profilId: Long, mode: ModeJeu, niveauCode: String): Flow<List<MeilleurDefi>> =
        dao.meilleursDefisParNiveau(profilId, mode, niveauCode)

    fun meilleuresPerformancesChronoParNiveau(profilId: Long, mode: ModeJeu, niveauCode: String): Flow<List<MeilleurDefi>> =
        dao.meilleuresPerformancesChronoParNiveau(profilId, mode, niveauCode)

    suspend fun reinitialiserJoueur(profilId: Long) = dao.reinitialiserJoueur(profilId)

    /** Tous les défis bruts d'un joueur — "Exporter mes statistiques". */
    suspend fun exporterDefis(profilId: Long): List<DefiEntity> = dao.defisDuJoueur(profilId)

    /** Réinsère des défis pour un profil cible — "Importer mes statistiques" (id d'origine ignorés). */
    suspend fun importerDefis(profilId: Long, defis: List<DefiEntity>) {
        for (defi in defis) {
            dao.enregistrer(defi.copy(id = 0, profilId = profilId))
        }
    }
}
