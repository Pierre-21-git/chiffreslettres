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
    /** Uniquement renseigné pour DUO/DUO_CONFRONTATION : score total strictement supérieur à celui de l'adversaire. Null pour les autres types de partie. */
    val victoireDuel: Boolean? = null,
    /** Score total exactement égal à celui de l'adversaire (easter egg "Ex-aequo"). Renseigné pour les mêmes types que [victoireDuel], sauf Duel mots Confrontation (pas de signal d'égalité disponible côté ViewModel). */
    val egaliteDuel: Boolean? = null,
)
