package fr.pierre.chiffreslettres.numbers

val TOUTES_OPERATIONS = setOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)
val OPERATIONS_PLUS_MOINS = setOf(Operation.PLUS, Operation.MOINS)

/**
 * Encode les 4 niveaux de difficulté du mode Chiffres (spec §3.2). Le libellé affiché
 * (ex. "Assez facile, Émile") vit dans strings.xml, pas ici — ce module est du Kotlin pur
 * sans dépendance Android, et le libellé doit être traduisible par langue (retour
 * utilisateur, cf. `libelleRes` côté app). [manchesParMode] et
 * [dureeSecondesPartieStructuree] sont fixes (pas réglables par le joueur, retour
 * utilisateur) et ne s'appliquent qu'en partie structurée — l'entraînement libre est sans
 * limite de temps ni de nombre de manches.
 */
enum class Niveau(
    val cibleMin: Int,
    val cibleMax: Int,
    val operations: Set<Operation>,
    val garantieSolution: Boolean,
    val manchesParMode: Int,
    val dureeSecondesPartieStructuree: Int,
) {
    EMILE(
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS,
        garantieSolution = true,
        manchesParMode = 2,
        dureeSecondesPartieStructuree = 120,
    ),
    NESTOR(
        cibleMin = 10,
        cibleMax = 100,
        operations = TOUTES_OPERATIONS,
        garantieSolution = true,
        manchesParMode = 3,
        dureeSecondesPartieStructuree = 100,
    ),
    MONIQUE(
        cibleMin = 10,
        cibleMax = 500,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
        manchesParMode = 4,
        dureeSecondesPartieStructuree = 60,
    ),
    MATHIEU(
        cibleMin = 100,
        cibleMax = 999,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
        manchesParMode = 5,
        dureeSecondesPartieStructuree = 45,
    ),
}
