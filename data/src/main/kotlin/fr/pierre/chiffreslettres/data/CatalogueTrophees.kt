package fr.pierre.chiffreslettres.data

/**
 * Statistiques agrégées d'un joueur (parties (solo, duo ou confrontation) + défis, jamais l'entraînement libre) : la
 * base sur laquelle chaque trophée du catalogue évalue sa condition de déblocage. Un seul aller-
 * retour vers la base (voir `TropheeRepository`) plutôt qu'une requête par trophée.
 */
data class TropheeStats(
    val comptesExacts: Int,
    /** Clé = longueur exacte du mot (4 à 10), valeur = nombre de fois où un mot de cette longueur a été trouvé, en partie (solo, duo ou confrontation). */
    val motsParLongueur: Map<Int, Int>,
    val partieTousComptesExacts: Boolean,
    /** Clé = longueur minimale (4 à 8), valeur = une partie (solo, duo ou confrontation) a-t-elle uniquement des mots d'au moins cette longueur. */
    val partiesMotsMin: Map<Int, Boolean>,
    /**
     * Comme [partiesMotsMin] (retour utilisateur), mais restreint aux parties jouées au niveau
     * Mathieu (aucune lettre exclue) — clés 7 et 8 uniquement, seuils concernés par cette exigence.
     */
    val partiesMotsMinNiveauMathieu: Map<Int, Boolean>,
    /** Clé = seuil de points (20 à 90), valeur = nombre de parties (solo, duo ou confrontation) atteignant au moins ce seuil. */
    val partiesParSeuilScore: Map<Int, Int>,
    val partiesSoloTotal: Int,
    /**
     * Parties Duo, même téléphone ou à distance confondus (retour utilisateur : les deux comptent
     * pour les mêmes trophées). Comptées séparément des parties Confrontation ici, mais les
     * trophées "duo_*" additionnent Duo, Confrontation, Duel mots et Duel points confondus (retour
     * utilisateur 2026-08-30 : une seule progression fusionnée pour ces 4 modes — voir
     * [partiesJoueesDuel]).
     */
    val partiesDuoJouees: Int,
    val partiesDuoGagnees: Int,
    /** Parties Confrontation, même téléphone ou à distance confondus. Voir [partiesDuoJouees]. */
    val partiesConfrontationJouees: Int,
    val partiesConfrontationGagnees: Int,
    /** Parties Duel mots, sous-mode Duo (100 % réseau, pas de variante même téléphone). Voir [partiesDuoJouees]. */
    val partiesDuelMotsJouees: Int,
    val partiesDuelMotsGagnees: Int,
    /** Parties Duel mots, sous-mode Confrontation (100 % réseau). Voir [partiesDuoJouees]. */
    val partiesDuelMotsConfrontationJouees: Int,
    val partiesDuelMotsConfrontationGagnees: Int,
    /** Parties Duel points (100 % réseau, premier à atteindre un total de points). Voir [partiesDuoJouees]. */
    val partiesDuelPointsJouees: Int,
    val partiesDuelPointsGagnees: Int,
    /** Plus grand écart de points en victoire, en Duel points (easter egg "Rouleau compresseur"). */
    val duelPointsEcartVictoireMax: Int,
    /** Plus grand écart de points en défaite, en Duel points (easter egg "Déculottée"). */
    val duelPointsEcartDefaiteMax: Int,
    /** Une victoire en Duel points a-t-elle déjà été obtenue avec l'option "atteindre exactement l'objectif" (easter egg "Compte rond"). */
    val duelPointsCompteRondObtenu: Boolean,
    /** Clé = nom du [ModeJeu] (ex. "CHIFFRES"), valeur = meilleure série en défi série, tous niveaux confondus. */
    val meilleuresSeriesDefi: Map<String, Int>,
    /** Comme [meilleuresSeriesDefi], restreint aux défis joués au niveau Monique ou Mathieu. */
    val meilleuresSeriesDefiNiveauMonique: Map<String, Int>,
    /** Comme [meilleuresSeriesDefi], restreint aux défis joués au niveau Mathieu. */
    val meilleuresSeriesDefiNiveauMathieu: Map<String, Int>,
    /** Clé = nom du [ModeJeu] (ex. "CHIFFRES"), valeur = meilleur nombre de réussites en défi chrono, tous niveaux confondus. */
    val meilleuresReussitesDefiChrono: Map<String, Int>,
    /** Comme [meilleuresReussitesDefiChrono], restreint aux défis joués au niveau Monique ou Mathieu. */
    val meilleuresReussitesDefiChronoNiveauMonique: Map<String, Int>,
    /** Comme [meilleuresReussitesDefiChrono], restreint aux défis joués au niveau Mathieu. */
    val meilleuresReussitesDefiChronoNiveauMathieu: Map<String, Int>,
    /** Meilleur nombre de mots distincts trouvés en défi mots max (lettres uniquement), tous niveaux confondus. */
    val meilleurScoreDefiMotsMax: Int,
    /** Comme [meilleurScoreDefiMotsMax], restreint au niveau Monique ou Mathieu. */
    val meilleurScoreDefiMotsMaxNiveauMonique: Int,
    /** Comme [meilleurScoreDefiMotsMax], restreint au niveau Mathieu. */
    val meilleurScoreDefiMotsMaxNiveauMathieu: Int,
    /** Meilleur nombre d'objectifs de points atteints en une partie de défi Points, tous niveaux confondus. */
    val meilleurScoreDefiObjectifsPoints: Int,
    /** Meilleure série en défi sans faute (mixte chiffres+lettres), tous niveaux confondus. */
    val meilleureSerieSansFaute: Int,
    /** Comme [meilleureSerieSansFaute], restreint au niveau Monique ou Mathieu. */
    val meilleureSerieSansFauteNiveauMonique: Int,
    /** Comme [meilleureSerieSansFaute], restreint au niveau Mathieu. */
    val meilleureSerieSansFauteNiveauMathieu: Int,
    /** Plus longue série de jours consécutifs avec le défi quotidien réussi. */
    val meilleureSerieJoursDefiQuotidien: Int,
    /** Comme [meilleureSerieJoursDefiQuotidien], restreint aux jours joués au niveau Monique ou Mathieu. */
    val meilleureSerieJoursDefiQuotidienNiveauMonique: Int,
    /** Comme [meilleureSerieJoursDefiQuotidien], restreint aux jours joués au niveau Mathieu. */
    val meilleureSerieJoursDefiQuotidienNiveauMathieu: Int,
    /**
     * Série de jours consécutifs en cours (pas la meilleure série historique) — sert uniquement à
     * l'affichage de la progression d'un trophée non débloqué (retour utilisateur : afficher "2/30"
     * après 2 jours d'affilée, pas rester bloqué sur un ancien record dépassé par une interruption).
     * Le déblocage du trophée continue lui de se baser sur [meilleureSerieJoursDefiQuotidien].
     */
    val serieEnCoursJoursDefiQuotidien: Int,
    /** Comme [serieEnCoursJoursDefiQuotidien], restreint aux jours joués au niveau Monique ou Mathieu. */
    val serieEnCoursJoursDefiQuotidienNiveauMonique: Int,
    /** Comme [serieEnCoursJoursDefiQuotidien], restreint aux jours joués au niveau Mathieu. */
    val serieEnCoursJoursDefiQuotidienNiveauMathieu: Int,

    // --- Easter eggs (refonte 2026-08) ---
    /** Parties solo (`TypePartie.STRUCTUREE`) exclusivement — pour distinguer "solo" des autres types dans le trophée "Touche-à-tout" (les autres champs `partiesXxxJouees` regroupent parfois plusieurs types). */
    val partiesSoloStructureeJouees: Int,
    val defisJouesTotal: Int,
    /** Ancienneté du profil (retour mainteneur : trophée "Ancien combattant"). */
    val ancienneteJoursProfil: Long,
    /** Nombre de niveaux de difficulté distincts déjà joués (trophée "Multi-niveaux"). */
    val nombreNiveauxDistinctsJoues: Int,
    /** Nombre maximum de parties jouées le même jour (trophée "Marathon"). */
    val maxPartiesMemeJour: Int,
    /** Au moins 5 parties jouées en moins d'une heure d'affilée (trophée "Ça ne s'arrête jamais"). */
    val cinqPartiesEnUneHeure: Boolean,
    /** Écart de moins de 10 points entre la meilleure et la moins bonne des 10 dernières parties (trophée "Constance"). */
    val ecartDixDernieresPartiesFaible: Boolean,
    /** Au moins une partie jouée entre 5h et 7h (trophée "Bonjour !"). */
    val partieJoueeEntre5et7h: Boolean,
    /** Au moins une partie jouée entre minuit et 5h (trophée "Oiseau de nuit"). */
    val unePartieEntreMinuitEt5h: Boolean,
    /** Au moins un mot d'au moins 8 lettres contenant Q/X/W/Y/Z déjà joué (trophée "Mot rare"). */
    val motRareJoue: Boolean,
    /** Au moins un mot palindrome déjà joué (trophée "Palindrome"). */
    val palindromeJoue: Boolean,
    /** Au moins un mot dont les lettres sont en ordre alphabétique déjà joué (trophée "Symétrique"). */
    val motSymetriqueJoue: Boolean,
    /** Nombre de lettres distinctes de l'alphabet (A-Z, 26 max) utilisées, cumulé sur tous les mots joués (trophée "Alphabet complet"). */
    val nombreLettresAlphabetUtilisees: Int,
    /** Plus longue série de dimanches consécutifs (avec au moins une partie) jamais réalisée — sert au déblocage (trophée "Rituel du dimanche"). */
    val meilleureSerieDimanchesConsecutifs: Int,
    /** Série de dimanches consécutifs en cours, en remontant depuis aujourd'hui — sert à l'affichage de la progression du trophée "Rituel du dimanche". */
    val serieEnCoursDimanches: Int,
    /** La page des règles du jeu a déjà été consultée (trophée "Curieux"). */
    val reglesDejaVues: Boolean,
    /** Nombre de clics sur "Statistiques" au menu principal (trophée "Data-lover"). */
    val nombreVisitesStats: Int,
    /** Un mot invalide d'au moins 10 lettres a déjà été proposé (trophée "Le mot le plus long jamais tenté"). */
    val motInvalideDixLettresTente: Boolean,
    /** Une partie duo/confrontation terminée exactement à égalité avec l'adversaire (trophée "Ex-aequo"). */
    val egaliteDuelDejaObtenue: Boolean,
    /** Un même score obtenu au moins deux fois en partie solo (trophée "Symétrie"). */
    val scoreSoloRepete: Boolean,
    // --- Easter eggs "Chiffres" (refonte 2026-08) ---
    /** Un compte exact dont la cible est un nombre premier (trophée "Nombre premier"). */
    val compteExactCibleNombrePremier: Boolean,
    /** Un compte exact sans étape intermédiaire à 3 chiffres ou plus (trophée "Calcul mental"). */
    val compteExactCalculMental: Boolean,
    /** Un compte exact en une seule opération (trophée "Chemin minimal"). */
    val compteExactCheminMinimal: Boolean,
    /** Un compte exact en utilisant les 6 nombres du tirage (trophée "Chirurgical"). */
    val compteExactChirurgical: Boolean,
    /** Un compte exact trouvé en moins de 5 secondes (trophée "Speedrun"). */
    val compteExactSpeedrun: Boolean,
    /** Un compte exact proposé à la toute dernière seconde du chrono (trophée "Va-tout"). */
    val compteExactVaTout: Boolean,
    /** Une proposition chiffres avec un écart d'au moins 200 à la cible (trophée "À côté de la plaque"). */
    val ecartEnormeChiffres: Boolean,
    /** Un compte exact en utilisant les 4 opérations dans le même calcul (trophée "Boîte à outils"). */
    val compteExactBoiteAOutils: Boolean,
    /** Une manche terminée sans aucune proposition (trophée "Aucune idée"). */
    val aucuneIdeeProposee: Boolean,
    /**
     * Temps de jeu cumulé, en secondes, toutes sources confondues : parties (solo/duo/
     * confrontation, local et réseau), entraînement libre, duels de mots réseau et défis
     * (série/chrono/mots max/sans faute/points/quotidien) — trophée "100 heures de jeu".
     */
    val secondesJoueesTotal: Int,
) {
    /**
     * Parties jouées combinées Duo + Confrontation + Duel mots (sous-modes Duo et Confrontation) +
     * Duel points, résultat indifférent (retour utilisateur 2026-09-03 : les trophées
     * "duo_jouee_*" comptent une partie jouée dans n'importe lequel de ces 4 modes comme une
     * seule et même progression, plutôt qu'une victoire — remplace l'ancien [victoiresDuel]).
     */
    val partiesJoueesDuel: Int
        get() = partiesDuoJouees + partiesConfrontationJouees +
            partiesDuelMotsJouees + partiesDuelMotsConfrontationJouees +
            partiesDuelPointsJouees
}

