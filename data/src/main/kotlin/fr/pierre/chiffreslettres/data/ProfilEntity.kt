package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfilEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pseudo: String,
    val dateCreation: Long,
    val avatar: String = AVATAR_PAR_DEFAUT,
)

const val AVATAR_PAR_DEFAUT = "🙂"
