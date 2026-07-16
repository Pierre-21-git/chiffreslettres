package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Un trophée débloqué par un joueur (id venant de `CatalogueTrophees`), avec sa date d'obtention. */
@Entity(
    primaryKeys = ["profilId", "trophyId"],
    foreignKeys = [
        ForeignKey(
            entity = ProfilEntity::class,
            parentColumns = ["id"],
            childColumns = ["profilId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("profilId")],
)
data class TropheeEntity(
    val profilId: Long,
    val trophyId: String,
    val dateDebloque: Long,
)
