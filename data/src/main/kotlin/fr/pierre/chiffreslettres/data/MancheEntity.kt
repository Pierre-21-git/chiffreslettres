package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class MancheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val ordre: Int,
    val mode: ModeJeu,
    /** Nom de l'enum `Niveau`/`NiveauLettres` correspondant (traduit côté :app). */
    val niveauCode: String,
    val score: Int,
    /** Renseigné uniquement pour les manches lettres, sert au classement "plus long mot". */
    val motJoue: String? = null,
)
