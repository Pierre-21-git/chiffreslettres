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

/**
 * Suivi du défi quotidien (retour utilisateur) : réussite du jour (le niveau le plus élevé
 * réussi est conservé, pas seulement le premier — voir [enregistrerReussite]) et séries de
 * jours consécutifs — la meilleure série (pour les trophées, comme toutes les autres stats de
 * trophées) et la série en cours (affichage seulement).
 */
class DefiQuotidienRepository(private val dao: DefiQuotidienDao) {

    suspend fun reussiteDuJour(profilId: Long, jour: String): Boolean = dao.reussiteDuJour(profilId, jour) != null

    /** Niveau (nom d'enum) déjà réussi aujourd'hui, ou null si aucune réussite ce jour (retour utilisateur : les boutons de niveau restent affichés, seul celui-ci doit être désactivé). */
    suspend fun niveauReussiAujourdhui(profilId: Long, jour: String): String? = dao.reussiteDuJour(profilId, jour)?.niveau

    /**
     * Enregistre une réussite du défi quotidien. Si aucune réussite n'existe encore pour ce jour,
     * l'insère. Si une réussite existe déjà avec un niveau inférieur, la remplace par ce niveau
     * plus élevé (retour utilisateur : rejouer un niveau supérieur le même jour doit compter,
     * pas rester bloqué sur le premier niveau réussi) ; si le niveau existant est déjà égal ou
     * supérieur, ne fait rien.
     */
    suspend fun enregistrerReussite(profilId: Long, jour: String, niveau: String) {
        val existante = dao.reussiteDuJour(profilId, jour)
        val entity = DefiQuotidienEntity(profilId = profilId, jour = jour, dateReussite = System.currentTimeMillis(), niveau = niveau)
        if (existante == null) {
            dao.enregistrerReussite(entity)
        } else if (rangNiveau(niveau) > rangNiveau(existante.niveau)) {
            dao.remplacerReussite(entity)
        }
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
