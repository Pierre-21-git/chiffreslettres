package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class LigneClassement(val profilId: Long, val pseudo: String, val score: Int, val date: Long)

/** Une manche individuelle, pour le classement personnel "meilleurs chiffres/lettres" d'un joueur. */
data class MeilleureManche(val score: Int, val date: Long)

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

    /**
     * Top 5 scores de partie (pas de regroupement par profil : un même joueur peut
     * apparaître plusieurs fois, retour utilisateur) pour un niveau donné (spec
     * §7.2), tous confondus chiffres/lettres (les deux modes partagent désormais
     * les mêmes noms de niveau), uniquement les parties structurées. Le score
     * affiché est le score final de la partie (`SessionEntity.scoreTotal`, somme
     * des manches), pas celui d'une manche individuelle (retour utilisateur) — un
     * seul niveau s'applique à toutes les manches d'une partie structurée, d'où
     * le `JOIN` sur `MancheEntity` pour filtrer par niveau malgré le regroupement
     * par session. À score égal, la partie la plus récente passe devant.
     */
    @Query(
        """
        SELECT p.id AS profilId, p.pseudo AS pseudo, s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN ProfilEntity p ON p.id = s.profilId
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.type = 'STRUCTUREE' AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 5
        """,
    )
    fun classementParNiveau(niveauCode: String): Flow<List<LigneClassement>>

    /** Nombre de manches jouées en entraînement libre par un joueur, pour un mode donné. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type = 'LIBRE' AND m.mode = :mode
        """,
    )
    fun compterManchesEntrainement(profilId: Long, mode: ModeJeu): Flow<Int>

    /** Nombre de parties solo (structurées) jouées par un joueur. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = 'STRUCTUREE'")
    fun compterPartiesSolo(profilId: Long): Flow<Int>

    /**
     * Top 5 des meilleurs scores individuels d'un joueur pour un mode donné (entraînement et
     * partie solo confondus, retour utilisateur), avec la date de la session correspondante.
     */
    @Query(
        """
        SELECT m.score AS score, s.date AS date
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND m.mode = :mode
        ORDER BY m.score DESC, s.date DESC
        LIMIT 5
        """,
    )
    fun meilleuresManches(profilId: Long, mode: ModeJeu): Flow<List<MeilleureManche>>

    /** Vide tout l'historique (sessions + manches, cascade) — bouton "Réinitialiser les statistiques". */
    @Query("DELETE FROM SessionEntity")
    suspend fun reinitialiserHistorique()
}
