package fr.pierre.chiffreslettres.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity
data class ProfilEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pseudo: String,
    val dateCreation: Long,
    val avatar: String = AVATAR_PAR_DEFAUT,
    /** Code langue ISO (ex. "fr", "en", "de", "es"), retour utilisateur : un profil = une langue d'affichage. */
    val langue: String = LANGUE_PAR_DEFAUT,
)

const val AVATAR_PAR_DEFAUT = "🙂"

/** Valeur de la migration Room historique (profils créés avant l'ajout de la colonne langue,
 * quand l'app était uniquement en français) : ne pas modifier, cf. AppDatabaseProvider. */
const val LANGUE_PAR_DEFAUT = "fr"

private val LANGUES_SUPPORTEES = setOf("fr", "en", "de", "es")

/**
 * Langue par défaut d'un nouveau profil (retour mainteneur F-Droid : ne pas imposer le
 * français à un utilisateur anglophone) : suit la locale système si elle fait partie des
 * langues supportées, sinon retombe sur l'anglais plutôt que le français.
 */
fun langueParDefautSysteme(): String {
    val langueSysteme = Locale.getDefault().language
    return if (langueSysteme in LANGUES_SUPPORTEES) langueSysteme else "en"
}
