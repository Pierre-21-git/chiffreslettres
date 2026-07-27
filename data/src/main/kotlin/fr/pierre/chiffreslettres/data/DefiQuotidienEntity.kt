package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Une réussite du défi quotidien pour un profil et un jour donnés (retour utilisateur). */
@Entity(
    primaryKeys = ["profilId", "jour"],
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
data class DefiQuotidienEntity(
    val profilId: Long,
    /** Format ISO (yyyy-MM-dd), date locale du joueur au moment de la réussite. */
    val jour: String,
    val dateReussite: Long,
)
