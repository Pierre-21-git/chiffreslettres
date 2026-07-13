package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex

/** Tous les mots de longueur maximale jouables avec ce tirage (spec §4.3). */
fun meilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> =
    dictionnaire.rechercher(tirage)

/** Un seul mot de longueur maximale jouable avec ce tirage, s'il en existe un. */
fun meilleurMot(tirage: List<Char>, dictionnaire: DictionnaireIndex): String? =
    meilleursMots(tirage, dictionnaire).firstOrNull()
