package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profilId: Long,
    val date: Long,
    val type: TypePartie,
    val scoreTotal: Int,
)
