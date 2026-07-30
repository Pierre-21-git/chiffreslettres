package fr.pierre.chiffreslettres.letters

/**
 * Alphabet français par défaut, utilisé uniquement dans les tests de ce module (le contenu
 * réel vit désormais dans strings.xml côté app, cf. ConfigurationAlphabetLettres). Mêmes
 * valeurs que l'ancienne distribution en dur de SacLettres, pour ne pas changer le sens des
 * tests existants.
 */
object AlphabetTestFixture {
    val VOYELLES = setOf('A', 'E', 'I', 'O', 'U', 'Y')

    val DISTRIBUTION_BASE = mapOf(
        'A' to 9, 'B' to 2, 'C' to 2, 'D' to 3, 'E' to 15, 'F' to 2, 'G' to 2,
        'H' to 2, 'I' to 8, 'J' to 1, 'K' to 1, 'L' to 5, 'M' to 3, 'N' to 6,
        'O' to 6, 'P' to 2, 'Q' to 1, 'R' to 6, 'S' to 6, 'T' to 6, 'U' to 6,
        'V' to 2, 'W' to 1, 'X' to 1, 'Y' to 1, 'Z' to 1,
    )

    val EXCLUSIONS = mapOf(
        NiveauLettres.EMILE to setOf('X', 'Y', 'Z', 'W', 'K', 'Q', 'H', 'J'),
        NiveauLettres.NESTOR to setOf('X', 'Y', 'Z', 'W', 'K', 'Q'),
        NiveauLettres.MONIQUE to setOf('X', 'Y', 'Z', 'W'),
        NiveauLettres.MATHIEU to emptySet(),
    )

    fun creerSac(niveau: NiveauLettres): SacLettres =
        SacLettres.creer(DISTRIBUTION_BASE, VOYELLES, EXCLUSIONS.getValue(niveau))
}
