package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Une meilleure série de défi (nombre de réussites + date), pour le classement personnel d'un joueur. */
data class MeilleurDefi(val serie: Int, val date: Long)

@Dao
interface DefiDao {
    @Insert
    suspend fun enregistrer(defi: DefiEntity)

    /** Top 3 des meilleures séries d'un joueur, pour un mode et un niveau donnés. */
    @Query(
        """
        SELECT serie, date FROM DefiEntity
        WHERE profilId = :profilId AND mode = :mode AND niveauCode = :niveauCode
        ORDER BY serie DESC, date DESC
        LIMIT 3
        """,
    )
    fun meilleursDefisParNiveau(profilId: Long, mode: ModeJeu, niveauCode: String): Flow<List<MeilleurDefi>>

    /**
     * Vide l'historique des défis d'un seul joueur — bouton "Réinitialiser mes statistiques"
     * sur l'onglet Joueurs (ne touche pas les autres profils).
     */
    @Query("DELETE FROM DefiEntity WHERE profilId = :profilId")
    suspend fun reinitialiserJoueur(profilId: Long)
}
