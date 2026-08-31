package fr.pierre.chiffreslettres.data

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class LigneClassement(val profilId: Long, val pseudo: String, val avatar: String, val score: Int, val date: Long)

/** Une partie solo (score final + date), pour le classement personnel d'un joueur par niveau. */
data class MeilleurePartieSolo(val score: Int, val date: Long)

/** Une session avec ses manches, pour l'export/import complet de l'historique d'un joueur. */
data class SessionAvecManches(
    @Embedded val session: SessionEntity,
    @Relation(parentColumn = "id", entityColumn = "sessionId")
    val manches: List<MancheEntity>,
)

@Dao
interface HistoriqueDao {
    @Insert
    suspend fun insererSession(session: SessionEntity): Long

    @Insert
    suspend fun insererManches(manches: List<MancheEntity>)

    @Transaction
    suspend fun enregistrerPartie(session: SessionEntity, manches: List<MancheEntity>) {
        val sessionId = insererSession(session)
        insererManches(manches.map { it.copy(sessionId = sessionId) })
    }

    /**
     * Podium (top 3, retour utilisateur) des scores de partie (pas de regroupement par profil :
     * un même joueur peut apparaître plusieurs fois) pour un niveau et un type de partie donnés
     * (spec §7.2 ; [type] = nom d'un [TypePartie] : STRUCTUREE pour le classement solo, DUO ou
     * DUO_CONFRONTATION pour les classements duel, chacun séparé — retour utilisateur), tous
     * confondus chiffres/lettres (les deux modes partagent désormais les mêmes noms de niveau).
     * Le score affiché est le score final de la partie (`SessionEntity.scoreTotal`, somme des
     * manches), pas celui d'une manche individuelle (retour utilisateur) — un seul niveau
     * s'applique à toutes les manches d'une partie, d'où le `JOIN` sur `MancheEntity` pour
     * filtrer par niveau malgré le regroupement par session. À score égal, la partie la plus
     * récente passe devant.
     */
    @Query(
        """
        SELECT p.id AS profilId, p.pseudo AS pseudo, p.avatar AS avatar, s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN ProfilEntity p ON p.id = s.profilId
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 3
        """,
    )
    fun classementParNiveau(niveauCode: String, type: String): Flow<List<LigneClassement>>

    /** Top 3 des meilleures parties (score final de la partie) d'un joueur pour un niveau et un type de partie donnés. */
    @Query(
        """
        SELECT s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.scoreTotal DESC, s.date DESC
        LIMIT 3
        """,
    )
    fun meilleuresPartiesSoloParNiveau(profilId: Long, niveauCode: String, type: String): Flow<List<MeilleurePartieSolo>>

    /**
     * Historique chronologique (toutes les parties, pas seulement le podium) des scores d'un
     * joueur pour un niveau et un type de partie donnés — graphique de progression sur l'onglet
     * "Mes statistiques" (solo uniquement pour l'instant).
     */
    @Query(
        """
        SELECT s.scoreTotal AS score, s.date AS date
        FROM SessionEntity s
        INNER JOIN MancheEntity m ON m.sessionId = s.id
        WHERE s.profilId = :profilId AND s.type = :type AND m.niveauCode = :niveauCode
        GROUP BY s.id
        ORDER BY s.date ASC
        """,
    )
    fun historiqueScoresParNiveau(profilId: Long, niveauCode: String, type: String): Flow<List<MeilleurePartieSolo>>

    /**
     * Vide l'historique (sessions + manches, cascade) d'un seul joueur — bouton
     * "Réinitialiser mes statistiques" sur l'onglet Joueurs (ne touche pas les autres profils).
     */
    @Query("DELETE FROM SessionEntity WHERE profilId = :profilId")
    suspend fun reinitialiserHistoriqueJoueur(profilId: Long)

