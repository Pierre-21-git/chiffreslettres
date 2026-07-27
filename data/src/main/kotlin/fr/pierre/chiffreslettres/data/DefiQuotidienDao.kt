package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DefiQuotidienDao {
    /** Ignore si une réussite existe déjà pour ce profil et ce jour (verrouillage, retour utilisateur). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enregistrerReussite(entity: DefiQuotidienEntity)

    @Query("SELECT * FROM DefiQuotidienEntity WHERE profilId = :profilId AND jour = :jour")
    suspend fun reussiteDuJour(profilId: Long, jour: String): DefiQuotidienEntity?

    /** Tous les jours réussis d'un profil, du plus récent au plus ancien. */
    @Query("SELECT jour FROM DefiQuotidienEntity WHERE profilId = :profilId ORDER BY jour DESC")
    suspend fun joursReussis(profilId: Long): List<String>

    @Query("DELETE FROM DefiQuotidienEntity WHERE profilId = :profilId")
    suspend fun reinitialiserJoueur(profilId: Long)
}
