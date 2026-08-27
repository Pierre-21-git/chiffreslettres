package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex

private const val LONGUEUR_MINIMALE_DICTIONNAIRE = 2
/** Nombre minimal de mots visé par [dixMeilleursMots] (retour utilisateur : certains tirages
 *  n'affichaient que 5 à 7 mots avec l'ancienne règle "2 plus grandes longueurs seulement"). */
private const val NOMBRE_MINIMAL_MOTS = 10

/** Tous les mots de longueur maximale jouables avec ce tirage (spec §4.3). */
fun meilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> =
    dictionnaire.rechercher(tirage)

/** Un seul mot de longueur maximale jouable avec ce tirage, s'il en existe un. */
fun meilleurMot(tirage: List<Char>, dictionnaire: DictionnaireIndex): String? =
    meilleursMots(tirage, dictionnaire).firstOrNull()

/**
 * Tous les mots jouables sur ce tirage, par tranches de longueur décroissante, jusqu'à atteindre
 * [NOMBRE_MINIMAL_MOTS] (retour utilisateur : affichés à la fin d'une manche de lettres, à la
 * place du seul meilleur mot) — par exemple tous les mots de 9 lettres, puis tous ceux de 8
 * lettres si le total est encore inférieur à 10, etc. Une tranche de longueur n'est jamais
 * tronquée : le total peut donc dépasser 10. Triés par longueur décroissante puis ordre
 * alphabétique français.
 */
fun dixMeilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> {
    val motsParLongueur = dictionnaire.rechercherAuMoins(tirage, LONGUEUR_MINIMALE_DICTIONNAIRE)
        .distinct()
        .groupBy { it.length }
    val comparateur = DictionnaireIndex.comparateurAlphabetiqueFrancais()
    val resultat = mutableListOf<String>()
    for (longueur in motsParLongueur.keys.sortedDescending()) {
        if (resultat.size >= NOMBRE_MINIMAL_MOTS) break
        resultat += motsParLongueur.getValue(longueur).sortedWith(comparateur)
    }
    return resultat
}
