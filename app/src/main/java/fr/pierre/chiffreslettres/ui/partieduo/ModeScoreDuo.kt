package fr.pierre.chiffreslettres.ui.partieduo

import fr.pierre.chiffreslettres.data.TypePartie

/**
 * DUO : barème indépendant, chacun son score (comme en solo). CONFRONTATION : le gagnant de
 * chaque manche garde ses points, l'autre passe à 0 ; égalité → chacun garde son score
 * (retour utilisateur, choisi avant de démarrer la partie).
 */
enum class ModeScoreDuo(val libelle: String) {
    DUO("Duo"),
    CONFRONTATION("Confrontation"),
}

fun ModeScoreDuo.versTypePartie(): TypePartie = when (this) {
    ModeScoreDuo.DUO -> TypePartie.DUO
    ModeScoreDuo.CONFRONTATION -> TypePartie.DUO_CONFRONTATION
}
