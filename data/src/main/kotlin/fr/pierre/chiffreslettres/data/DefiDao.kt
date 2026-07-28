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

    /** Meilleure performance (nombre de réussites) en défi chrono, par combinaison mode × niveau. */
    @Query(
        """
        SELECT mode, niveauCode, MAX(serie) as meilleur FROM DefiEntity
        WHERE profilId = :profilId AND type = 'CHRONO'
        GROUP BY mode, niveauCode
        """,
    )
    suspend fun meilleuresReussitesChronoParCombinaison(profilId: Long): List<MeilleureReussiteChrono>
}
