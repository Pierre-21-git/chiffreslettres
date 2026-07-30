package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfilDao {
    @Insert
    suspend fun inserer(profil: ProfilEntity): Long

    @Delete
    suspend fun supprimer(profil: ProfilEntity)

    @Query("SELECT * FROM ProfilEntity ORDER BY dateCreation ASC")
    fun tous(): Flow<List<ProfilEntity>>

    @Query("SELECT * FROM ProfilEntity WHERE id = :id")
    suspend fun parId(id: Long): ProfilEntity?

    @Query("UPDATE ProfilEntity SET pseudo = :pseudo WHERE id = :id")
    suspend fun renommer(id: Long, pseudo: String)

    @Query("UPDATE ProfilEntity SET avatar = :avatar WHERE id = :id")
    suspend fun definirAvatar(id: Long, avatar: String)

    @Query("UPDATE ProfilEntity SET langue = :langue WHERE id = :id")
    suspend fun definirLangue(id: Long, langue: String)
}
