package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Meilleure performance en défi chrono pour une combinaison mode × niveau, pour l'évaluation des trophées. */
data class MeilleureReussiteChrono(val mode: ModeJeu, val niveauCode: String, val meilleur: Int)

/** Meilleure série en défi série pour un mode, tous niveaux confondus, pour l'évaluation des trophées. */
data class MeilleureSerieDefi(val mode: ModeJeu, val meilleur: Int)

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

    /** Nombre total de défis terminés (tous types/modes/niveaux confondus). */
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
}
