package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Une série de défi terminée (du début jusqu'à l'échec), pour un joueur/mode/niveau donné. */
@Entity(
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
data class DefiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profilId: Long,
    val mode: ModeJeu,
    /** Nom de l'enum `Niveau`/`NiveauLettres` correspondant, comme `MancheEntity.niveauCode`. */
    val niveauCode: String,
    val type: TypeDefi,
    /**
     * SERIE : nombre de réussites d'affilée avant l'échec qui a terminé le défi.
     * CHRONO : nombre total de réussites obtenues avant l'épuisement du budget de temps.
     */
    val serie: Int,
    val date: Long,
)
