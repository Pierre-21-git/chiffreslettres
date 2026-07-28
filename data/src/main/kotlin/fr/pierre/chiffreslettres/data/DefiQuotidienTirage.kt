package fr.pierre.chiffreslettres.data

import kotlin.random.Random

/**
 * Tirage du jour pour le défi quotidien (retour utilisateur) : mode, type et objectif à
 * atteindre, déterminés de façon stable pour un profil et un jour donnés (même profil + même
 * jour => même tirage toute la journée, différent d'un profil à l'autre).
 */
data class TirageDefiQuotidien(val mode: ModeJeu, val type: TypeDefi, val objectif: Int)

object DefiQuotidienTirage {
    // Réduit après retour utilisateur (5-10 jugé trop long pour un défi quotidien "rapide").
    private const val OBJECTIF_MIN_SERIE = 3
    private const val OBJECTIF_MAX_SERIE = 5
    private const val OBJECTIF_MIN_CHRONO = 3
    private const val OBJECTIF_MAX_CHRONO = 5

    /** [jour] au format ISO (yyyy-MM-dd, cf. `LocalDate.toString()`). */
    fun pour(profilId: Long, jour: String): TirageDefiQuotidien {
        val rng = Random("$profilId-$jour".hashCode().toLong())
        val mode = ModeJeu.entries[rng.nextInt(ModeJeu.entries.size)]
        val type = TypeDefi.entries[rng.nextInt(TypeDefi.entries.size)]
        val objectif = when (type) {
            TypeDefi.SERIE -> rng.nextInt(OBJECTIF_MIN_SERIE, OBJECTIF_MAX_SERIE + 1)
            TypeDefi.CHRONO -> rng.nextInt(OBJECTIF_MIN_CHRONO, OBJECTIF_MAX_CHRONO + 1)
        }
        return TirageDefiQuotidien(mode, type, objectif)
    }
}
