package fr.pierre.chiffreslettres.letters

/** Encode les 3 niveaux de difficulté du mode Lettres (spec §4.2). */
enum class NiveauLettres(val label: String, val lettresExclues: Set<Char>) {
    FACILE("Facile", setOf('X', 'Y', 'Z', 'W', 'K', 'Q')),
    MOYEN("Moyen", setOf('X', 'Y', 'Z', 'W')),
    NORMAL("Normal", emptySet()),
}
