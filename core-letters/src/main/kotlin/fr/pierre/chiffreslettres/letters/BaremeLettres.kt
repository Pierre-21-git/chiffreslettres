package fr.pierre.chiffreslettres.letters

/**
 * Valeur en points de chaque lettre, barème Scrabble français classique (donnée fonctionnelle
 * libre de droits) — utilisé par le défi Points (`TypeDefi.OBJECTIFS_POINTS`).
 */
object BaremeLettres {
    private val valeurs: Map<Char, Int> = mapOf(
        'E' to 1, 'A' to 1, 'I' to 1, 'N' to 1, 'O' to 1, 'R' to 1, 'S' to 1, 'T' to 1, 'U' to 1, 'L' to 1,
        'D' to 2, 'M' to 2, 'G' to 2,
        'B' to 3, 'C' to 3, 'P' to 3,
        'F' to 4, 'H' to 4, 'V' to 4,
        'J' to 8, 'Q' to 8,
        'K' to 10, 'W' to 10, 'X' to 10, 'Y' to 10, 'Z' to 10,
    )

    fun valeurLettre(lettre: Char): Int = valeurs[lettre.uppercaseChar()] ?: 0

    fun scoreMot(mot: String): Int = mot.sumOf { valeurLettre(it) }
}
