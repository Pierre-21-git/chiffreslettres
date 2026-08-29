package fr.pierre.chiffreslettres.data

import java.time.LocalDate

/** Plus longue série de jours consécutifs dans une liste triée croissante (fonction pure, testable). */
internal fun plusLongueSerieDeJours(joursTries: List<LocalDate>): Int {
    if (joursTries.isEmpty()) return 0
    var meilleure = 1
    var courante = 1
    for (i in 1 until joursTries.size) {
        courante = if (joursTries[i] == joursTries[i - 1].plusDays(1)) courante + 1 else 1
        if (courante > meilleure) meilleure = courante
    }
    return meilleure
}

/**
 * Série en cours, en remontant depuis [aujourdHui] (ou la veille si [aujourdHui] n'y est pas
 * encore, pour ne pas casser la série avant que le défi du jour soit joué) — fonction pure, testable.
 */
internal fun serieEnCoursDeJours(jours: Set<LocalDate>, aujourdHui: LocalDate): Int {
    var courant = if (aujourdHui in jours) aujourdHui else aujourdHui.minusDays(1)
    var serie = 0
    while (courant in jours) {
        serie++
        courant = courant.minusDays(1)
    }
    return serie
}

/** Ordre de difficulté croissant des niveaux, chiffres et lettres confondus (mêmes noms d'enum des deux côtés). */
private val ORDRE_NIVEAUX = listOf("EMILE", "NESTOR", "MONIQUE", "MATHIEU")

internal fun rangNiveau(code: String?): Int = code?.let { ORDRE_NIVEAUX.indexOf(it) } ?: -1

internal fun niveauxDepuisCsv(csv: String): Set<String> = csv.split(",").filter { it.isNotBlank() }.toSet()

/**
 * Nouvel état (niveau le plus élevé, ensemble des niveaux réussis en CSV) après une réussite au
 * niveau [niveauReussi], sachant l'état existant pour ce jour — fonction pure, testable. Null si
 * [niveauReussi] est déjà dans l'ensemble existant (déjà verrouillé, ne doit rien changer, cf.
 * [DefiQuotidienRepository.enregistrerReussite]).
 */
internal fun apresReussiteDefiQuotidien(
    niveauLePlusHautExistant: String?,
    niveauxReussisExistants: String,
    niveauReussi: String,
): Pair<String, String>? {
    val ensembleExistant = niveauxDepuisCsv(niveauxReussisExistants)
    if (niveauReussi in ensembleExistant) return null
    val niveauLePlusHaut = if (rangNiveau(niveauReussi) > rangNiveau(niveauLePlusHautExistant)) niveauReussi else niveauLePlusHautExistant ?: niveauReussi
    return niveauLePlusHaut to (ensembleExistant + niveauReussi).joinToString(",")
}

/**
 * Suivi du défi quotidien (retour utilisateur) : réussite du jour (le niveau le plus élevé
 * réussi est conservé, pas seulement le premier — voir [enregistrerReussite]) et séries de
 * jours consécutifs — la meilleure série (pour les trophées, comme toutes les autres stats de
 * trophées) et la série en cours (affichage seulement).
 */
class DefiQuotidienRepository(private val dao: DefiQuotidienDao) {

    suspend fun reussiteDuJour(profilId: Long, jour: String): Boolean = dao.reussiteDuJour(profilId, jour) != null

    /** Niveau (nom d'enum) le plus élevé déjà réussi aujourd'hui, ou null si aucune réussite ce jour (retour utilisateur : affiché dans le message "défi déjà réussi"). */
    suspend fun niveauReussiAujourdhui(profilId: Long, jour: String): String? = dao.reussiteDuJour(profilId, jour)?.niveau

    /**
     * Tous les niveaux déjà réussis aujourd'hui (retour utilisateur : chacun doit rester
     * verrouillé, pas seulement le plus élevé — sinon rejouer un niveau supérieur déverrouillait
     * à tort le niveau réussi précédemment).
     */
    suspend fun niveauxReussisAujourdhui(profilId: Long, jour: String): Set<String> =
        niveauxDepuisCsv(dao.reussiteDuJour(profilId, jour)?.niveauxReussis ?: "")

    /**
     * Enregistre une réussite du défi quotidien. Si aucune réussite n'existe encore pour ce jour,
     * l'insère. Sinon, ajoute ce niveau à l'ensemble des niveaux réussis aujourd'hui (retour
     * utilisateur : chacun reste verrouillé) et met à jour le niveau le plus élevé si celui-ci le
     * dépasse (rejouer un niveau supérieur le même jour doit compter pour les trophées, sans pour
     * autant déverrouiller le niveau déjà réussi). Ne fait rien si ce niveau est déjà dans
     * l'ensemble (déjà verrouillé, ne devrait pas pouvoir être rejoué).
     */
    suspend fun enregistrerReussite(profilId: Long, jour: String, niveau: String) {
        val existante = dao.reussiteDuJour(profilId, jour)
        if (existante == null) {
            dao.enregistrerReussite(
                DefiQuotidienEntity(profilId = profilId, jour = jour, dateReussite = System.currentTimeMillis(), niveau = niveau, niveauxReussis = niveau),
            )
            return
        }
        val (niveauLePlusHaut, ensembleNiveaux) = apresReussiteDefiQuotidien(existante.niveau, existante.niveauxReussis, niveau) ?: return
        dao.remplacerReussite(existante.copy(niveau = niveauLePlusHaut, niveauxReussis = ensembleNiveaux))
    }

    suspend fun reinitialiserJoueur(profilId: Long) = dao.reinitialiserJoueur(profilId)

    /** Toutes les réussites brutes d'un joueur — "Exporter mes statistiques". */
    suspend fun exporterReussites(profilId: Long): List<DefiQuotidienEntity> = dao.reussitesDuJoueur(profilId)

    /** Réinsère des réussites pour un profil cible — "Importer mes statistiques" (id d'origine ignoré). */
    suspend fun importerReussites(profilId: Long, reussites: List<DefiQuotidienEntity>) {
        for (reussite in reussites) {
            dao.enregistrerReussite(reussite.copy(profilId = profilId))
        }
    }

    suspend fun meilleureSerieJours(profilId: Long): Int = plusLongueSerieDeJours(joursTries(profilId))

    suspend fun serieActuelle(profilId: Long, aujourdHui: LocalDate = LocalDate.now()): Int =
        serieEnCoursDeJours(joursTries(profilId).toSet(), aujourdHui)

    private suspend fun joursTries(profilId: Long): List<LocalDate> =
        dao.joursReussis(profilId).mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
}