    /** Tout l'historique (sessions + manches) d'un joueur, pour "Exporter mes statistiques". */
    @Transaction
    @Query("SELECT * FROM SessionEntity WHERE profilId = :profilId")
    suspend fun sessionsAvecManchesDuJoueur(profilId: Long): List<SessionAvecManches>

    // --- Agrégats pour l'évaluation des trophées (parties solo/duo/confrontation, jamais
    // l'entraînement libre — retour utilisateur : les trophées "généraux" comptent aussi les
    // parties duo et confrontation, pas seulement le solo). En confrontation, le score d'une
    // manche perdue est écrasé à 0 mais motJoue et les manches à écart 0 (compte exact) ne sont
    // jamais affectés (une proposition exacte gagne toujours ou fait égalité, jamais perdante).

    /** Nombre de manches chiffres à compte exact (score 10). */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.mode = 'CHIFFRES' AND m.score = 10
        """,
    )
    suspend fun compterComptesExacts(profilId: Long): Int

    /** Nombre de manches lettres dont le mot joué a exactement [longueur] lettres. */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.mode = 'LETTRES' AND LENGTH(m.motJoue) = :longueur
        """,
    )
    suspend fun compterMotsLongueur(profilId: Long, longueur: Int): Int

    /**
     * Nombre de parties où toutes les manches chiffres ont un compte exact (score 10).
     * `COUNT(*) = SUM(...)` exige qu'aucune manche chiffres de la partie n'ait un score différent.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'CHIFFRES'
            WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            GROUP BY s.id
            HAVING COUNT(*) = SUM(CASE WHEN m.score = 10 THEN 1 ELSE 0 END)
        )
        """,
    )
    suspend fun compterPartiesTousComptesExacts(profilId: Long): Int

    /**
     * Nombre de parties où toutes les manches lettres ont un mot valide d'au moins
     * [longueurMin] lettres. `COUNT(*) = COUNT(m.motJoue)` exclut toute manche invalide/vide
     * (mot null) de la partie, condition nécessaire avant de vérifier la longueur minimale.
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'LETTRES'
            WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            GROUP BY s.id
            HAVING COUNT(*) = COUNT(m.motJoue) AND MIN(LENGTH(m.motJoue)) >= :longueurMin
        )
        """,
    )
    suspend fun compterPartiesMotsMin(profilId: Long, longueurMin: Int): Int

    /**
     * Comme [compterPartiesMotsMin], mais restreint aux parties jouées au niveau [niveauCode]
     * (retour utilisateur : trophées "partie parfaite" 7/8 lettres, exigeant le niveau Mathieu).
     */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT s.id
            FROM SessionEntity s
            INNER JOIN MancheEntity m ON m.sessionId = s.id AND m.mode = 'LETTRES' AND m.niveauCode = :niveauCode
            WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            GROUP BY s.id
            HAVING COUNT(*) = COUNT(m.motJoue) AND MIN(LENGTH(m.motJoue)) >= :longueurMin
        )
        """,
    )
    suspend fun compterPartiesMotsMinNiveau(profilId: Long, longueurMin: Int, niveauCode: String): Int

    /** Nombre de parties dont le score total atteint au moins [seuil]. */
    @Query(
        """
        SELECT COUNT(*) FROM SessionEntity
        WHERE profilId = :profilId AND type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU') AND scoreTotal >= :seuil
        """,
    )
    suspend fun compterPartiesScoreAuMoins(profilId: Long, seuil: Int): Int

    /** Nombre total de parties terminées, tous niveaux et tous types confondus (solo, duo, confrontation). */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')")
    suspend fun compterPartiesSoloTotal(profilId: Long): Int

    /** Nombre de parties d'un [type] (DUO ou DUO_CONFRONTATION) jouées par ce profil, tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = :type")
    suspend fun compterPartiesParType(profilId: Long, type: String): Int

    /** Nombre de parties d'un [type] gagnées par ce profil (`victoireDuel = 1`), tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type = :type AND victoireDuel = 1")
    suspend fun compterPartiesGagneesParType(profilId: Long, type: String): Int

    /**
     * Nombre de parties dont le type figure dans [types] jouées par ce profil, tous niveaux
     * confondus — pour les trophées, où le duo même téléphone et le duo à distance comptent
     * ensemble (retour utilisateur : trophées "Duo"/"Confrontation" fusionnés).
     */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type IN (:types)")
    suspend fun compterPartiesParTypes(profilId: Long, types: List<String>): Int

    /** Nombre de parties dont le type figure dans [types] gagnées par ce profil (`victoireDuel = 1`), tous niveaux confondus. */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND type IN (:types) AND victoireDuel = 1")
    suspend fun compterPartiesGagneesParTypes(profilId: Long, types: List<String>): Int

    // --- Agrégats pour les easter eggs (refonte 2026-08) ---

    /** Nombre de niveaux de difficulté distincts déjà joués (trophée "Multi-niveaux"). */
    @Query(
        """
        SELECT COUNT(DISTINCT m.niveauCode)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
        """,
    )
    suspend fun compterNiveauxDistinctsJoues(profilId: Long): Int

    data class DateEtScore(val date: Long, val score: Int)

    /**
     * Date et score de chaque partie, triés du plus ancien au plus récent — base commune pour
     * plusieurs easter eggs calculés en mémoire (Marathon, Ça ne s'arrête jamais, Constance,
     * Bonjour !/Oiseau de nuit), trop spécifiques pour mériter chacun leur propre requête SQL.
     */
    @Query(
        """
        SELECT date, scoreTotal AS score
        FROM SessionEntity
        WHERE profilId = :profilId AND type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
        ORDER BY date ASC
        """,
    )
    suspend fun datesEtScoresParties(profilId: Long): List<DateEtScore>

    /**
     * Tous les mots valides joués en mode Lettres — base commune pour les easter eggs
     * "Mot rare"/"Palindrome"/"Symétrique"/"Alphabet complet", calculés en mémoire (pas
     * pratique à exprimer en SQL).
     */
    @Query(
        """
        SELECT m.motJoue
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.mode = 'LETTRES' AND m.motJoue IS NOT NULL
        """,
    )
    suspend fun motsJoues(profilId: Long): List<String>

    /** Un mot invalide d'au moins 10 lettres a-t-il déjà été proposé (easter egg "Le mot le plus long jamais tenté") ? */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.longueurMotInvalide >= 10
        """,
    )
    suspend fun compterMotsInvalidesDixLettresOuPlus(profilId: Long): Int

    /** Une partie duo/confrontation terminée exactement à égalité avec l'adversaire (easter egg "Ex-aequo") ? */
    @Query("SELECT COUNT(*) FROM SessionEntity WHERE profilId = :profilId AND egaliteDuel = 1")
    suspend fun compterEgalitesDuel(profilId: Long): Int

    /** Un même score obtenu au moins deux fois en partie solo (easter egg "Symétrie") ? */
    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT scoreTotal FROM SessionEntity WHERE profilId = :profilId AND type = 'STRUCTUREE'
            GROUP BY scoreTotal HAVING COUNT(*) >= 2
        )
        """,
    )
    suspend fun compterScoresSoloRepetes(profilId: Long): Int

    /** Détail des manches chiffres à compte exact (easter eggs "Nombre premier"/"Calcul mental"/"Chemin minimal"/"Chirurgical"/"Speedrun"/"Va-tout") — calculés en mémoire, trop spécifiques pour 6 requêtes séparées. */
    data class DetailCompteExact(
        val cible: Int?,
        val nombreOperations: Int?,
        val maxEtapeIntermediaire: Int?,
        val dureeSecondesManche: Int?,
        val tempsRestantSecondesValidation: Int?,
        val niveauCode: String,
        /** Masque des opérations utilisées (bit = `Operation.ordinal`), pour l'easter egg "Boîte à outils". */
        val operateursUtilises: Int?,
    )

    @Query(
        """
        SELECT m.cibleChiffres AS cible, m.nombreOperationsChiffres AS nombreOperations,
            m.maxEtapeIntermediaireChiffres AS maxEtapeIntermediaire,
            m.dureeSecondesManche AS dureeSecondesManche,
            m.tempsRestantSecondesValidation AS tempsRestantSecondesValidation,
            m.niveauCode AS niveauCode,
            m.operateursUtilisesChiffres AS operateursUtilises
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.mode = 'CHIFFRES' AND m.score = 10
        """,
    )
    suspend fun comptesExactsChiffresDetail(profilId: Long): List<DetailCompteExact>

    /** Une manche chiffres a-t-elle déjà été proposée avec un écart d'au moins 200 à la cible (easter egg "À côté de la plaque") ? */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.mode = 'CHIFFRES' AND m.ecartCibleChiffres >= 200
        """,
    )
    suspend fun compterEcartEnormeChiffres(profilId: Long): Int

    /** Une manche terminée par expiration du chrono sans aucune proposition (easter egg "Aucune idée"). */
    @Query(
        """
        SELECT COUNT(*)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId AND s.type IN ('STRUCTUREE', 'DUO', 'DUO_CONFRONTATION', 'DUO_RESEAU', 'DUO_CONFRONTATION_RESEAU')
            AND m.score = 0
            AND (
                (m.mode = 'CHIFFRES' AND m.nombreOperationsChiffres = 0)
                OR (m.mode = 'LETTRES' AND m.motJoue IS NULL AND m.longueurMotInvalide IS NULL)
            )
        """,
    )
    suspend fun compterManchesSansRienPropose(profilId: Long): Int

    /**
     * Temps de jeu cumulé en parties/entraînement/duels, en secondes (easter egg "100 heures de
     * jeu") — tous types de partie confondus, y compris entraînement libre et duels de mots
     * réseau (retour utilisateur 2026-08-31 : le trophée doit refléter tout le temps de jeu, pas
     * seulement les parties solo/duo/confrontation). Les défis (table `DefiEntity`, aucune
     * `MancheEntity` associée) sont sommés séparément par [DefiDao.sommeSecondesDefis].
     */
    @Query(
        """
        SELECT COALESCE(SUM(m.dureeSecondesManche), 0)
        FROM MancheEntity m
        INNER JOIN SessionEntity s ON s.id = m.sessionId
        WHERE s.profilId = :profilId
        """,
    )
    suspend fun sommeSecondesJouees(profilId: Long): Int

    // --- Duel points (refonte 2026-08) ---

    /** Plus grand écart de points en victoire, en Duel points (easter egg "Rouleau compresseur"). */
    @Query(
        """
        SELECT COALESCE(MAX(ecartDuel), 0) FROM SessionEntity
        WHERE profilId = :profilId AND type = 'DUEL_MOTS_POINTS_RESEAU' AND victoireDuel = 1
        """,
    )
    suspend fun maxEcartVictoireDuelPoints(profilId: Long): Int

    /** Plus grand écart de points en défaite, en Duel points (easter egg "Déculottée"). */
    @Query(
        """
        SELECT COALESCE(MAX(-ecartDuel), 0) FROM SessionEntity
        WHERE profilId = :profilId AND type = 'DUEL_MOTS_POINTS_RESEAU' AND victoireDuel = 0
        """,
    )
    suspend fun maxEcartDefaiteDuelPoints(profilId: Long): Int

    /** Une victoire en Duel points a-t-elle déjà été obtenue avec l'option "atteindre exactement l'objectif" (easter egg "Compte rond") ? */
    @Query(
        """
        SELECT COUNT(*) FROM SessionEntity
        WHERE profilId = :profilId AND type = 'DUEL_MOTS_POINTS_RESEAU' AND objectifExactAtteint = 1
        """,
    )
    suspend fun compterCompteRondDuelPoints(profilId: Long): Int
}
