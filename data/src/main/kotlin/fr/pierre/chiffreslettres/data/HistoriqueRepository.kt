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

    fun compterManchesEntrainement(profilId: Long, mode: ModeJeu): Flow<Int> =
        dao.compterManchesEntrainement(profilId, mode)

    fun compterPartiesSolo(profilId: Long): Flow<Int> = dao.compterPartiesSolo(profilId)

    fun meilleuresManches(profilId: Long, mode: ModeJeu): Flow<List<MeilleureManche>> =
        dao.meilleuresManches(profilId, mode)

    suspend fun reinitialiserHistorique() = dao.reinitialiserHistorique()
}