/**
 * Palier de difficulté d'un trophée (retour utilisateur), du plus facile au plus rare. Ordre de
 * déclaration significatif : `rangJoueur()` (ci-dessous) compare les paliers par ordinal, donc
 * toute insertion doit rester à la bonne place dans la progression. Émeraude/Saphir/Rubis
 * s'intercalent entre Platine et Diamant (refonte 2026-08, cf. `trophées_paliers2.xlsx`).
 * Les easter eggs (`Trophee.palier == null`) ne participent pas à cette échelle — ce sont des
 * curiosités, pas des jalons de progression (retour utilisateur).
 */
enum class Palier { BRONZE, ARGENT, OR, PLATINE, EMERAUDE, SAPHIR, RUBIS, DIAMANT }

/**
 * Libellé du rang joueur associé à un palier (retour utilisateur, ex. "Joueur Bronze") : ID de
 * ressource, pas le texte résolu (ce module n'a pas accès à Compose) — à résoudre côté UI avec
 * `stringResource`.
 */
val Palier.libelleJoueurRes: Int
    get() = when (this) {
        Palier.BRONZE -> R.string.palier_joueur_bronze
        Palier.ARGENT -> R.string.palier_joueur_argent
        Palier.OR -> R.string.palier_joueur_or
        Palier.PLATINE -> R.string.palier_joueur_platine
        Palier.EMERAUDE -> R.string.palier_joueur_emeraude
        Palier.SAPHIR -> R.string.palier_joueur_saphir
        Palier.RUBIS -> R.string.palier_joueur_rubis
        Palier.DIAMANT -> R.string.palier_joueur_diamant
    }

/** Libellé court de la difficulté d'un trophée (retour utilisateur, ex. "Bronze"), affiché dans son détail. */
val Palier.libelleCourtRes: Int
    get() = when (this) {
        Palier.BRONZE -> R.string.palier_court_bronze
        Palier.ARGENT -> R.string.palier_court_argent
        Palier.OR -> R.string.palier_court_or
        Palier.PLATINE -> R.string.palier_court_platine
        Palier.EMERAUDE -> R.string.palier_court_emeraude
        Palier.SAPHIR -> R.string.palier_court_saphir
        Palier.RUBIS -> R.string.palier_court_rubis
        Palier.DIAMANT -> R.string.palier_court_diamant
    }

enum class CategorieTrophee(val titreRes: Int) {
    PARTIES_TERMINEES(R.string.categorie_parties_terminees),
    SCORE_PARTIE(R.string.categorie_score_partie),
    COMPTES_EXACTS(R.string.categorie_comptes_exacts),
    PARTIE_PARFAITE(R.string.categorie_partie_parfaite),
    MOTS(R.string.categorie_mots),
    DUO(R.string.categorie_duo),
    DEFI(R.string.categorie_defi),
    DEFI_CHRONO(R.string.categorie_defi_chrono),
    DEFI_MOTS_MAX(R.string.categorie_defi_mots_max),
    DEFI_OBJECTIFS_POINTS(R.string.categorie_defi_points),
    DEFI_SANS_FAUTE(R.string.categorie_defi_sans_faute),
    DEFI_QUOTIDIEN(R.string.categorie_defi_quotidien),
    TROPHEES_SPECIAUX(R.string.categorie_trophees_speciaux),
    EASTER_CHIFFRES(R.string.categorie_easter_chiffres),
    EASTER_LETTRES(R.string.categorie_easter_lettres),
    EASTER_GENERAL(R.string.categorie_easter_general),
    /**
     * Regroupe tous les trophées [NiveauVisibilite.INVISIBLE] ("???????" tant que non débloqués),
     * quel que soit leur thème d'origine (retour utilisateur 2026-08-30) : leur vraie catégorie
     * est celle-ci en permanence, donc leur position ne change jamais entre verrouillé et
     * débloqué — remplace l'ancienne catégorie EASTER_ULTIME, qui n'avait qu'un seul trophée.
     */
    EASTER_SECRETS(R.string.categorie_easter_secrets),
}

