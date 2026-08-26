package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

data class ResultatManche(
    val mode: ModeJeu,
    val niveauCode: String,
    val score: Int,
    val motJoue: String? = null,
    /** Longueur du mot soumis quand il était invalide (mode Lettres, parties duo/confrontation uniquement), pour le bonus de score de l'adversaire. */
    val longueurMotInvalide: Int? = null,
    val cibleChiffres: Int? = null,
    val nombreOperationsChiffres: Int? = null,
    val maxEtapeIntermediaireChiffres: Int? = null,
    val dureeSecondesManche: Int? = null,
    val tempsRestantSecondesValidation: Int? = null,
)

class HistoriqueRepository(private val dao: HistoriqueDao) {

    suspend fun enregistrerSession(
        profilId: Long,
        type: TypePartie,
        manches: List<ResultatManche>,
        victoireDuel: Boolean? = null,
        egaliteDuel: Boolean? = null,
    ) {
        val session = SessionEntity(
            profilId = profilId,
            date = System.currentTimeMillis(),
            type = type,
            scoreTotal = manches.sumOf { it.score },
            victoireDuel = victoireDuel,
            egaliteDuel = egaliteDuel,
        )
        val entites = manches.mapIndexed { index, resultat ->
            MancheEntity(
                sessionId = 0, // remplacé par enregistrerPartie() une fois la session insérée
                ordre = index,
                mode = resultat.mode,
                niveauCode = resultat.niveauCode,
                score = resultat.score,
                motJoue = resultat.motJoue,
                longueurMotInvalide = resultat.longueurMotInvalide,
                cibleChiffres = resultat.cibleChiffres,
                nombreOperationsChiffres = resultat.nombreOperationsChiffres,
                maxEtapeIntermediaireChiffres = resultat.maxEtapeIntermediaireChiffres,
                dureeSecondesManche = resultat.dureeSecondesManche,
                tempsRestantSecondesValidation = resultat.tempsRestantSecondesValidation,
            )
        }
        dao.enregistrerPartie(session, entites)
    }

    fun classementParNiveau(niveauCode: String, type: TypePartie): Flow<List<LigneClassement>> =
        dao.classementParNiveau(niveauCode, type.name)

    fun meilleuresPartiesSoloParNiveau(profilId: Long, niveauCode: String, type: TypePartie): Flow<List<MeilleurePartieSolo>> =
        dao.meilleuresPartiesSoloParNiveau(profilId, niveauCode, type.name)

    fun historiqueScoresParNiveau(profilId: Long, niveauCode: String, type: TypePartie): Flow<List<MeilleurePartieSolo>> =
        dao.historiqueScoresParNiveau(profilId, niveauCode, type.name)

    suspend fun compterPartiesParType(profilId: Long, type: TypePartie): Int = dao.compterPartiesParType(profilId, type.name)

    suspend fun compterPartiesGagneesParType(profilId: Long, type: TypePartie): Int =
        dao.compterPartiesGagneesParType(profilId, type.name)

    suspend fun reinitialiserHistoriqueJoueur(profilId: Long) = dao.reinitialiserHistoriqueJoueur(profilId)

    /** Tout l'historique brut d'un joueur — "Exporter mes statistiques". */
    suspend fun exporterSessions(profilId: Long): List<SessionAvecManches> = dao.sessionsAvecManchesDuJoueur(profilId)

    /**
     * Réinsère des sessions (avec leurs manches) pour un profil cible — "Importer mes
     * statistiques". Les id d'origine (session et manches) sont ignorés : de nouveaux id sont
     * générés, comme pour toute nouvelle partie enregistrée.
     */
    suspend fun importerSessions(profilId: Long, sessions: List<SessionAvecManches>) {
        for (entree in sessions) {
            dao.enregistrerPartie(
                entree.session.copy(id = 0, profilId = profilId),
                entree.manches.map { it.copy(id = 0, sessionId = 0) },
            )
        }
    }
}
