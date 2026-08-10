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

    /**
     * Purge les trophées débloqués dont l'id ne correspond plus à aucun trophée du catalogue
     * actuel (retour utilisateur : les refontes de seuils renomment parfois des ids, ex.
     * `defi_serie_..._10_mathieu` → `..._12_mathieu` — l'ancienne ligne reste sinon en base pour
     * toujours et gonfle artificiellement le compteur "x/y débloquées").
     */
    @Query("DELETE FROM TropheeEntity WHERE profilId = :profilId AND trophyId NOT IN (:idsValides)")
    suspend fun supprimerOrphelins(profilId: Long, idsValides: List<String>)
}
