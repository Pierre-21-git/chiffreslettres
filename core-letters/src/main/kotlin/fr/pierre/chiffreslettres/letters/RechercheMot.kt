package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex

private const val LONGUEUR_MINIMALE_DICTIONNAIRE = 2
/** Nombre de longueurs distinctes (les plus grandes) retenues dans [dixMeilleursMots]. */
private const val NOMBRE_LONGUEURS_RETENUES = 2

/** Tous les mots de longueur maximale jouables avec ce tirage (spec §4.3). */
fun meilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> =
    dictionnaire.rechercher(tirage)

/** Un seul mot de longueur maximale jouable avec ce tirage, s'il en existe un. */
fun meilleurMot(tirage: List<Char>, dictionnaire: DictionnaireIndex): String? =
    meilleursMots(tirage, dictionnaire).firstOrNull()

/**
 * Tous les mots jouables sur ce tirage aux [NOMBRE_LONGUEURS_RETENUES] plus grandes longueurs
 * présentes (retour utilisateur : affichés à la fin d'une manche de lettres, à la place du seul
 * meilleur mot) — par exemple tous les mots de 9 lettres puis tous ceux de 8 lettres s'il y a des
 * mots de 9 lettres, sans plafond fixe sur leur nombre. Triés par longueur décroissante puis
 * ordre alphabétique français.
 */
fun dixMeilleursMots(tirage: List<Char>, dictionnaire: DictionnaireIndex): List<String> {
    val motsParLongueur = dictionnaire.rechercherAuMoins(tirage, LONGUEUR_MINIMALE_DICTIONNAIRE)
        .distinct()
        .groupBy { it.length }
    val comparateur = DictionnaireIndex.comparateurAlphabetiqueFrancais()
    return motsParLongueur.keys.sortedDescending()
        .take(NOMBRE_LONGUEURS_RETENUES)
        .flatMap { longueur -> motsParLongueur.getValue(longueur).sortedWith(comparateur) }
}
