package fr.pierre.chiffreslettres.letters

/**
 * Valeur en points de chaque lettre (défi Points, `TypeDefi.OBJECTIFS_POINTS`) — barème classique
 * de points par lettre, dépendant de la langue (retour utilisateur : les lettres rares n'ont pas
 * la même fréquence d'une langue à l'autre). Ce module reste du Kotlin pur sans dépendance
 * Android : le barème de la langue courante est fourni par l'appelant
 * (`ConfigurationAlphabetLettres.baremeLettres`, chargé depuis strings.xml côté app), pas connu
 * ici.
 */
object BaremeLettres {
    /** Barème français, utilisé par défaut et par les tests purs de ce module. */
    val FRANCAIS: Map<Char, Int> = mapOf(
        'E' to 1, 'A' to 1, 'I' to 1, 'N' to 1, 'O' to 1, 'R' to 1, 'S' to 1, 'T' to 1, 'U' to 1, 'L' to 1,
        'D' to 2, 'M' to 2, 'G' to 2,
        'B' to 3, 'C' to 3, 'P' to 3,
        'F' to 4, 'H' to 4, 'V' to 4,
        'J' to 8, 'Q' to 8,
        'K' to 10, 'W' to 10, 'X' to 10, 'Y' to 10, 'Z' to 10,
    )

    fun valeurLettre(lettre: Char, valeurs: Map<Char, Int>): Int = valeurs[lettre.uppercaseChar()] ?: 0

    fun scoreMot(mot: String, valeurs: Map<Char, Int>): Int = mot.sumOf { valeurLettre(it, valeurs) }
}