/**
 * Argument de format (titreArgs/descriptionArgs de [Trophee]) qui est lui-même un ID de
 * ressource string à résoudre avant substitution (ex. nom de mode "chiffres"/"lettres") — ce
 * module n'a pas accès à Compose pour le résoudre lui-même, donc l'UI doit le faire (voir
 * `TropheesScreen.kt`) avant d'appeler `stringResource(titreRes, *args)`.
 */
data class ArgRes(val res: Int)

/**
 * Visibilité d'un trophée non débloqué dans le catalogue (refonte 2026-08, "easter eggs") :
 * [VISIBLE] par défaut (tuile + titre + description normale, comme aujourd'hui), [SEMI_CACHE]
 * (tuile visible, vrai titre, mais [Trophee.descriptionAvantDeblocageRes] à la place de la
 * vraie description), [INVISIBLE] (n'apparaît nulle part tant qu'il n'est pas débloqué — secret
 * complet). Une fois débloqué, un trophée s'affiche toujours normalement, quel que soit son
 * niveau de visibilité.
 */
enum class NiveauVisibilite { VISIBLE, SEMI_CACHE, INVISIBLE }

/**
 * Unité d'affichage de la progression "X / objectif" d'un trophée (retour utilisateur) :
 * [NOMBRE] (défaut) affiche les valeurs brutes, [DUREE] les formate en heures/minutes/secondes
 * (ex. trophée "100 heures de jeu", dont [Trophee.objectif]/[Trophee.progression] sont en
 * secondes).
 */
enum class UniteProgression { NOMBRE, DUREE }

class Trophee(
    val id: String,
    val titreRes: Int,
    val titreArgs: List<Any> = emptyList(),
    val descriptionRes: Int,
    val descriptionArgs: List<Any> = emptyList(),
    val categorie: CategorieTrophee,
    /** Null pour les easter eggs (retour utilisateur) : ce sont des curiosités à découvrir, pas des jalons de la progression Bronze→Diamant. */
    val palier: Palier?,
    /** Regroupement visuel au sein d'une catégorie (ex. niveau du défi chrono), null si non applicable. */
    val sousTitreRes: Int? = null,
    /**
     * Objectif numérique affiché "X / objectif" dans le détail du trophée (retour utilisateur),
     * null si le trophée n'a pas de progression chiffrée (ex. partie parfaite, tout ou rien).
     */
    val objectif: Int? = null,
    /** Valeur courante du joueur pour [objectif] (retour utilisateur), null si non applicable. */
    val progression: ((TropheeStats) -> Int)? = null,
    /** Unité d'affichage de [objectif]/[progression] (retour utilisateur, défaut nombre brut). */
    val uniteProgression: UniteProgression = UniteProgression.NOMBRE,
    val niveauVisibilite: NiveauVisibilite = NiveauVisibilite.VISIBLE,
    /** Description affichée tant que le trophée n'est pas débloqué, si [niveauVisibilite] != VISIBLE. */
    val descriptionAvantDeblocageRes: Int? = null,
    val estDebloque: (TropheeStats) -> Boolean,
)

/** Seuil du trophée "100 heures de jeu" (easter egg), en secondes. */
private const val SECONDES_CENT_HEURES = 100 * 3600

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
/** Seuils "partie parfaite" (lettres) qui exigent en plus le niveau Mathieu (retour utilisateur). */
private val SEUILS_MOTS_NIVEAU_MATHIEU = listOf(7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)

// Barème unifié (refonte 2026-08, cf. trophées_paliers2.xlsx) partagé par Mots max uniquement
// (Série/Sans-faute/Chrono sont passés à l'échelle courte ci-dessous le 2026-09-03) : 3/5/8 tous
// niveaux (Bronze/Argent/Or), 10 au niveau Monique+ (Platine), puis 12/15/20/25 au niveau Mathieu
// (Émeraude/Saphir/Rubis/Diamant).
private val SEUILS_DEFI_MOTS_MAX = listOf(3, 5, 8)
private const val SEUIL_DEFI_NIVEAU_MONIQUE = 10
private val SEUILS_DEFI_NIVEAU_MATHIEU = listOf(12, 15, 20, 25)
private val PALIERS_DEFI_NIVEAU_MATHIEU = mapOf(
    12 to Palier.EMERAUDE, 15 to Palier.SAPHIR, 20 to Palier.RUBIS, 25 to Palier.DIAMANT,
)

// Échelle courte (retour utilisateur 2026-09-03), partagée par Série/Sans-faute/Chrono : 3/5/8
// tous niveaux (Bronze/Argent/Or), 10 au niveau Monique+ (Émeraude), puis 12/15 au niveau Mathieu
// (Saphir/Rubis) — pas de Platine ni au-delà de Rubis.
private val SEUILS_DEFI_SERIE = listOf(3, 5, 8)
private val SEUILS_DEFI_SANS_FAUTE = listOf(3, 5, 8)
private val SEUILS_DEFI_CHRONO = listOf(3, 5, 8)
private const val SEUIL_DEFI_NIVEAU_MONIQUE_COURT = 10
private val SEUILS_DEFI_NIVEAU_MATHIEU_COURT = listOf(12, 15)
private val PALIERS_DEFI_NIVEAU_MATHIEU_COURT = mapOf(12 to Palier.SAPHIR, 15 to Palier.RUBIS)

/** Barème dédié au défi Points (retour utilisateur 2026-09-03) : 1/3/5 tous niveaux (Bronze/Argent/Or), puis 8/10/12/15 (Émeraude/Saphir/Rubis/Diamant), sans distinction de niveau. */
private val SEUILS_DEFI_POINTS = listOf(1, 3, 5, 8, 10, 12, 15)
private val PALIERS_DEFI_POINTS = mapOf(
    1 to Palier.BRONZE, 3 to Palier.ARGENT, 5 to Palier.OR,
    8 to Palier.EMERAUDE, 10 to Palier.SAPHIR, 12 to Palier.RUBIS, 15 to Palier.DIAMANT,
)

private val LONGUEURS_MOTS_TROPHEE = 4..10
// Défi quotidien : rythme hebdomadaire (refonte 2026-08, seuil Émeraude ajusté 2026-09-03) — 1/2/3
// semaines tous niveaux, 4 semaines au niveau Monique+, puis 35/42/56/70 jours au niveau Mathieu.
private val SEUILS_DEFI_QUOTIDIEN = listOf(7, 14, 21)
private const val SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE = 28
private val SEUILS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU = listOf(35, 42, 56, 70)
private val PALIERS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU = mapOf(
    35 to Palier.EMERAUDE, 42 to Palier.SAPHIR, 56 to Palier.RUBIS, 70 to Palier.DIAMANT,
)

