package fr.pierre.chiffreslettres.letters

/**
 * Encode les 4 niveaux de difficulté du mode Lettres (spec §4.2), noms validés avec
 * l'utilisateur. [manchesParMode] et [dureeSecondesPartieStructuree] sont fixes (pas
 * réglables par le joueur, retour utilisateur) et ne s'appliquent qu'en partie
 * structurée — l'entraînement libre est sans limite de temps ni de nombre de manches.
 */
enum class NiveauLettres(
    val label: String,
    val lettresExclues: Set<Char>,
    val manchesParMode: Int,
    val dureeSecondesPartieStructuree: Int,
) {
    EMILE("Assez facile, Émile", setOf('X', 'Y', 'Z', 'W', 'K', 'Q', 'H', 'J'), manchesParMode = 2, dureeSecondesPartieStructuree = 110),
    NESTOR("Ça va encore, Nestor", setOf('X', 'Y', 'Z', 'W', 'K', 'Q'), manchesParMode = 3, dureeSecondesPartieStructuree = 90),
    MONIQUE("Ça se complique, Monique", setOf('X', 'Y', 'Z', 'W'), manchesParMode = 4, dureeSecondesPartieStructuree = 50),
    MATHIEU("Là c'est sérieux, Mathieu", emptySet(), manchesParMode = 5, dureeSecondesPartieStructuree = 40),
}
