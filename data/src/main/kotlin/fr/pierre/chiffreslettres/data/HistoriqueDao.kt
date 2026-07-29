package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class LigneClassement(val profilId: Long, val pseudo: String, val avatar: String, val score: Int, val date: Long)

/** Une partie solo (score final + date), pour le classement personnel d'un joueur par niveau. */
data class MeilleurePartieSolo(val score: Int, val date: Long)

/** Une session avec ses manches, pour l'export/import complet de l'historique d'un joueur. */
data class SessionAvecManches(
    @Embedded val session: SessionEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionId")
    val manches: List<MancheEntity>,
)

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
     * Podium (top 3, retour utilisateur) des scores de partie (pas de regroupement par profil :
     * un même joueur peut apparaître plusieurs fois) pour un niveau et un type de partie donnés
     * (spec §7.2 ; [type] = nom d'un [TypePartie] : STRUCTUREE pour le classement solo, DUO ou
     * DUO_CONFRONTATION pour les classements duel, chacun séparé — retour utilisateur), tous
     * confondus chiffres/lettres (les deux modes partagent désormais les mêmes noms de niveau).
     * Le score affiché est le score final de la partie (`SessionEntity.scoreTotal`, somme des
     * manches), pas celui d'une manche individuelle (retour utilisateur) — un seul niveau
     * s'applique à toutes les manches d'une partie, d'où le `JOIN` sur `MancheEntity` pour
     * filtrer par niveau malgré le regroupement par session. À score égal, la partie la plus
     * récente passe devant.
     */
    @Query(
        """
        SELECT p.id AS profilId, p.pseudo AS pseudo, p.avatar AS avatar, s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN ProfilEntity p ON p.id = s.profilId
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 3
        """,
    )
    fun classementParNiveau(niveauCode: String, type: String): Flow<List<LigneClassement>>

    /** Top 3 des meilleures parties (score final de la partie) d'un joueur pour un niveau et un type de partie donnés. */
    @Query(
        """
        SELECT s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 3
        """,
    )
    fun meilleuresPartiesSoloParNiveau(profilId: Long, niveauCode: String, type: String): Flow<List<MeilleurePartieSolo>>

    /**
     * Historique chronologique (toutes les parties, pas seulement le podium) des scores d'un
     * joueur pour un niveau et un type de partie donnés — graphique de progression sur l'onglet
     * "Mes statistiques" (solo uniquement pour l'instant).
     */
    @Query(
        """
        SELECT s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.date ASC
        """,
    )
    fun historiqueScoresParNiveau(profilId: Long, niveauCode: String, type: String): Flow<List<MeilleurePartieSolo>>

    /**
     * Vide l'historique (sessions + manches, cascade) d'un seul joueur — bouton
     * "Réinitialiser mes statistiques" sur l'onglet Joueurs (ne touche pas les autres profils).
     */
    @Query("DELETE FROM SessionEntity WHERE profilId = :profilId")
    suspend fun reinitialiserHistoriqueJoueur(profilId: Long)

    /** Tout l'historique (sessions + manches) d'un joueur, pour "Exporter mes statistiques". */
    @Transaction
    @Query("SELECT * FROM SessionEntity WHERE profilId = :profilId")
    suspend fun sessionsAvecManchesDuJoueur(profilId: Long): List<SessionAvecManches>

    // --- Agrégats pour l'évaluation des trophées (parties solo/duo/confrontation, jamais
    // l'entraînement libre — retour utilisateur : les trophées "généraux" comptent aussi les
    // parties duo et confrontation, pas seulement le solo). En confrontation, le score d'une
    // manche perdue est écrasé à 0 mais motJoue et les manches à écart 0 (compte exact) ne sont
    // jamais affectés (une proposition exacte gagne toujours ou fait égalité, jamais perdante).

    /** Nombre de manches chiffres à compte exact (score 10). */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION')
            AND m.mode = 'CHIFFRES' AND m.score = 10
        """,
    )
    suspend fun compterComptesExacts(profilId: Long): Int

    /** Nombre de manches lettres dont le mot joué a exactement [longueur] lettres. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION')
            AND m.mode = 'LETTRES' AND LENGTH(m.motJoue) = :longueur
        """,
    )
    suspend fun compterMotsLongueur(profilId: Long, longueur: Int): Int

    /**
     * Nombre de parties où toutes les manches chiffres ont un compte exact (score 10).
     * `COUNT(*) = SUM(...)` exige qu'aucune manche chiffres de la partie n'ait un score différent.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'CHIFFRES'
            WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION')
            GROUP BY s.id
            HAVING COUNT(*) = SUM(CASE WHEN m.score = 10 THEN 1 ELSE 0 END)
        )
        """,
    )
    suspend fun compterPartiesTousComptesExacts(profilId: Long): Int

    /**
     * Nombre de parties où toutes les manches lettres ont un mot valide d'au moins
     * [longueurMin] lettres. `COUNT(*) = COUNT(m.motJoue)` exclut toute manche invalide/vide
     * (mot null) de la partie, condition nécessaire avant de vérifier la longueur minimale.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'LETTRES'
            WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION')
            GROUP BY s.id
            HAVING COUNT(*) = COUNT(m.motJoue) AND MIN(LENGTH(m.motJoue)) >= :longueurMin
        )
        """,
    )
    suspend fun compterPartiesMotsMin(profilId: Long, longueurMin: Int): Int

    /** Nombre de parties dont le score total atteint au moins [seuil]. */
    @Query(
        """
        SELECT COUNT(*) FROM SessionEntity
        WHERE profilId = :profilId AND type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION') AND scoreTotal >= :seuil
        """,
    )
    suspend fun compterPartiesScoreAuMoins(profilId: Long, seuil: Int): Int

    /** Nombre total de parties terminées, tous niveaux et tous types confondus (solo, duo, confrontation). */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION')")
    suspend fun compterPartiesSoloTotal(profilId: Long): Int

    /** Nombre de parties d'un [type] (DUO ou DUO_CONFRONTATION) jouées par ce profil, tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = :type")
    suspend fun compterPartiesParType(profilId: Long, type: String): Int

    /** Nombre de parties d'un [type] gagnées par ce profil (`victoireDuel = 1`), tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = :type AND victoireDuel = 1")
    suspend fun compterPartiesGagneesParType(profilId: Long, type: String): Int
}
