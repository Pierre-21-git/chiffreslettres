package fr.pierre.chiffreslettres.numbers

val TOUTES_OPERATIONS = setOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)
val OPERATIONS_PLUS_MOINS = setOf(Operation.PLUS, Operation.MOINS)
val OPERATIONS_PLUS_MOINS_FOIS = setOf(Operation.PLUS, Operation.MOINS, Operation.FOIS)

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
    /**
     * Restreint la multiplication aux tables de 1 à cette valeur (retour utilisateur : un des
     * deux facteurs doit être dans `1..tableMultiplicationMax`, l'autre dans `1..10`, comme une
     * table de multiplication apprise à l'école). `null` = multiplication libre (défaut).
     */
    val tableMultiplicationMax: Int? = null,
) {
    EMILE(
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS,
        garantieSolution = true,
        manchesParMode = 2,
        dureeSecondesPartieStructuree = 120,
    ),
    /**
     * Comme [EMILE], avec la multiplication en plus (retour utilisateur : niveau intermédiaire
     * avant Nestor, qui a les 4 opérations), limitée aux tables de 1 à 5.
     */
    ODILE(
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS_FOIS,
        garantieSolution = true,
        manchesParMode = 2,
        dureeSecondesPartieStructuree = 120,
        tableMultiplicationMax = 5,
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
