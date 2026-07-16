package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TropheeDao {
    /** Idempotent : un trophée déjà débloqué n'est jamais réécrit (ni sa date perdue). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun debloquerSiAbsent(trophee: TropheeEntity)

    @Query("SELECT * FROM TropheeEntity WHERE profilId = :profilId")
    fun tropheesDebloques(profilId: Long): Flow<List<TropheeEntity>>

    @Query("DELETE FROM TropheeEntity WHERE profilId = :profilId")
    suspend fun reinitialiserJoueur(profilId: Long)
}
