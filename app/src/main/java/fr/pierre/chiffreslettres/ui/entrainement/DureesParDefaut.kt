package fr.pierre.chiffreslettres.ui.entrainement

/**
 * Durées de chrono par défaut (spec §5). Constantes en dur pour l'instant ;
 * un futur écran Réglages (phase 4) les rendra configurables sans toucher
 * aux ViewModels de manche, qui prennent déjà la durée en paramètre.
 */
object DureesParDefaut {
    const val CHIFFRES_SECONDES = 45
    const val LETTRES_SECONDES = 40
}
