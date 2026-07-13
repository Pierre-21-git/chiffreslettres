package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class LigneClassement(val profilId: Long, val pseudo: String, val meilleurScore: Int)

data class MeilleurMot(val motJoue: String, val score: Int)

@Dao
interface HistoriqueDao {
    @Insert
    suspend fun insererSession(session: SessionEntity): Long

    @Insert
    suspend fun insererManches(manches: List<MancheEntity>)

    @Transaction
    suspend fun enregistrerPartie(session: SessionEntity, manches: List<MancheEntity>) {
        val sessionId = insererSession(session)
        insererManches(manches.map { it.copy(sessionId = sessionId) })
    }

    /** Classement (meilleur score par profil) pour un mode et un niveau donnés (spec §7.2). */
    @Query(
        """
        SELECT p.id AS profilId, p.pseudo AS pseudo, MAX(m.score) AS meilleurScore
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        INNER JOIN ProfilEntity p ON p.id = s.profilId
        WHERE m.mode = :mode AND m.niveauCode = :niveauCode
        GROUP BY p.id
        ORDER BY meilleurScore DESC
        """,
    )
    fun classementParNiveau(mode: ModeJeu, niveauCode: String): Flow<List<LigneClassement>>

    /** Le mot le plus long trouvé par ce profil en mode Lettres (spec §7.2). */
    @Query(
        """
        SELECT m.motJoue AS motJoue, m.score AS score
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND m.mode = 'LETTRES' AND m.motJoue IS NOT NULL
        ORDER BY LENGTH(m.motJoue) DESC
        LIMIT 1
        """,
    )
    fun plusLongMot(profilId: Long): Flow<MeilleurMot?>

    /** Meilleur score total en partie structurée pour ce profil (spec §7.2). */
    @Query("SELECT MAX(scoreTotal) FROM SessionEntity WHERE profilId = :profilId AND type = 'STRUCTUREE'")
    fun meilleurScorePartieStructuree(profilId: Long): Flow<Int?>
}
