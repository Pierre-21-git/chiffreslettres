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

/**
 * Suivi du défi quotidien (retour utilisateur) : réussite du jour (verrouillage du rejeu une
 * fois atteinte) et séries de jours consécutifs — la meilleure série (pour les trophées, comme
 * toutes les autres stats de trophées) et la série en cours (affichage seulement).
 */
class DefiQuotidienRepository(private val dao: DefiQuotidienDao) {

    suspend fun reussiteDuJour(profilId: Long, jour: String): Boolean = dao.reussiteDuJour(profilId, jour) != null

    suspend fun enregistrerReussite(profilId: Long, jour: String, niveau: String) {
        dao.enregistrerReussite(
            DefiQuotidienEntity(profilId = profilId, jour = jour, dateReussite = System.currentTimeMillis(), niveau = niveau),
        )
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
