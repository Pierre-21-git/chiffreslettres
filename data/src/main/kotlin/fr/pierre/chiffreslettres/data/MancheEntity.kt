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
    /** Longueur du mot soumis quand il était invalide (mode Lettres), pour l'easter egg "Le mot le plus long jamais tenté". */
    val longueurMotInvalide: Int? = null,
    // --- Easter eggs "Chiffres" + temps de jeu (refonte 2026-08) ---
    /** Cible du tirage (mode Chiffres uniquement), pour l'easter egg "Nombre premier". */
    val cibleChiffres: Int? = null,
    /**
     * Nombre d'opérations utilisées par le joueur (mode Chiffres). 1 = "Chemin minimal", 5 =
     * tous les jetons combinés = "Chirurgical" (6 nombres → 5 combinaisons pour n'en faire qu'un).
     */
    val nombreOperationsChiffres: Int? = null,
    /** Résultat intermédiaire le plus élevé parmi les opérations du joueur (mode Chiffres), pour l'easter egg "Calcul mental" (aucune étape à 3 chiffres ou plus). */
    val maxEtapeIntermediaireChiffres: Int? = null,
    /** Durée écoulée entre le début de la manche et sa validation (chiffres et lettres), pour les easter eggs "Speedrun" et "100 heures de jeu". Null si manche non chronométrée. */
    val dureeSecondesManche: Int? = null,
    /** Temps restant au moment de la validation (mode Chiffres), pour l'easter egg "Va-tout". */
    val tempsRestantSecondesValidation: Int? = null,
)
