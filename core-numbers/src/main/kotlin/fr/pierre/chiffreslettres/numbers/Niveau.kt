package fr.pierre.chiffreslettres.numbers

val TOUTES_OPERATIONS = setOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)
val OPERATIONS_PLUS_MOINS = setOf(Operation.PLUS, Operation.MOINS)

/** Encode le tableau des 7 niveaux de difficulté du mode Chiffres (spec §3.2). */
enum class Niveau(
    val label: String,
    val cibleMin: Int,
    val cibleMax: Int,
    val operations: Set<Operation>,
    val garantieSolution: Boolean,
) {
    FACILE_100(
        label = "Facile ≤ 100",
        cibleMin = 10,
        cibleMax = 100,
        operations = TOUTES_OPERATIONS,
        garantieSolution = true,
    ),
    ALEATOIRE_100(
        label = "Aléatoire ≤ 100",
        cibleMin = 10,
        cibleMax = 100,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
    ),
    FACILE_100_PLUS_MOINS(
        label = "Facile ≤ 100 (+ / −)",
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS,
        garantieSolution = true,
    ),
    ALEATOIRE_100_PLUS_MOINS(
        label = "Aléatoire ≤ 100 (+ / −)",
        cibleMin = 10,
        cibleMax = 100,
        operations = OPERATIONS_PLUS_MOINS,
        garantieSolution = false,
    ),
    FACILE_200(
        label = "Facile ≤ 200",
        cibleMin = 10,
        cibleMax = 200,
        operations = TOUTES_OPERATIONS,
        garantieSolution = true,
    ),
    ALEATOIRE_200(
        label = "Aléatoire ≤ 200",
        cibleMin = 10,
        cibleMax = 200,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
    ),
    NORMAL_OFFICIEL(
        label = "Normal (officiel)",
        cibleMin = 100,
        cibleMax = 999,
        operations = TOUTES_OPERATIONS,
        garantieSolution = false,
    ),
}
