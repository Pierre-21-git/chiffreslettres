package fr.pierre.chiffreslettres.ui.partieduo

import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche

/** Résultat de la comparaison d'une manche entre les deux joueurs du mode duo. */
enum class VainqueurManche { JOUEUR1, JOUEUR2, EGALITE }

/**
 * Compare deux écarts à la cible en chiffres (le plus petit gagne). Une proposition absente
 * (null) équivaut à un écart infini — perd toujours face à une proposition, même mauvaise.
 */
fun vainqueurMancheChiffres(ecartJoueur1: Int?, ecartJoueur2: Int?): VainqueurManche {
    if (ecartJoueur1 == ecartJoueur2) return VainqueurManche.EGALITE
    val e1 = ecartJoueur1 ?: Int.MAX_VALUE
    val e2 = ecartJoueur2 ?: Int.MAX_VALUE
    return if (e1 < e2) VainqueurManche.JOUEUR1 else VainqueurManche.JOUEUR2
}

/** Compare deux mots en lettres (le plus long gagne, mot absent/invalide = longueur 0). */
fun vainqueurMancheLettres(motJoueur1: String?, motJoueur2: String?): VainqueurManche {
    val l1 = motJoueur1?.length ?: 0
    val l2 = motJoueur2?.length ?: 0
    return when {
        l1 == l2 -> VainqueurManche.EGALITE
        l1 > l2 -> VainqueurManche.JOUEUR1
        else -> VainqueurManche.JOUEUR2
    }
}

/**
 * Manche lettres uniquement (retour utilisateur, parties duo et confrontation) : si le mot
 * soumis par un joueur était invalide mais plus long que le mot valide de l'autre, ce dernier
 * marque un score égal au nombre de lettres du mot invalide — un remplacement, pas un bonus
 * ajouté à son propre score (ex. A invalide 8 lettres, B valide 7 lettres → B marque 8, pas 15).
 * Sans effet en chiffres (pas de notion de "mot invalide" hors lettres).
 */
fun appliquerBonusMotInvalide(a: ResultatManche, b: ResultatManche): Pair<ResultatManche, ResultatManche> {
    if (a.mode != ModeJeu.LETTRES) return a to b
    var resultatA = a
    var resultatB = b
    a.longueurMotInvalide?.let { longueur -> if (longueur > resultatB.score) resultatB = resultatB.copy(score = longueur) }
    b.longueurMotInvalide?.let { longueur -> if (longueur > resultatA.score) resultatA = resultatA.copy(score = longueur) }
    return resultatA to resultatB
}