// Paliers (refonte 2026-08, cf. trophées_paliers2.xlsx).
private val PALIERS_PARTIE_MOTS_MIN = mapOf(4 to Palier.ARGENT, 5 to Palier.OR, 6 to Palier.PLATINE, 7 to Palier.SAPHIR, 8 to Palier.RUBIS)
// 2026-09-03 : le 10ème mot de 10 lettres (Diamant) est supprimé, le 1er mot de 10 lettres
// passe de Rubis à Diamant (dernier palier de la catégorie Mots).
private val PALIERS_MOTS_1 = mapOf(
    4 to Palier.BRONZE, 5 to Palier.ARGENT, 6 to Palier.OR, 7 to Palier.PLATINE,
    8 to Palier.EMERAUDE, 9 to Palier.SAPHIR, 10 to Palier.DIAMANT,
)
private val PALIERS_MOTS_10 = mapOf(
    4 to Palier.ARGENT, 5 to Palier.OR, 6 to Palier.PLATINE, 7 to Palier.EMERAUDE,
    8 to Palier.SAPHIR, 9 to Palier.RUBIS,
)
// 2026-09-03 : la 10ème partie à au moins 90 points (Diamant) est supprimée, la 1ère partie à au
// moins 90 points passe de Rubis à Diamant (dernier palier de la catégorie Score de partie).
private val PALIERS_SCORE_1 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.BRONZE, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.PLATINE, 70 to Palier.EMERAUDE, 80 to Palier.SAPHIR, 90 to Palier.DIAMANT,
)
private val PALIERS_SCORE_10 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.OR, 50 to Palier.PLATINE,
    60 to Palier.EMERAUDE, 70 to Palier.SAPHIR, 80 to Palier.RUBIS,
)
// Barème (retour utilisateur) de série/chrono/sans-faute/mots max : 3=Bronze, 5=Argent, 8=Or.
private val PALIERS_DEFI_UNIFIE = mapOf(3 to Palier.BRONZE, 5 to Palier.ARGENT, 8 to Palier.OR)
private val PALIERS_DEFI_QUOTIDIEN = mapOf(7 to Palier.BRONZE, 14 to Palier.ARGENT, 21 to Palier.OR)

/** Catalogue complet des trophées possibles (spec produit, retour utilisateur) : 153 au total. */
object CatalogueTrophees {

