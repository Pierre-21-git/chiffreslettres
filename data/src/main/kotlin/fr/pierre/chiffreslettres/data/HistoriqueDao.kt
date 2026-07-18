package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class LigneClassement(val profilId: Long, val pseudo: String, val score: Int, val date: Long)

/** Une partie solo (score final + date), pour le classement personnel d'un joueur par niveau. */
data class MeilleurePartieSolo(val score: Int, val date: Long)

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

    /** Nombre de manches jouées en entraînement libre par un joueur, pour un mode et un niveau donnés. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type = 'LIBRE' AND m.mode = :mode AND m.niveauCode = :niveauCode
        """,
    )
    fun compterManchesEntrainementParNiveau(profilId: Long, mode: ModeJeu, niveauCode: String): Flow<Int>

    /**
     * Nombre de parties solo (structurées) jouées par un joueur pour un niveau donné. Un seul
     * niveau s'applique à toutes les manches d'une partie solo, d'où le `JOIN` sur
     * `MancheEntity` pour filtrer par niveau tout en comptant des sessions distinctes.
     */
    @Query(
        """
        SELECT COUNT(DISTINCT s.id)
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE' AND m.niveauCode = :niveauCode
        """,
    )
    fun compterPartiesSoloParNiveau(profilId: Long, niveauCode: String): Flow<Int>

    /** Top 3 des meilleures parties solo (score final de la partie) d'un joueur pour un niveau donné. */
    @Query(
        """
        SELECT s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE' AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 3
        """,
    )
    fun meilleuresPartiesSoloParNiveau(profilId: Long, niveauCode: String): Flow<List<MeilleurePartieSolo>>

    /**
     * Vide l'historique (sessions + manches, cascade) d'un seul joueur — bouton
     * "Réinitialiser mes statistiques" sur l'onglet Joueurs (ne touche pas les autres profils).
     */
    @Query("DELETE FROM SessionEntity WHERE profilId = :profilId")
    suspend fun reinitialiserHistoriqueJoueur(profilId: Long)

    // --- Agrégats pour l'évaluation des trophées (parties solo uniquement, jamais l'entraînement libre) ---

    /** Nombre de manches chiffres à compte exact (score 10), en partie solo. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE' AND m.mode = 'CHIFFRES' AND m.score = 10
        """,
    )
    suspend fun compterComptesExacts(profilId: Long): Int

    /** Nombre de manches lettres dont le mot joué a exactement [longueur] lettres, en partie solo. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE' AND m.mode = 'LETTRES' AND LENGTH(m.motJoue) = :longueur
        """,
    )
    suspend fun compterMotsLongueur(profilId: Long, longueur: Int): Int

    /**
     * Nombre de parties solo où toutes les manches chiffres ont un compte exact (score 10).
     * `COUNT(*) = SUM(...)` exige qu'aucune manche chiffres de la partie n'ait un score différent.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'CHIFFRES'
            WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE'
            GROUP BY s.id
            HAVING COUNT(*) = SUM(CASE WHEN m.score = 10 THEN 1 ELSE 0 END)
        )
        """,
    )
    suspend fun compterPartiesTousComptesExacts(profilId: Long): Int

    /**
     * Nombre de parties solo où toutes les manches lettres ont un mot valide d'au moins
     * [longueurMin] lettres. `COUNT(*) = COUNT(m.motJoue)` exclut toute manche invalide/vide
     * (mot null) de la partie, condition nécessaire avant de vérifier la longueur minimale.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'LETTRES'
            WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE'
            GROUP BY s.id
            HAVING COUNT(*) = COUNT(m.motJoue) AND MIN(LENGTH(m.motJoue)) >= :longueurMin
        )
        """,
    )
    suspend fun compterPartiesMotsMin(profilId: Long, longueurMin: Int): Int

    /** Nombre de parties solo dont le score total atteint au moins [seuil]. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = 'STRUCTUREE' AND scoreTotal >= :seuil")
    suspend fun compterPartiesScoreAuMoins(profilId: Long, seuil: Int): Int

    /** Nombre total de parties solo terminées, tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = 'STRUCTUREE'")
    suspend fun compterPartiesSoloTotal(profilId: Long): Int

    /** Nombre de niveaux distincts avec au moins une partie solo terminée. */
    @Query(
        """
        SELECT COUNT(DISTINCT m.niveauCode)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type = 'STRUCTUREE'
        """,
    )
    suspend fun compterNiveauxSoloCouverts(profilId: Long): Int
}
