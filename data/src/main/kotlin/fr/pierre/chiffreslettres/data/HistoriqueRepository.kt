package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

data class ResultatManche(
    val mode: ModeJeu,
    val niveauCode: String,
    val score: Int,
    val motJoue: String? = null,
)

class HistoriqueRepository(private val dao: HistoriqueDao) {

    suspend fun enregistrerSession(profilId: Long, type: TypePartie, manches: List<ResultatManche>) {
        val session = SessionEntity(
            profilId = profilId,
            date = System.currentTimeMillis(),
            type = type,
            scoreTotal = manches.sumOf { it.score },
        )
        val entites = manches.mapIndexed { index, resultat ->
            MancheEntity(
                sessionId = 0, // remplacé par enregistrerPartie() une fois la session insérée
                ordre = index,
                mode = resultat.mode,
                niveauCode = resultat.niveauCode,
                score = resultat.score,
                motJoue = resultat.motJoue,
            )
        }
        dao.enregistrerPartie(session, entites)
    }

    fun classementParNiveau(niveauCode: String): Flow<List<LigneClassement>> = dao.classementParNiveau(niveauCode)

    fun meilleuresPartiesSoloParNiveau(profilId: Long, niveauCode: String): Flow<List<MeilleurePartieSolo>> =
        dao.meilleuresPartiesSoloParNiveau(profilId, niveauCode)

    suspend fun reinitialiserHistoriqueJoueur(profilId: Long) = dao.reinitialiserHistoriqueJoueur(profilId)

    /** Tout l'historique brut d'un joueur — "Exporter mes statistiques". */
    suspend fun exporterSessions(profilId: Long): List<SessionAvecManches> = dao.sessionsAvecManchesDuJoueur(profilId)

    /**
     * Réinsère des sessions (avec leurs manches) pour un profil cible — "Importer mes
     * statistiques". Les id d'origine (session et manches) sont ignorés : de nouveaux id sont
     * générés, comme pour toute nouvelle partie enregistrée.
     */
    suspend fun importerSessions(profilId: Long, sessions: List<SessionAvecManches>) {
        for (entree in sessions) {
            dao.enregistrerPartie(
                entree.session.copy(id = 0, profilId = profilId),
                entree.manches.map { it.copy(id = 0, sessionId = 0) },
            )
        }
    }
}