    val TOUS: List<Trophee> = buildList {
        add(
            Trophee(
                "compte_exact_1",
                titreRes = R.string.trophee_titre_compte_exact_1,
                descriptionRes = R.string.trophee_desc_compte_exact_1,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.BRONZE,
                objectif = 1,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 1 },
        )
        add(
            Trophee(
                "compte_exact_10",
                titreRes = R.string.trophee_titre_compte_exact_10,
                descriptionRes = R.string.trophee_desc_compte_exact_10,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.ARGENT,
                objectif = 10,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 10 },
        )
        add(
            Trophee(
                "compte_exact_50",
                titreRes = R.string.trophee_titre_compte_exact_50,
                descriptionRes = R.string.trophee_desc_compte_exact_50,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.OR,
                objectif = 50,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 50 },
        )
        add(
            Trophee(
                "compte_exact_100",
                titreRes = R.string.trophee_titre_compte_exact_100,
                descriptionRes = R.string.trophee_desc_compte_exact_100,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.PLATINE,
                objectif = 100,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 100 },
        )
        add(
            Trophee(
                "compte_exact_200",
                titreRes = R.string.trophee_titre_compte_exact_200,
                descriptionRes = R.string.trophee_desc_compte_exact_200,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.EMERAUDE,
                objectif = 200,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 200 },
        )
        add(
            Trophee(
                "compte_exact_500",
                titreRes = R.string.trophee_titre_compte_exact_500,
                descriptionRes = R.string.trophee_desc_compte_exact_500,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.SAPHIR,
                objectif = 500,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 500 },
        )
        add(
            Trophee(
                "compte_exact_750",
                titreRes = R.string.trophee_titre_compte_exact_750,
                descriptionRes = R.string.trophee_desc_compte_exact_750,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.RUBIS,
                objectif = 750,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 750 },
        )
        add(
            Trophee(
                "compte_exact_1000",
                titreRes = R.string.trophee_titre_compte_exact_1000,
                descriptionRes = R.string.trophee_desc_compte_exact_1000,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.DIAMANT,
                objectif = 1000,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 1000 },
        )

        for (longueur in LONGUEURS_MOTS_TROPHEE) {
            val descRes1 = if (longueur == 10) R.string.trophee_desc_mot_1_max else R.string.trophee_desc_mot_1
            add(
                Trophee(
                    "mot_${longueur}_1",
                    titreRes = R.string.trophee_titre_mot_1,
                    titreArgs = listOf(longueur),
                    descriptionRes = descRes1,
                    descriptionArgs = listOf(longueur),
                    categorie = CategorieTrophee.MOTS,
                    palier = PALIERS_MOTS_1.getValue(longueur),
                    objectif = 1,
                    progression = { (it.motsParLongueur[longueur] ?: 0) },
                ) { (it.motsParLongueur[longueur] ?: 0) >= 1 },
            )
            // Pas de palier "10 mots" à la longueur maximale (10 lettres) : le 1er mot de cette
            // longueur (ci-dessus) est déjà le palier Diamant, le plus haut de la catégorie.
            if (longueur != 10) {
                add(
                    Trophee(
                        "mot_${longueur}_10",
                        titreRes = R.string.trophee_titre_mot_10,
                        titreArgs = listOf(longueur),
                        descriptionRes = R.string.trophee_desc_mot_10,
                        descriptionArgs = listOf(longueur),
                        categorie = CategorieTrophee.MOTS,
                        palier = PALIERS_MOTS_10.getValue(longueur),
                        objectif = 10,
                        progression = { (it.motsParLongueur[longueur] ?: 0) },
                    ) { (it.motsParLongueur[longueur] ?: 0) >= 10 },
                )
            }
        }

        add(
            Trophee(
                "partie_parfaite_chiffres",
                titreRes = R.string.trophee_titre_partie_parfaite_chiffres,
                descriptionRes = R.string.trophee_desc_partie_parfaite_chiffres,
                categorie = CategorieTrophee.PARTIE_PARFAITE,
                palier = Palier.BRONZE,
            ) { it.partieTousComptesExacts },
        )
        for (seuil in SEUILS_MOTS) {
            // Retour utilisateur : à partir de 7 lettres, la partie doit en plus avoir été jouée
            // au niveau Mathieu (aucune lettre exclue) — sinon le palier viendrait trop souvent
            // de tirages déjà facilités par l'exclusion des lettres rares.
            if (seuil in SEUILS_MOTS_NIVEAU_MATHIEU) {
                add(
                    Trophee(
                        "partie_mots_min_$seuil",
                        titreRes = R.string.trophee_titre_partie_mots_min_mathieu,
                        titreArgs = listOf(seuil),
                        descriptionRes = R.string.trophee_desc_partie_mots_min_mathieu,
                        descriptionArgs = listOf(seuil),
                        categorie = CategorieTrophee.PARTIE_PARFAITE,
                        palier = PALIERS_PARTIE_MOTS_MIN.getValue(seuil),
                    ) { it.partiesMotsMinNiveauMathieu[seuil] == true },
                )
            } else {
                add(
                    Trophee(
                        "partie_mots_min_$seuil",
                        titreRes = R.string.trophee_titre_partie_mots_min,
                        titreArgs = listOf(seuil),
                        descriptionRes = R.string.trophee_desc_partie_mots_min,
                        descriptionArgs = listOf(seuil),
                        categorie = CategorieTrophee.PARTIE_PARFAITE,
                        palier = PALIERS_PARTIE_MOTS_MIN.getValue(seuil),
                    ) { it.partiesMotsMin[seuil] == true },
                )
            }
        }

        for (seuil in SEUILS_SCORE) {
            add(
                Trophee(
                    "score_${seuil}_1",
                    titreRes = R.string.trophee_titre_score_1,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_score_1,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.SCORE_PARTIE,
                    palier = PALIERS_SCORE_1.getValue(seuil),
                    objectif = 1,
                    progression = { (it.partiesParSeuilScore[seuil] ?: 0) },
                ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 1 },
            )
            // Pas de palier "10 parties" au seuil maximal (90 points) : la 1ère partie à ce seuil
            // (ci-dessus) est déjà le palier Diamant, le plus haut de la catégorie.
            if (seuil != 90) {
                add(
                    Trophee(
                        "score_${seuil}_10",
                        titreRes = R.string.trophee_titre_score_10,
                        titreArgs = listOf(seuil),
                        descriptionRes = R.string.trophee_desc_score_10,
                        descriptionArgs = listOf(seuil),
                        categorie = CategorieTrophee.SCORE_PARTIE,
                        palier = PALIERS_SCORE_10.getValue(seuil),
                        objectif = 10,
                        progression = { (it.partiesParSeuilScore[seuil] ?: 0) },
                    ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 10 },
                )
            }
        }

        add(
            Trophee(
                "parties_1",
                titreRes = R.string.trophee_titre_parties_1,
                descriptionRes = R.string.trophee_desc_parties_1,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.BRONZE,
                objectif = 1,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 1 },
        )
        add(
            Trophee(
                "parties_10",
                titreRes = R.string.trophee_titre_parties_10,
                descriptionRes = R.string.trophee_desc_parties_10,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.ARGENT,
                objectif = 10,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 10 },
        )
        add(
            Trophee(
                "parties_50",
                titreRes = R.string.trophee_titre_parties_50,
                descriptionRes = R.string.trophee_desc_parties_50,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.OR,
                objectif = 50,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 50 },
        )
        add(
            Trophee(
                "parties_100",
                titreRes = R.string.trophee_titre_parties_100,
                descriptionRes = R.string.trophee_desc_parties_100,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.PLATINE,
                objectif = 100,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 100 },
        )
        add(
            Trophee(
                "parties_150",
                titreRes = R.string.trophee_titre_parties_150,
                descriptionRes = R.string.trophee_desc_parties_150,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.EMERAUDE,
                objectif = 150,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 150 },
        )
        add(
            Trophee(
                "parties_200",
                titreRes = R.string.trophee_titre_parties_200,
                descriptionRes = R.string.trophee_desc_parties_200,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.SAPHIR,
                objectif = 200,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 200 },
        )
        add(
            Trophee(
                "parties_250",
                titreRes = R.string.trophee_titre_parties_250,
                descriptionRes = R.string.trophee_desc_parties_250,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.RUBIS,
                objectif = 250,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 250 },
        )
        add(
            Trophee(
                "parties_500",
                titreRes = R.string.trophee_titre_parties_500,
                descriptionRes = R.string.trophee_desc_parties_500,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.DIAMANT,
                objectif = 500,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 500 },
        )

        // Duo, Confrontation, Duel mots et Duel points partagent une seule progression fusionnée
        // (retour utilisateur, 2026-08-30 puis 2026-09-03) : une partie jouée compte pour ce
        // trophée quel que soit le mode parmi ces 4 et quel qu'en soit le résultat (auparavant
        // réservé aux victoires), au lieu de 3 séries de trophées parallèles distinguées par
        // sous-titre.
        add(
            Trophee(
                "duo_1",
                titreRes = R.string.trophee_titre_duo_1,
                descriptionRes = R.string.trophee_desc_duo_1,
                categorie = CategorieTrophee.DUO,
                palier = Palier.ARGENT,
                objectif = 1,
                progression = {
                    it.partiesDuoJouees + it.partiesConfrontationJouees +
                        it.partiesDuelMotsJouees + it.partiesDuelMotsConfrontationJouees +
                        it.partiesDuelPointsJouees
                },
            ) {
                it.partiesDuoJouees + it.partiesConfrontationJouees +
                    it.partiesDuelMotsJouees + it.partiesDuelMotsConfrontationJouees +
                    it.partiesDuelPointsJouees >= 1
            },
        )
        add(
            Trophee(
                "duo_jouee_5",
                titreRes = R.string.trophee_titre_duo_jouee_5,
                descriptionRes = R.string.trophee_desc_duo_jouee_5,
                categorie = CategorieTrophee.DUO,
                palier = Palier.OR,
                objectif = 5,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 5 },
        )
        add(
            Trophee(
                "duo_jouee_10",
                titreRes = R.string.trophee_titre_duo_jouee_10,
                descriptionRes = R.string.trophee_desc_duo_jouee_10,
                categorie = CategorieTrophee.DUO,
                palier = Palier.PLATINE,
                objectif = 10,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 10 },
        )
        add(
            Trophee(
                "duo_jouee_25",
                titreRes = R.string.trophee_titre_duo_jouee_25,
                descriptionRes = R.string.trophee_desc_duo_jouee_25,
                categorie = CategorieTrophee.DUO,
                palier = Palier.EMERAUDE,
                objectif = 25,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 25 },
        )
        add(
            Trophee(
                "duo_jouee_50",
                titreRes = R.string.trophee_titre_duo_jouee_50,
                descriptionRes = R.string.trophee_desc_duo_jouee_50,
                categorie = CategorieTrophee.DUO,
                palier = Palier.SAPHIR,
                objectif = 50,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 50 },
        )
        add(
            Trophee(
                "duo_jouee_75",
                titreRes = R.string.trophee_titre_duo_jouee_75,
                descriptionRes = R.string.trophee_desc_duo_jouee_75,
                categorie = CategorieTrophee.DUO,
                palier = Palier.RUBIS,
                objectif = 75,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 75 },
        )
        add(
            Trophee(
                "duo_jouee_100",
                titreRes = R.string.trophee_titre_duo_jouee_100,
                descriptionRes = R.string.trophee_desc_duo_jouee_100,
                categorie = CategorieTrophee.DUO,
                palier = Palier.DIAMANT,
                objectif = 100,
                progression = { it.partiesJoueesDuel },
            ) { it.partiesJoueesDuel >= 100 },
        )

        for (mode in ModeJeu.entries) {
            val modeCode = mode.name.lowercase()
            val modeMinusculeRes = if (mode == ModeJeu.CHIFFRES) R.string.mode_minuscule_chiffres else R.string.mode_minuscule_lettres
            val sousTitreModeRes = if (mode == ModeJeu.CHIFFRES) R.string.soustitre_chiffres else R.string.soustitre_lettres
            for (seuil in SEUILS_DEFI_SERIE) {
                add(
                    Trophee(
                        "defi_serie_${modeCode}_$seuil",
                        titreRes = R.string.trophee_titre_defi_serie,
                        titreArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        descriptionRes = R.string.trophee_desc_defi_serie,
                        descriptionArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI,
                        palier = PALIERS_DEFI_UNIFIE.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresSeriesDefi[mode.name] ?: 0) },
                    ) { (it.meilleuresSeriesDefi[mode.name] ?: 0) >= seuil },
                )
            }
            // Retour utilisateur : au-delà de Or, le palier dépend du niveau où la série a été
            // réalisée (Émeraude à Monique+, puis Saphir/Rubis à Mathieu, avec un seuil de
            // comptage propre à chaque palier).
            add(
                Trophee(
                    "defi_serie_${modeCode}_10_monique",
                    titreRes = R.string.trophee_titre_defi_serie_niveau_monique,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT, ArgRes(modeMinusculeRes)),
                    descriptionRes = R.string.trophee_desc_defi_serie_niveau_monique,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT, ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI,
                    palier = Palier.EMERAUDE,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MONIQUE_COURT,
                    progression = { (it.meilleuresSeriesDefiNiveauMonique[mode.name] ?: 0) },
                ) { (it.meilleuresSeriesDefiNiveauMonique[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MONIQUE_COURT },
            )
            for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU_COURT) {
                add(
                    Trophee(
                        "defi_serie_${modeCode}_${seuil}_mathieu",
                        titreRes = R.string.trophee_titre_defi_serie_niveau_mathieu,
                        titreArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        descriptionRes = R.string.trophee_desc_defi_serie_niveau_mathieu,
                        descriptionArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI,
                        palier = PALIERS_DEFI_NIVEAU_MATHIEU_COURT.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresSeriesDefiNiveauMathieu[mode.name] ?: 0) },
                    ) { (it.meilleuresSeriesDefiNiveauMathieu[mode.name] ?: 0) >= seuil },
                )
            }
        }

        for (mode in ModeJeu.entries) {
            val natureRes = if (mode == ModeJeu.CHIFFRES) R.string.nature_comptes_exacts else R.string.nature_mots
            val modeCode = mode.name.lowercase()
            val modeMinusculeRes = if (mode == ModeJeu.CHIFFRES) R.string.mode_minuscule_chiffres else R.string.mode_minuscule_lettres
            val sousTitreModeRes = if (mode == ModeJeu.CHIFFRES) R.string.soustitre_chiffres else R.string.soustitre_lettres
            for (seuil in SEUILS_DEFI_CHRONO) {
                add(
                    Trophee(
                        "defi_chrono_${modeCode}_$seuil",
                        titreRes = R.string.trophee_titre_defi_chrono,
                        titreArgs = listOf(seuil, ArgRes(natureRes)),
                        descriptionRes = R.string.trophee_desc_defi_chrono,
                        descriptionArgs = listOf(seuil, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI_CHRONO,
                        palier = PALIERS_DEFI_UNIFIE.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresReussitesDefiChrono[mode.name] ?: 0) },
                    ) { (it.meilleuresReussitesDefiChrono[mode.name] ?: 0) >= seuil },
                )
            }
            // Échelle plus courte que Série/Sans-faute/Mots max (retour utilisateur) : le jalon
            // Monique+ saute directement à Émeraude (pas de Platine), et Mathieu s'arrête à Rubis.
            add(
                Trophee(
                    "defi_chrono_${modeCode}_10_monique",
                    titreRes = R.string.trophee_titre_defi_chrono_niveau_monique,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT, ArgRes(natureRes)),
                    descriptionRes = R.string.trophee_desc_defi_chrono_niveau_monique,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI_CHRONO,
                    palier = Palier.EMERAUDE,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MONIQUE_COURT,
                    progression = { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) },
                ) { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MONIQUE_COURT },
            )
            for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU_COURT) {
                add(
                    Trophee(
                        "defi_chrono_${modeCode}_${seuil}_mathieu",
                        titreRes = R.string.trophee_titre_defi_chrono_niveau_mathieu,
                        titreArgs = listOf(seuil, ArgRes(natureRes)),
                        descriptionRes = R.string.trophee_desc_defi_chrono_niveau_mathieu,
                        descriptionArgs = listOf(seuil, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI_CHRONO,
                        palier = PALIERS_DEFI_NIVEAU_MATHIEU_COURT.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresReussitesDefiChronoNiveauMathieu[mode.name] ?: 0) },
                    ) { (it.meilleuresReussitesDefiChronoNiveauMathieu[mode.name] ?: 0) >= seuil },
                )
            }
        }

        for (seuil in SEUILS_DEFI_MOTS_MAX) {
            add(
                Trophee(
                    "defi_mots_max_$seuil",
                    titreRes = R.string.trophee_titre_defi_mots_max,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_mots_max,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_MOTS_MAX,
                    palier = PALIERS_DEFI_UNIFIE.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleurScoreDefiMotsMax },
                ) { it.meilleurScoreDefiMotsMax >= seuil },
            )
        }
        add(
            Trophee(
                "defi_mots_max_10_monique",
                titreRes = R.string.trophee_titre_defi_mots_max_niveau_monique,
                titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE),
                descriptionRes = R.string.trophee_desc_defi_mots_max_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE),
                categorie = CategorieTrophee.DEFI_MOTS_MAX,
                palier = Palier.PLATINE,
                objectif = SEUIL_DEFI_NIVEAU_MONIQUE,
                progression = { it.meilleurScoreDefiMotsMaxNiveauMonique },
            ) { it.meilleurScoreDefiMotsMaxNiveauMonique >= SEUIL_DEFI_NIVEAU_MONIQUE },
        )
        for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU) {
            add(
                Trophee(
                    "defi_mots_max_${seuil}_mathieu",
                    titreRes = R.string.trophee_titre_defi_mots_max_niveau_mathieu,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_mots_max_niveau_mathieu,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_MOTS_MAX,
                    palier = PALIERS_DEFI_NIVEAU_MATHIEU.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleurScoreDefiMotsMaxNiveauMathieu },
                ) { it.meilleurScoreDefiMotsMaxNiveauMathieu >= seuil },
            )
        }

        // Défi Points (refonte 2026-09-03) : nombreObjectifsDefiPoints est passé à 3/5/8/15
        // objectifs selon le niveau (Émile/Nestor/Monique/Mathieu, cf. `ParametresDefi.kt`), ce qui
        // rend les seuils 8/10/12/15 atteignables en une seule partie à partir du niveau Monique
        // (8) ou Mathieu (15) — plus besoin de distinguer un palier "défi complet" par niveau.
        for (seuil in SEUILS_DEFI_POINTS) {
            add(
                Trophee(
                    "defi_points_$seuil",
                    titreRes = R.string.trophee_titre_defi_points,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_points,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_OBJECTIFS_POINTS,
                    palier = PALIERS_DEFI_POINTS.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleurScoreDefiObjectifsPoints },
                ) { it.meilleurScoreDefiObjectifsPoints >= seuil },
            )
        }

        for (seuil in SEUILS_DEFI_SANS_FAUTE) {
            add(
                Trophee(
                    "defi_sans_faute_$seuil",
                    titreRes = R.string.trophee_titre_defi_sans_faute,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_sans_faute,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                    palier = PALIERS_DEFI_UNIFIE.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleureSerieSansFaute },
                ) { it.meilleureSerieSansFaute >= seuil },
            )
        }
        add(
            Trophee(
                "defi_sans_faute_10_monique",
                titreRes = R.string.trophee_titre_defi_sans_faute_niveau_monique,
                titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT),
                descriptionRes = R.string.trophee_desc_defi_sans_faute_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE_COURT),
                categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                palier = Palier.EMERAUDE,
                objectif = SEUIL_DEFI_NIVEAU_MONIQUE_COURT,
                progression = { it.meilleureSerieSansFauteNiveauMonique },
            ) { it.meilleureSerieSansFauteNiveauMonique >= SEUIL_DEFI_NIVEAU_MONIQUE_COURT },
        )
        for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU_COURT) {
            add(
                Trophee(
                    "defi_sans_faute_${seuil}_mathieu",
                    titreRes = R.string.trophee_titre_defi_sans_faute_niveau_mathieu,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_sans_faute_niveau_mathieu,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                    palier = PALIERS_DEFI_NIVEAU_MATHIEU_COURT.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleureSerieSansFauteNiveauMathieu },
                ) { it.meilleureSerieSansFauteNiveauMathieu >= seuil },
            )
        }

        val titresPaliersQuotidien = mapOf(
            7 to R.string.trophee_titre_defi_quotidien_semaine,
            14 to R.string.trophee_titre_defi_quotidien_deux_semaines,
            21 to R.string.trophee_titre_defi_quotidien_trois_semaines,
        )
        for (seuil in SEUILS_DEFI_QUOTIDIEN) {
            add(
                Trophee(
                    "defi_quotidien_$seuil",
                    titreRes = titresPaliersQuotidien.getValue(seuil),
                    descriptionRes = R.string.trophee_desc_defi_quotidien,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_QUOTIDIEN,
                    palier = PALIERS_DEFI_QUOTIDIEN.getValue(seuil),
                    objectif = seuil,
                    progression = { it.serieEnCoursJoursDefiQuotidien },
                ) { it.meilleureSerieJoursDefiQuotidien >= seuil },
            )
        }
        add(
            Trophee(
                "defi_quotidien_28_monique",
                titreRes = R.string.trophee_titre_defi_quotidien_niveau_monique,
                titreArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE),
                descriptionRes = R.string.trophee_desc_defi_quotidien_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE),
                categorie = CategorieTrophee.DEFI_QUOTIDIEN,
                palier = Palier.PLATINE,
                objectif = SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE,
                progression = { it.serieEnCoursJoursDefiQuotidienNiveauMonique },
            ) { it.meilleureSerieJoursDefiQuotidienNiveauMonique >= SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE },
        )
        for (seuil in SEUILS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU) {
            add(
                Trophee(
                    "defi_quotidien_${seuil}_mathieu",
                    titreRes = R.string.trophee_titre_defi_quotidien_niveau_mathieu,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_quotidien_niveau_mathieu,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_QUOTIDIEN,
                    palier = PALIERS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU.getValue(seuil),
                    objectif = seuil,
                    progression = { it.serieEnCoursJoursDefiQuotidienNiveauMathieu },
                ) { it.meilleureSerieJoursDefiQuotidienNiveauMathieu >= seuil },
            )
        }

        // Méta-trophées "tous les trophées de la section X débloqués" (refonte 2026-08). Leur
        // condition dépend de l'état des AUTRES trophées, pas des stats de jeu : `estDebloque`
        // ici est un sentinel qui ne se déclenche jamais tout seul — c'est
        // `TropheeRepository.reevaluer` qui les débloque explicitement une fois les autres
        // trophées de la section à jour (cf. [idsSectionDefi]/[idsSectionPartie]).
        add(
            Trophee(
                "section_defi_complete",
                titreRes = R.string.trophee_titre_section_defi_complete,
                descriptionRes = R.string.trophee_desc_section_defi_complete,
                categorie = CategorieTrophee.TROPHEES_SPECIAUX,
                palier = Palier.DIAMANT,
                estDebloque = { false },
            ),
        )
        add(
            Trophee(
                "section_partie_complete",
                titreRes = R.string.trophee_titre_section_partie_complete,
                descriptionRes = R.string.trophee_desc_section_partie_complete,
                categorie = CategorieTrophee.TROPHEES_SPECIAUX,
                palier = Palier.DIAMANT,
                estDebloque = { false },
            ),
        )

        // --- Easter eggs (refonte 2026-08), groupe 1 : calculables avec les données déjà en
        // base (voir trophées_paliers2.xlsx, feuille "Général"/"Lettres"/"Ultime"). Les 4
        // marquées `estDebloque = { false }` dépendent de l'état d'autres trophées (comme les
        // méta-trophées de section ci-dessus) et sont débloquées à part dans
        // `TropheeRepository.reevaluer`.
        add(
            Trophee(
                "easter_ancien_combattant",
                titreRes = R.string.trophee_titre_easter_ancien_combattant,
                descriptionRes = R.string.trophee_desc_easter_ancien_combattant,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_longue_haleine,
            ) { it.ancienneteJoursProfil >= 365 },
        )
        add(
            Trophee(
                "easter_multi_niveaux",
                titreRes = R.string.trophee_titre_easter_multi_niveaux,
                descriptionRes = R.string.trophee_desc_easter_multi_niveaux,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_assiduite,
            ) { it.nombreNiveauxDistinctsJoues >= 4 },
        )
        add(
            Trophee(
                "easter_marathon",
                titreRes = R.string.trophee_titre_easter_marathon,
                descriptionRes = R.string.trophee_desc_easter_marathon,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_assiduite,
                objectif = 20,
                progression = { it.maxPartiesMemeJour },
            ) { it.maxPartiesMemeJour >= 20 },
        )
        add(
            Trophee(
                "easter_ca_ne_sarrete_jamais",
                titreRes = R.string.trophee_titre_easter_ca_ne_sarrete_jamais,
                descriptionRes = R.string.trophee_desc_easter_ca_ne_sarrete_jamais,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_assiduite,
            ) { it.cinqPartiesEnUneHeure },
        )
        add(
            Trophee(
                "easter_constance",
                titreRes = R.string.trophee_titre_easter_constance,
                descriptionRes = R.string.trophee_desc_easter_constance,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_longue_haleine,
            ) { it.ecartDixDernieresPartiesFaible },
        )
        add(
            Trophee(
                "easter_polyvalent",
                titreRes = R.string.trophee_titre_easter_polyvalent,
                descriptionRes = R.string.trophee_desc_easter_polyvalent,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_longue_haleine,
                estDebloque = { false },
            ),
        )
        add(
            Trophee(
                "easter_specialiste_complet",
                titreRes = R.string.trophee_titre_easter_specialiste_complet,
                descriptionRes = R.string.trophee_desc_easter_specialiste_complet,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_longue_haleine,
                estDebloque = { false },
            ),
        )
        add(
            Trophee(
                "easter_bonjour",
                titreRes = R.string.trophee_titre_easter_bonjour,
                descriptionRes = R.string.trophee_desc_easter_bonjour,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_heure,
            ) { it.partieJoueeEntre5et7h },
        )
        add(
            Trophee(
                "easter_oiseau_de_nuit",
                titreRes = R.string.trophee_titre_easter_oiseau_de_nuit,
                descriptionRes = R.string.trophee_desc_easter_oiseau_de_nuit,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_heure,
            ) { it.unePartieEntreMinuitEt5h },
        )
        add(
            Trophee(
                "easter_touche_a_tout",
                titreRes = R.string.trophee_titre_easter_touche_a_tout,
                descriptionRes = R.string.trophee_desc_easter_touche_a_tout,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_exploration,
            ) {
                it.partiesSoloStructureeJouees >= 1 && it.partiesConfrontationJouees >= 1 && it.partiesDuoJouees >= 1 &&
                    (it.partiesDuelMotsJouees + it.partiesDuelMotsConfrontationJouees) >= 1 && it.defisJouesTotal >= 1
            },
        )
        add(
            Trophee(
                "easter_curieux",
                titreRes = R.string.trophee_titre_easter_curieux,
                descriptionRes = R.string.trophee_desc_easter_curieux,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_exploration,
            ) { it.reglesDejaVues },
        )
        add(
            Trophee(
                "easter_data_lover",
                titreRes = R.string.trophee_titre_easter_data_lover,
                descriptionRes = R.string.trophee_desc_easter_data_lover,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_exploration,
                objectif = 100,
                progression = { it.nombreVisitesStats },
            ) { it.nombreVisitesStats >= 100 },
        )
        add(
            Trophee(
                "easter_rituel_dimanche",
                titreRes = R.string.trophee_titre_easter_rituel_dimanche,
                descriptionRes = R.string.trophee_desc_easter_rituel_dimanche,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_assiduite,
                objectif = 4,
                progression = { it.serieEnCoursDimanches },
            ) { it.meilleureSerieDimanchesConsecutifs >= 4 },
        )
        add(
            Trophee(
                "easter_ex_aequo",
                titreRes = R.string.trophee_titre_easter_ex_aequo,
                descriptionRes = R.string.trophee_desc_easter_ex_aequo,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.egaliteDuelDejaObtenue },
        )
        add(
            Trophee(
                "easter_compte_rond",
                titreRes = R.string.trophee_titre_easter_compte_rond,
                descriptionRes = R.string.trophee_desc_easter_compte_rond,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.duelPointsCompteRondObtenu },
        )
        add(
            Trophee(
                "easter_rouleau_compresseur",
                titreRes = R.string.trophee_titre_easter_rouleau_compresseur,
                descriptionRes = R.string.trophee_desc_easter_rouleau_compresseur,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.duelPointsEcartVictoireMax >= 20 },
        )
        add(
            Trophee(
                "easter_deculottee",
                titreRes = R.string.trophee_titre_easter_deculottee,
                descriptionRes = R.string.trophee_desc_easter_deculottee,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.duelPointsEcartDefaiteMax >= 20 },
        )
        add(
            Trophee(
                "easter_symetrie",
                titreRes = R.string.trophee_titre_easter_symetrie,
                descriptionRes = R.string.trophee_desc_easter_symetrie,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.scoreSoloRepete },
        )
        add(
            Trophee(
                "easter_noce_de_chene",
                titreRes = R.string.trophee_titre_easter_noce_de_chene,
                descriptionRes = R.string.trophee_desc_easter_noce_de_chene,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
                estDebloque = { false },
            ),
        )
        add(
            Trophee(
                "easter_mot_invalide_dix_lettres",
                titreRes = R.string.trophee_titre_easter_mot_invalide_dix_lettres,
                descriptionRes = R.string.trophee_desc_easter_mot_invalide_dix_lettres,
                categorie = CategorieTrophee.EASTER_LETTRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_vocabulaire,
            ) { it.motInvalideDixLettresTente },
        )
        add(
            Trophee(
                "easter_mot_rare",
                titreRes = R.string.trophee_titre_easter_mot_rare,
                descriptionRes = R.string.trophee_desc_easter_mot_rare,
                categorie = CategorieTrophee.EASTER_LETTRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_vocabulaire,
            ) { it.motRareJoue },
        )
        add(
            Trophee(
                "easter_palindrome",
                titreRes = R.string.trophee_titre_easter_palindrome,
                descriptionRes = R.string.trophee_desc_easter_palindrome,
                categorie = CategorieTrophee.EASTER_LETTRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_vocabulaire,
            ) { it.palindromeJoue },
        )
        add(
            Trophee(
                "easter_symetrique",
                titreRes = R.string.trophee_titre_easter_symetrique,
                descriptionRes = R.string.trophee_desc_easter_symetrique,
                categorie = CategorieTrophee.EASTER_LETTRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_vocabulaire,
            ) { it.motSymetriqueJoue },
        )
        add(
            Trophee(
                "easter_alphabet_complet",
                titreRes = R.string.trophee_titre_easter_alphabet_complet,
                descriptionRes = R.string.trophee_desc_easter_alphabet_complet,
                categorie = CategorieTrophee.EASTER_LETTRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_vocabulaire,
                objectif = 26,
                progression = { it.nombreLettresAlphabetUtilisees },
            ) { it.nombreLettresAlphabetUtilisees >= 26 },
        )
        add(
            Trophee(
                "easter_nombre_premier",
                titreRes = R.string.trophee_titre_easter_nombre_premier,
                descriptionRes = R.string.trophee_desc_easter_nombre_premier,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactCibleNombrePremier },
        )
        add(
            Trophee(
                "easter_calcul_mental",
                titreRes = R.string.trophee_titre_easter_calcul_mental,
                descriptionRes = R.string.trophee_desc_easter_calcul_mental,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactCalculMental },
        )
        add(
            Trophee(
                "easter_chemin_minimal",
                titreRes = R.string.trophee_titre_easter_chemin_minimal,
                descriptionRes = R.string.trophee_desc_easter_chemin_minimal,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactCheminMinimal },
        )
        add(
            Trophee(
                "easter_chirurgical",
                titreRes = R.string.trophee_titre_easter_chirurgical,
                descriptionRes = R.string.trophee_desc_easter_chirurgical,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactChirurgical },
        )
        add(
            Trophee(
                "easter_speedrun",
                titreRes = R.string.trophee_titre_easter_speedrun,
                descriptionRes = R.string.trophee_desc_easter_speedrun,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactSpeedrun },
        )
        add(
            Trophee(
                "easter_va_tout",
                titreRes = R.string.trophee_titre_easter_va_tout,
                descriptionRes = R.string.trophee_desc_easter_va_tout,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_timing,
            ) { it.compteExactVaTout },
        )
        add(
            Trophee(
                "easter_a_cote_de_la_plaque",
                titreRes = R.string.trophee_titre_easter_a_cote_de_la_plaque,
                descriptionRes = R.string.trophee_desc_easter_a_cote_de_la_plaque,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.ecartEnormeChiffres },
        )
        add(
            Trophee(
                "easter_boite_a_outils",
                titreRes = R.string.trophee_titre_easter_boite_a_outils,
                descriptionRes = R.string.trophee_desc_easter_boite_a_outils,
                categorie = CategorieTrophee.EASTER_CHIFFRES,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_precision_chiffres,
            ) { it.compteExactBoiteAOutils },
        )
        add(
            Trophee(
                "easter_aucune_idee",
                titreRes = R.string.trophee_titre_easter_aucune_idee,
                descriptionRes = R.string.trophee_desc_easter_aucune_idee,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.aucuneIdeeProposee },
        )
        add(
            Trophee(
                "easter_cent_heures",
                titreRes = R.string.trophee_titre_easter_cent_heures,
                descriptionRes = R.string.trophee_desc_easter_cent_heures,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.SEMI_CACHE,
                descriptionAvantDeblocageRes = R.string.easter_avant_longue_haleine,
                objectif = SECONDES_CENT_HEURES,
                progression = { it.secondesJoueesTotal },
                uniteProgression = UniteProgression.DUREE,
            ) { it.secondesJoueesTotal >= SECONDES_CENT_HEURES },
        )
        add(
            Trophee(
                "easter_toit_du_monde",
                titreRes = R.string.trophee_titre_easter_toit_du_monde,
                descriptionRes = R.string.trophee_desc_easter_toit_du_monde,
                categorie = CategorieTrophee.EASTER_SECRETS,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
                estDebloque = { false },
            ),
        )
    }

    /** Rendu par `TropheesScreen.kt` sous le grand titre "Trophées des défis". */
    val CATEGORIES_SECTION_DEFI = setOf(
        CategorieTrophee.DEFI, CategorieTrophee.DEFI_CHRONO, CategorieTrophee.DEFI_MOTS_MAX,
        CategorieTrophee.DEFI_OBJECTIFS_POINTS, CategorieTrophee.DEFI_SANS_FAUTE, CategorieTrophee.DEFI_QUOTIDIEN,
    )

    /** Rendu par `TropheesScreen.kt` sous le grand titre "Trophées des parties et duels". */
    val CATEGORIES_SECTION_PARTIE = setOf(
        CategorieTrophee.PARTIES_TERMINEES, CategorieTrophee.SCORE_PARTIE, CategorieTrophee.COMPTES_EXACTS,
        CategorieTrophee.PARTIE_PARFAITE, CategorieTrophee.MOTS, CategorieTrophee.DUO,
    )

    /**
     * Icône dédiée d'un easter egg (colonne "Icône" de trophées_paliers2.xlsx, retour
     * utilisateur), affichée à la place du 🏆 générique dans la tuile/le détail/le dialogue de
     * déblocage. Les trophées gradués (Bronze→Diamant) gardent le 🏆.
     */
    private val ICONES_EASTER_EGGS: Map<String, String> = mapOf(
        "easter_nombre_premier" to "➗",
        "easter_calcul_mental" to "🧠",
        "easter_chemin_minimal" to "📐",
        "easter_chirurgical" to "🔢",
        "easter_speedrun" to "⏱️",
        "easter_va_tout" to "🎰",
        "easter_symetrique" to "🔠",
        "easter_alphabet_complet" to "🔤",
        "easter_mot_invalide_dix_lettres" to "📏",
        "easter_mot_rare" to "🦕",
        "easter_palindrome" to "🪞",
        "easter_noce_de_chene" to "🌳",
        "easter_ex_aequo" to "🤝",
        "easter_symetrie" to "⚖️",
        "easter_curieux" to "📖",
        "easter_data_lover" to "📊",
        "easter_ancien_combattant" to "🏅",
        "easter_marathon" to "🎪",
        "easter_multi_niveaux" to "🌍",
        "easter_rituel_dimanche" to "📆",
        "easter_ca_ne_sarrete_jamais" to "🔥",
        "easter_constance" to "🧭",
        "easter_polyvalent" to "🎓",
        "easter_specialiste_complet" to "🧩",
        "easter_cent_heures" to "⏳",
        "easter_bonjour" to "☕",
        "easter_oiseau_de_nuit" to "🌙",
        "easter_touche_a_tout" to "🗺️",
        "easter_aucune_idee" to "🙈",
        "easter_toit_du_monde" to "🏔️",
        "easter_compte_rond" to "🎯",
        "easter_rouleau_compresseur" to "🚜",
        "easter_deculottee" to "🩲",
        "easter_a_cote_de_la_plaque" to "🛰️",
        "easter_boite_a_outils" to "🧰",
    )

    fun iconeTrophee(id: String): String = ICONES_EASTER_EGGS[id] ?: "🏆"

    /** Ids des trophées "défi" hors méta-trophée lui-même (voir `TropheeRepository.reevaluer`). */
    fun idsSectionDefi(): Set<String> = TOUS.filter { it.categorie in CATEGORIES_SECTION_DEFI }.map { it.id }.toSet()

    /** Ids des trophées "partie" hors méta-trophée lui-même (voir `TropheeRepository.reevaluer`). */
    fun idsSectionPartie(): Set<String> = TOUS.filter { it.categorie in CATEGORIES_SECTION_PARTIE }.map { it.id }.toSet()

    /**
     * Les "grandes catégories" de jeu (défi + partie confondus, hors trophées spéciaux/easter
     * eggs) — utilisé par les easter eggs "Polyvalent"/"Spécialiste complet" (voir
     * `TropheeRepository.reevaluer`).
     */
    val CATEGORIES_JEU: Set<CategorieTrophee> = CATEGORIES_SECTION_DEFI + CATEGORIES_SECTION_PARTIE

    /**
     * Rang global d'un joueur (retour utilisateur) : le palier le plus haut dont TOUS les
     * trophées de ce palier ET des paliers inférieurs sont débloqués — cumulatif, pas juste le
     * palier isolé. Null si même le bronze n'est pas complet.
     */
    fun rangJoueur(idsDebloques: Set<String>): Palier? {
        var rang: Palier? = null
        // Les easter eggs (palier == null) sont exclus de ce calcul (retour utilisateur) : ce
        // sont des curiosités, pas des jalons de la progression Bronze→Diamant.
        val tropheesAPalier = TOUS.filter { it.palier != null }
        for (palier in Palier.entries) {
            val complet = tropheesAPalier.filter { it.palier!!.ordinal <= palier.ordinal }.all { it.id in idsDebloques }
            if (!complet) break
            rang = palier
        }
        return rang
    }
}
