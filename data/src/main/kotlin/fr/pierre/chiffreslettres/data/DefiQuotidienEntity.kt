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
    /**
     * Niveau (nom d'enum [fr.pierre.chiffreslettres.numbers.Niveau] ou
     * [fr.pierre.chiffreslettres.letters.NiveauLettres]) du défi joué ce jour-là — le tirage du
     * jour ne propose que chiffres OU lettres, jamais les deux (retour utilisateur). Nullable :
     * absent sur les réussites enregistrées avant l'ajout des trophées défi quotidien niveau.
     */
    val niveau: String? = null,
    /**
     * Tous les niveaux déjà réussis ce jour-là (retour utilisateur), séparés par des virgules
     * (ex. "EMILE,NESTOR") — chacun doit rester verrouillé, pas seulement [niveau] (le plus
     * élevé). Vide sur les réussites enregistrées avant l'ajout de ce champ.
     */
    val niveauxReussis: String = "",
)
