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

    /** Toujours 3 en lettres pour série/chrono (retour utilisateur) : trouver des mots valides est plus dur que les chiffres, un objectif tiré au-delà rend le défi trop difficile. Non utilisé pour [TypeDefi.OBJECTIFS_POINTS] (objectif fixé par le niveau, pas par le tirage). */
    private const val OBJECTIF_LETTRES = 3

    /** Types tirables en chiffres (retour utilisateur : sans faute retiré du tirage — reste jouable en défi libre depuis le menu). */
    private val TYPES_TIRABLES_CHIFFRES = listOf(TypeDefi.SERIE, TypeDefi.CHRONO)

    /**
     * Types tirables en lettres. Mots max en est délibérément absent : défi à tirage unique, sans
     * chaînage de manches (cf. commentaire sur `Routes.JEU_DEFI_MOTS_MAX_PATTERN`), donc pas
     * compatible avec la logique d'objectif quotidien — l'inclure ici faisait tirer le type mais
     * router silencieusement vers l'écran chrono (bug découvert en ajoutant le défi points).
     */
    private val TYPES_TIRABLES_LETTRES = listOf(TypeDefi.SERIE, TypeDefi.CHRONO, TypeDefi.OBJECTIFS_POINTS)

    /** [jour] au format ISO (yyyy-MM-dd, cf. `LocalDate.toString()`). */
    fun pour(profilId: Long, jour: String): TirageDefiQuotidien {
        val rng = Random("$profilId-$jour".hashCode().toLong())
        val mode = ModeJeu.entries[rng.nextInt(ModeJeu.entries.size)]
        val typesTirables = if (mode == ModeJeu.LETTRES) TYPES_TIRABLES_LETTRES else TYPES_TIRABLES_CHIFFRES
        val type = typesTirables[rng.nextInt(typesTirables.size)]
        val objectif = when {
            type == TypeDefi.OBJECTIFS_POINTS -> 0
            mode == ModeJeu.LETTRES -> OBJECTIF_LETTRES
            type == TypeDefi.SERIE -> rng.nextInt(OBJECTIF_MIN_SERIE, OBJECTIF_MAX_SERIE + 1)
            else -> rng.nextInt(OBJECTIF_MIN_CHRONO, OBJECTIF_MAX_CHRONO + 1)
        }
        return TirageDefiQuotidien(mode, type, objectif)
    }
}
