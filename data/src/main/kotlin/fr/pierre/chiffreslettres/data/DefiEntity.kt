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
    /** Non signifiant pour `TypeDefi.SANS_FAUTE` (défi mixte chiffres+lettres) : toujours `ModeJeu.CHIFFRES` par convention. */
    val mode: ModeJeu,
    /** Nom de l'enum `Niveau`/`NiveauLettres` correspondant, comme `MancheEntity.niveauCode`. */
    val niveauCode: String,
    val type: TypeDefi,
    /**
     * SERIE : nombre de réussites d'affilée avant l'échec qui a terminé le défi.
     * CHRONO : nombre total de réussites obtenues avant l'épuisement du budget de temps.
     * MOTS_MAX : nombre de mots distincts trouvés sur le même tirage avant l'arrêt du défi.
     * SANS_FAUTE : nombre de manches (chiffres et lettres confondues) réussies d'affilée.
     */
    val serie: Int,
    val date: Long,
    /** Durée totale du défi (toutes manches confondues), en secondes — trophée "100 heures de jeu". */
    val dureeSecondes: Int = 0,
)
