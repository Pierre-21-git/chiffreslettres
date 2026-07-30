package fr.pierre.chiffreslettres.letters

/**
 * Encode les 4 niveaux de difficulté du mode Lettres (spec §4.2). Ni le libellé affiché ni
 * les lettres exclues ne vivent ici (retour utilisateur : externalisés dans strings.xml
 * côté app pour permettre une déclinaison par langue — un autre alphabet peut avoir
 * d'autres lettres rares à exclure). Ce module reste du Kotlin pur sans dépendance
 * Android ; voir `ConfigurationAlphabetLettres` côté app pour le contenu réel.
 * [manchesParMode] et [dureeSecondesPartieStructuree] sont fixes (pas réglables par le
 * joueur, retour utilisateur) et ne s'appliquent qu'en partie structurée — l'entraînement
 * libre est sans limite de temps ni de nombre de manches.
 */
enum class NiveauLettres(
    val manchesParMode: Int,
    val dureeSecondesPartieStructuree: Int,
) {
    EMILE(manchesParMode = 2, dureeSecondesPartieStructuree = 110),
    NESTOR(manchesParMode = 3, dureeSecondesPartieStructuree = 90),
    MONIQUE(manchesParMode = 4, dureeSecondesPartieStructuree = 50),
    MATHIEU(manchesParMode = 5, dureeSecondesPartieStructuree = 40),
}
