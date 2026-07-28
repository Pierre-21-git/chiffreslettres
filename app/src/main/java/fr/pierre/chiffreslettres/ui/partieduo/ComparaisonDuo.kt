package fr.pierre.chiffreslettres.ui.partieduo

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
