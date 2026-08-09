package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex

private const val LIMITE_DIX_MEILLEURS_MOTS = 10
private const val LONGUEUR_MINIMALE_DICTIONNAIRE = 2

/** Tous les mots de longueur maximale jouables avec ce tirage (spec §4.3). */
fun meilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> =
    dictionnaire.rechercher(tirage)

/** Un seul mot de longueur maximale jouable avec ce tirage, s'il en existe un. */
fun meilleurMot(tirage: List<Char>, dictionnaire: DictionnaireIndex): String? =
    meilleursMots(tirage, dictionnaire).firstOrNull()

/**
 * Les [limite] meilleurs mots jouables sur ce tirage (retour utilisateur : affichés à la fin
 * d'une manche de lettres, à la place du seul meilleur mot), toutes longueurs confondues et pas
 * seulement la longueur maximale — triés par longueur décroissante puis ordre alphabétique.
 */
fun dixMeilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex, limite: Int = LIMITE_DIX_MEILLEURS_MOTS): List<String> =
    dictionnaire.rechercherAuMoins(tirage, LONGUEUR_MINIMALE_DICTIONNAIRE)
        .distinct()
        .sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))
        .take(limite)
