package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Meilleure performance en défi chrono pour une combinaison mode × niveau, pour l'évaluation des trophées. */
data class MeilleureReussiteChrono(val mode: ModeJeu, val niveauCode: String, val meilleur: Int)

/** Meilleure série en défi série pour un mode, tous niveaux confondus, pour l'évaluation des trophées. */
data class MeilleureSerieDefi(val mode: ModeJeu, val meilleur: Int)

/** Un défi terminé (série/réussites/mots trouvés + date), pour le podium et le graphique de progression d'un joueur (retour utilisateur, statistiques étendues). */
data class DefiResultat(val serie: Int, val date: Long)

/** Comme [LigneClassement] (parties), mais pour le classement général des défis, commun à tous les profils. */
data class LigneClassementDefi(val profilId: Long, val pseudo: String, val avatar: String, val serie: Int, val date: Long)

@Dao
interface DefiDao {
    @Insert
    suspend fun enregistrer(defi: DefiEntity)

    /**
     * Vide l'historique des défis d'un seul joueur — bouton "Réinitialiser mes statistiques"
     * sur l'onglet Joueurs (ne touche pas les autres profils).
     */
    @Query("DELETE FROM DefiEntity WHERE profilId = :profilId")
    suspend fun reinitialiserJoueur(profilId: Long)

    /** Tous les défis d'un joueur, pour "Exporter mes statistiques". */
    @Query("SELECT * FROM DefiEntity WHERE profilId = :profilId")
    suspend fun defisDuJoueur(profilId: Long): List<DefiEntity>

    // --- Agrégats pour l'évaluation des trophées ---

    /** Nombre total de défis terminés, tous types/modes/niveaux confondus (easter egg "Touche-à-tout"). */
    @Query("SELECT COUNT(*) FROM DefiEntity WHERE profilId = :profilId")
    suspend fun compterDefisTotal(profilId: Long): Int

    /** Meilleure série jamais réalisée en défi série, par mode, tous niveaux confondus. */
    @Query(
        """
        SELECT mode, MAX(serie) as meilleur FROM DefiEntity
        WHERE profilId = :profilId AND type = 'SERIE'
        GROUP BY mode
        """,
    )
    suspend fun meilleuresSeriesDefiParMode(profilId: Long): List<MeilleureSerieDefi>

    /**
     * Meilleure série jamais réalisée en défi série, par mode, restreinte aux niveaux de
     * [niveauCodes] (retour utilisateur : paliers Platine/Diamant du barème unifié, gagnés par le
     * niveau atteint plutôt que par un seuil plus élevé).
     */
    @Query(
        """
        SELECT mode, MAX(serie) as meilleur FROM DefiEntity
        WHERE profilId = :profilId AND type = 'SERIE' AND niveauCode IN (:niveauCodes)
        GROUP BY mode
        """,
    )
    suspend fun meilleuresSeriesDefiParModeEtNiveaux(profilId: Long, niveauCodes: List<String>): List<MeilleureSerieDefi>

    /** Meilleure performance (nombre de réussites) en défi chrono, par combinaison mode × niveau. */
    @Query(
        """
        SELECT mode, niveauCode, MAX(serie) as meilleur FROM DefiEntity
        WHERE profilId = :profilId AND type = 'CHRONO'
        GROUP BY mode, niveauCode
        """,
    )
    suspend fun meilleuresReussitesChronoParCombinaison(profilId: Long): List<MeilleureReussiteChrono>

    /** Meilleure performance en défi chrono, par mode, restreinte aux niveaux de [niveauCodes]. */
    @Query(
        """
        SELECT mode, MAX(serie) as meilleur FROM DefiEntity
        WHERE profilId = :profilId AND type = 'CHRONO' AND niveauCode IN (:niveauCodes)
        GROUP BY mode
        """,
    )
    suspend fun meilleuresReussitesChronoParModeEtNiveaux(profilId: Long, niveauCodes: List<String>): List<MeilleureSerieDefi>

    /** Meilleur nombre de mots trouvés en défi mots max (lettres uniquement), tous niveaux confondus. */
    @Query("SELECT MAX(serie) FROM DefiEntity WHERE profilId = :profilId AND type = 'MOTS_MAX'")
    suspend fun meilleurScoreDefiMotsMax(profilId: Long): Int?

    /** Meilleur nombre de mots trouvés en défi mots max, restreint aux niveaux de [niveauCodes]. */
    @Query("SELECT MAX(serie) FROM DefiEntity WHERE profilId = :profilId AND type = 'MOTS_MAX' AND niveauCode IN (:niveauCodes)")
    suspend fun meilleurScoreDefiMotsMaxNiveaux(profilId: Long, niveauCodes: List<String>): Int?

    /** Meilleure série jamais réalisée en défi sans faute (mixte chiffres+lettres), tous niveaux confondus. */
    @Query("SELECT MAX(serie) FROM DefiEntity WHERE profilId = :profilId AND type = 'SANS_FAUTE'")
    suspend fun meilleureSerieSansFaute(profilId: Long): Int?

    /** Meilleure série en défi sans faute, restreinte aux niveaux de [niveauCodes]. */
    @Query("SELECT MAX(serie) FROM DefiEntity WHERE profilId = :profilId AND type = 'SANS_FAUTE' AND niveauCode IN (:niveauCodes)")
    suspend fun meilleureSerieSansFauteNiveaux(profilId: Long, niveauCodes: List<String>): Int?

    // --- Statistiques étendues (retour utilisateur : podium + progression, comme pour les parties) ---

    /** Podium (top 3) des meilleures réussites d'un joueur pour un type de défi, un mode et un niveau donnés. */
    @Query(
        """
        SELECT serie, date FROM DefiEntity
        WHERE profilId = :profilId AND type = :type AND mode = :mode AND niveauCode = :niveauCode
        ORDER BY serie DESC, date DESC
        LIMIT 3
        """,
    )
    fun podiumDefi(profilId: Long, type: String, mode: String, niveauCode: String): Flow<List<DefiResultat>>

    /** Historique chronologique complet (pas seulement le podium), pour le graphique de progression. */
    @Query(
        """
        SELECT serie, date FROM DefiEntity
        WHERE profilId = :profilId AND type = :type AND mode = :mode AND niveauCode = :niveauCode
        ORDER BY date ASC
        """,
    )
    fun historiqueDefi(profilId: Long, type: String, mode: String, niveauCode: String): Flow<List<DefiResultat>>

    /** Classement général (top 3, tous profils confondus) pour un type de défi, un mode et un niveau donnés. */
    @Query(
        """
        SELECT p.id AS profilId, p.pseudo AS pseudo, p.avatar AS avatar, d.serie AS serie, d.date AS date
        FROM DefiEntity d
        INNER JOIN ProfilEntity p ON p.id = d.profilId
        WHERE d.type = :type AND d.mode = :mode AND d.niveauCode = :niveauCode
        ORDER BY d.serie DESC, d.date DESC
        LIMIT 3
        """,
    )
    fun classementDefi(type: String, mode: String, niveauCode: String): Flow<List<LigneClassementDefi>>
}
