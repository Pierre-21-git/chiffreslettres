package fr.pierre.chiffreslettres.numbers

val TOUTES_OPERATIONS = setOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)
val OPERATIONS_PLUS_MOINS = setOf(Operation.PLUS, Operation.MOINS)

/**
 * Encode les 4 niveaux de difficulté du mode Chiffres (spec §3.2), noms validés avec
 * l'utilisateur. [manchesParMode] et [dureeSecondesPartieStructuree] sont fixes (pas
 * réglables par le joueur, retour utilisateur) et ne s'appliquent qu'en partie
 * structurée — l'entraînement libre est sans limite de temps ni de nombre de manches.
 */
enum class Niveau(
    val label: String,
    val cibleMin: Int,
    val cibleMax: Int,
    val operations: Set<Operation>,
    val garantieSolution: Boolean,
    val manchesParMode: Int,
    val dureeSecondesPartieStructuree: Int,
) {
    EMILE(
        label = "Assez facile, Émile",
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS,
        garantieSolution = true,
        manchesParMode = 2,
        dureeSecondesPartieStructuree = 120,
    ),
    NESTOR(
        label = "Ça va encore, Nestor",
        cibleMin = 10,
        cibleMax = 100,
        operations = TOUTES_OPERATIONS,
        garantieSolution = true,
        manchesParMode = 3,
        dureeSecondesPartieStructuree = 100,
    ),
    MONIQUE(
        label = "Ça se complique, Monique",
        cibleMin = 10,
        cibleMax = 500,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
        manchesParMode = 4,
        dureeSecondesPartieStructuree = 60,
    ),
    MATHIEU(
        label = "Là c'est sérieux, Mathieu",
        cibleMin = 100,
        cibleMax = 999,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
        manchesParMode = 5,
        dureeSecondesPartieStructuree = 45,
    ),
}
