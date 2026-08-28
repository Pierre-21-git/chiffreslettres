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
     * trophées "duo_*" additionnent les deux (retour utilisateur : une seule catégorie de trophée
     * pour Duo et Confrontation confondus).
     */
    val partiesDuoJouees: Int,
    val partiesDuoGagnees: Int,
    /** Parties Confrontation, même téléphone ou à distance confondus. Voir [partiesDuoJouees]. */
    val partiesConfrontationJouees: Int,
    val partiesConfrontationGagnees: Int,
    /**
     * Parties Duel mots, sous-mode Duo (100 % réseau, pas de variante même téléphone). Comme
     * [partiesDuoJouees], les trophées "duel_mots_*" additionnent Duo et Confrontation.
     */
    val partiesDuelMotsJouees: Int,
    val partiesDuelMotsGagnees: Int,
    /** Parties Duel mots, sous-mode Confrontation (100 % réseau). Voir [partiesDuelMotsJouees]. */
    val partiesDuelMotsConfrontationJouees: Int,
    val partiesDuelMotsConfrontationGagnees: Int,
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
    /** Le défi Points a-t-il déjà été complété (tous les objectifs atteints) au moins une fois, tous niveaux confondus. */
    val defiObjectifsPointsComplete: Boolean,
    /** Comme [defiObjectifsPointsComplete], restreint au niveau Monique ou Mathieu. */
    val defiObjectifsPointsCompleteNiveauMonique: Boolean,
    /** Comme [defiObjectifsPointsComplete], restreint au niveau Mathieu. */
    val defiObjectifsPointsCompleteNiveauMathieu: Boolean,
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
    /** La toute première partie du joueur a été jouée entre 5h et 7h (trophée "Bonjour !"). */
    val premierePartieEntre5et7h: Boolean,
    /** Au moins une partie jouée entre minuit et 5h (trophée "Oiseau de nuit"). */
    val unePartieEntreMinuitEt5h: Boolean,
    /** Au moins un mot d'au moins 8 lettres contenant Q/X/W/Y/Z déjà joué (trophée "Mot rare"). */
    val motRareJoue: Boolean,
    /** Au moins un mot palindrome déjà joué (trophée "Palindrome"). */
    val palindromeJoue: Boolean,
    /** Au moins un mot dont les lettres sont en ordre alphabétique déjà joué (trophée "Symétrique"). */
    val motSymetriqueJoue: Boolean,
    /** Chaque lettre de l'alphabet utilisée au moins une fois, cumulé sur tous les mots joués (trophée "Alphabet complet"). */
    val alphabetComplet: Boolean,
    /** Au moins une partie jouée un dimanche, 4 semaines de suite (trophée "Rituel du dimanche"). */
    val dimancheQuatreSemainesDeSuite: Boolean,
    /** La page des règles du jeu a déjà été consultée (trophée "Curieux"). */
    val reglesDejaVues: Boolean,
    /** Nombre de fois où la page de statistiques personnelles a été ouverte (trophée "Data-lover"). */
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
    /** Une manche terminée sans aucune proposition (trophée "Aucune idée"). */
    val aucuneIdeeProposee: Boolean,
    /** Temps de jeu cumulé, en secondes (trophée "100 heures de jeu"). */
    val secondesJoueesTotal: Int,
)

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
    EASTER_ULTIME(R.string.categorie_easter_ultime),
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

// Barème unifié (refonte 2026-08, cf. trophées_paliers2.xlsx) partagé par Série/Sans-faute/Mots
// max : 3/5/8 tous niveaux (Bronze/Argent/Or), 10 au niveau Monique+ (Platine), puis 12/15/20/25
// au niveau Mathieu (Émeraude/Saphir/Rubis/Diamant).
private val SEUILS_DEFI_SERIE = listOf(3, 5, 8)
private val SEUILS_DEFI_SANS_FAUTE = listOf(3, 5, 8)
private val SEUILS_DEFI_MOTS_MAX = listOf(3, 5, 8)

/** Barème dédié au défi Points (retour utilisateur) : score maximal en une partie = nombre d'objectifs du niveau (3 à 6), bien plus faible que les autres défis lettres. */
private val SEUILS_DEFI_POINTS = listOf(1, 3)
private val PALIERS_DEFI_POINTS = mapOf(1 to Palier.BRONZE, 3 to Palier.ARGENT)
private const val SEUIL_DEFI_NIVEAU_MONIQUE = 10
private val SEUILS_DEFI_NIVEAU_MATHIEU = listOf(12, 15, 20, 25)
private val PALIERS_DEFI_NIVEAU_MATHIEU = mapOf(
    12 to Palier.EMERAUDE, 15 to Palier.SAPHIR, 20 to Palier.RUBIS, 25 to Palier.DIAMANT,
)

// Défi chrono a sa propre échelle, plus courte (pas de Platine, ni au-delà de Rubis).
private val SEUILS_DEFI_CHRONO = listOf(3, 5, 8)
private const val SEUIL_DEFI_CHRONO_NIVEAU_MONIQUE = 10
private val SEUILS_DEFI_CHRONO_NIVEAU_MATHIEU = listOf(12, 15)
private val PALIERS_DEFI_CHRONO_NIVEAU_MATHIEU = mapOf(12 to Palier.SAPHIR, 15 to Palier.RUBIS)

private val LONGUEURS_MOTS_TROPHEE = 4..10
// Défi quotidien : rythme hebdomadaire (refonte 2026-08) — 1/2/3 semaines tous niveaux, 4
// semaines au niveau Monique+, puis 4/6/8/10 semaines au niveau Mathieu.
private val SEUILS_DEFI_QUOTIDIEN = listOf(7, 14, 21)
private const val SEUIL_DEFI_QUOTIDIEN_NIVEAU_MONIQUE = 28
private val SEUILS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU = listOf(28, 42, 56, 70)
private val PALIERS_DEFI_QUOTIDIEN_NIVEAU_MATHIEU = mapOf(
    28 to Palier.EMERAUDE, 42 to Palier.SAPHIR, 56 to Palier.RUBIS, 70 to Palier.DIAMANT,
)

// Paliers (refonte 2026-08, cf. trophées_paliers2.xlsx).
private val PALIERS_PARTIE_MOTS_MIN = mapOf(4 to Palier.ARGENT, 5 to Palier.OR, 6 to Palier.PLATINE, 7 to Palier.SAPHIR, 8 to Palier.RUBIS)
private val PALIERS_MOTS_1 = mapOf(
    4 to Palier.BRONZE, 5 to Palier.ARGENT, 6 to Palier.OR, 7 to Palier.PLATINE,
    8 to Palier.EMERAUDE, 9 to Palier.SAPHIR, 10 to Palier.RUBIS,
)
private val PALIERS_MOTS_10 = mapOf(
    4 to Palier.ARGENT, 5 to Palier.OR, 6 to Palier.PLATINE, 7 to Palier.EMERAUDE,
    8 to Palier.SAPHIR, 9 to Palier.RUBIS, 10 to Palier.DIAMANT,
)
private val PALIERS_SCORE_1 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.BRONZE, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.PLATINE, 70 to Palier.EMERAUDE, 80 to Palier.SAPHIR, 90 to Palier.RUBIS,
)
private val PALIERS_SCORE_10 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.OR, 50 to Palier.PLATINE,
    60 to Palier.EMERAUDE, 70 to Palier.SAPHIR, 80 to Palier.RUBIS, 90 to Palier.DIAMANT,
)
// Barème (retour utilisateur) de série/chrono/sans-faute/mots max : 3=Bronze, 5=Argent, 8=Or.
private val PALIERS_DEFI_UNIFIE = mapOf(3 to Palier.BRONZE, 5 to Palier.ARGENT, 8 to Palier.OR)
private val PALIERS_DEFI_QUOTIDIEN = mapOf(7 to Palier.BRONZE, 14 to Palier.ARGENT, 21 to Palier.OR)

/** Catalogue complet des trophées possibles (spec produit, retour utilisateur) : 88 au total. */
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
                "compte_exact_1000",
                titreRes = R.string.trophee_titre_compte_exact_1000,
                descriptionRes = R.string.trophee_desc_compte_exact_1000,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.RUBIS,
                objectif = 1000,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 1000 },
        )
        add(
            Trophee(
                "compte_exact_2000",
                titreRes = R.string.trophee_titre_compte_exact_2000,
                descriptionRes = R.string.trophee_desc_compte_exact_2000,
                categorie = CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.DIAMANT,
                objectif = 2000,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 2000 },
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

        add(
            Trophee(
                "duo_1",
                titreRes = R.string.trophee_titre_duo_1,
                descriptionRes = R.string.trophee_desc_duo_1,
                categorie = CategorieTrophee.DUO,
                palier = Palier.ARGENT,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 1,
                progression = { it.partiesDuoJouees + it.partiesConfrontationJouees },
            ) { it.partiesDuoJouees + it.partiesConfrontationJouees >= 1 },
        )
        add(
            Trophee(
                "duo_gagnee_1",
                titreRes = R.string.trophee_titre_duo_gagnee_1,
                descriptionRes = R.string.trophee_desc_duo_gagnee_1,
                categorie = CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 1,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 1 },
        )
        add(
            Trophee(
                "duo_gagnee_10",
                titreRes = R.string.trophee_titre_duo_gagnee_10,
                descriptionRes = R.string.trophee_desc_duo_gagnee_10,
                categorie = CategorieTrophee.DUO,
                palier = Palier.PLATINE,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 10,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 10 },
        )
        add(
            Trophee(
                "duo_gagnee_25",
                titreRes = R.string.trophee_titre_duo_gagnee_25,
                descriptionRes = R.string.trophee_desc_duo_gagnee_25,
                categorie = CategorieTrophee.DUO,
                palier = Palier.EMERAUDE,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 25,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 25 },
        )
        add(
            Trophee(
                "duo_gagnee_50",
                titreRes = R.string.trophee_titre_duo_gagnee_50,
                descriptionRes = R.string.trophee_desc_duo_gagnee_50,
                categorie = CategorieTrophee.DUO,
                palier = Palier.SAPHIR,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 50,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 50 },
        )
        add(
            Trophee(
                "duo_gagnee_75",
                titreRes = R.string.trophee_titre_duo_gagnee_75,
                descriptionRes = R.string.trophee_desc_duo_gagnee_75,
                categorie = CategorieTrophee.DUO,
                palier = Palier.RUBIS,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 75,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 75 },
        )
        add(
            Trophee(
                "duo_gagnee_100",
                titreRes = R.string.trophee_titre_duo_gagnee_100,
                descriptionRes = R.string.trophee_desc_duo_gagnee_100,
                categorie = CategorieTrophee.DUO,
                palier = Palier.DIAMANT,
                sousTitreRes = R.string.soustitre_duo,
                objectif = 100,
                progression = { it.partiesDuoGagnees + it.partiesConfrontationGagnees },
            ) { it.partiesDuoGagnees + it.partiesConfrontationGagnees >= 100 },
        )
        add(
            Trophee(
                "duel_mots_1",
                titreRes = R.string.trophee_titre_duel_mots_1,
                descriptionRes = R.string.trophee_desc_duel_mots_1,
                categorie = CategorieTrophee.DUO,
                palier = Palier.ARGENT,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 1,
                progression = { it.partiesDuelMotsJouees + it.partiesDuelMotsConfrontationJouees },
            ) { it.partiesDuelMotsJouees + it.partiesDuelMotsConfrontationJouees >= 1 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_1",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_1,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_1,
                categorie = CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 1,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 1 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_10",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_10,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_10,
                categorie = CategorieTrophee.DUO,
                palier = Palier.PLATINE,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 10,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 10 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_25",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_25,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_25,
                categorie = CategorieTrophee.DUO,
                palier = Palier.EMERAUDE,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 25,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 25 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_50",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_50,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_50,
                categorie = CategorieTrophee.DUO,
                palier = Palier.SAPHIR,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 50,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 50 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_75",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_75,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_75,
                categorie = CategorieTrophee.DUO,
                palier = Palier.RUBIS,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 75,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 75 },
        )
        add(
            Trophee(
                "duel_mots_gagnee_100",
                titreRes = R.string.trophee_titre_duel_mots_gagnee_100,
                descriptionRes = R.string.trophee_desc_duel_mots_gagnee_100,
                categorie = CategorieTrophee.DUO,
                palier = Palier.DIAMANT,
                sousTitreRes = R.string.soustitre_duel_mots,
                objectif = 100,
                progression = { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees },
            ) { it.partiesDuelMotsGagnees + it.partiesDuelMotsConfrontationGagnees >= 100 },
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
            // réalisée (Platine à Monique+, puis Émeraude/Saphir/Rubis/Diamant à Mathieu, avec un
            // seuil de comptage propre à chaque palier).
            add(
                Trophee(
                    "defi_serie_${modeCode}_10_monique",
                    titreRes = R.string.trophee_titre_defi_serie_niveau_monique,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE, ArgRes(modeMinusculeRes)),
                    descriptionRes = R.string.trophee_desc_defi_serie_niveau_monique,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE, ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI,
                    palier = Palier.PLATINE,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MONIQUE,
                    progression = { (it.meilleuresSeriesDefiNiveauMonique[mode.name] ?: 0) },
                ) { (it.meilleuresSeriesDefiNiveauMonique[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MONIQUE },
            )
            for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU) {
                add(
                    Trophee(
                        "defi_serie_${modeCode}_${seuil}_mathieu",
                        titreRes = R.string.trophee_titre_defi_serie_niveau_mathieu,
                        titreArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        descriptionRes = R.string.trophee_desc_defi_serie_niveau_mathieu,
                        descriptionArgs = listOf(seuil, ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI,
                        palier = PALIERS_DEFI_NIVEAU_MATHIEU.getValue(seuil),
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
                    titreArgs = listOf(SEUIL_DEFI_CHRONO_NIVEAU_MONIQUE, ArgRes(natureRes)),
                    descriptionRes = R.string.trophee_desc_defi_chrono_niveau_monique,
                    descriptionArgs = listOf(SEUIL_DEFI_CHRONO_NIVEAU_MONIQUE, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI_CHRONO,
                    palier = Palier.EMERAUDE,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_CHRONO_NIVEAU_MONIQUE,
                    progression = { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) },
                ) { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) >= SEUIL_DEFI_CHRONO_NIVEAU_MONIQUE },
            )
            for (seuil in SEUILS_DEFI_CHRONO_NIVEAU_MATHIEU) {
                add(
                    Trophee(
                        "defi_chrono_${modeCode}_${seuil}_mathieu",
                        titreRes = R.string.trophee_titre_defi_chrono_niveau_mathieu,
                        titreArgs = listOf(seuil, ArgRes(natureRes)),
                        descriptionRes = R.string.trophee_desc_defi_chrono_niveau_mathieu,
                        descriptionArgs = listOf(seuil, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                        categorie = CategorieTrophee.DEFI_CHRONO,
                        palier = PALIERS_DEFI_CHRONO_NIVEAU_MATHIEU.getValue(seuil),
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

        // Défi Points (refonte 2026-08-27) : le score maximal atteignable en une partie est le
        // nombre d'objectifs du niveau (3 à 6, cf. `nombreObjectifsDefiPoints`) — bien plus faible
        // que pour les autres défis lettres, donc un barème dédié plutôt que réutiliser
        // SEUILS_DEFI_MOTS_MAX (qui dépasserait le score maximal possible dès le 3e palier).
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
        add(
            Trophee(
                "defi_points_complet",
                titreRes = R.string.trophee_titre_defi_points_complet,
                descriptionRes = R.string.trophee_desc_defi_points_complet,
                categorie = CategorieTrophee.DEFI_OBJECTIFS_POINTS,
                palier = Palier.OR,
            ) { it.defiObjectifsPointsComplete },
        )
        add(
            Trophee(
                "defi_points_complet_monique",
                titreRes = R.string.trophee_titre_defi_points_complet_niveau_monique,
                descriptionRes = R.string.trophee_desc_defi_points_complet_niveau_monique,
                categorie = CategorieTrophee.DEFI_OBJECTIFS_POINTS,
                palier = Palier.PLATINE,
            ) { it.defiObjectifsPointsCompleteNiveauMonique },
        )
        add(
            Trophee(
                "defi_points_complet_mathieu",
                titreRes = R.string.trophee_titre_defi_points_complet_niveau_mathieu,
                descriptionRes = R.string.trophee_desc_defi_points_complet_niveau_mathieu,
                categorie = CategorieTrophee.DEFI_OBJECTIFS_POINTS,
                palier = Palier.EMERAUDE,
            ) { it.defiObjectifsPointsCompleteNiveauMathieu },
        )

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
                titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE),
                descriptionRes = R.string.trophee_desc_defi_sans_faute_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE),
                categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                palier = Palier.PLATINE,
                objectif = SEUIL_DEFI_NIVEAU_MONIQUE,
                progression = { it.meilleureSerieSansFauteNiveauMonique },
            ) { it.meilleureSerieSansFauteNiveauMonique >= SEUIL_DEFI_NIVEAU_MONIQUE },
        )
        for (seuil in SEUILS_DEFI_NIVEAU_MATHIEU) {
            add(
                Trophee(
                    "defi_sans_faute_${seuil}_mathieu",
                    titreRes = R.string.trophee_titre_defi_sans_faute_niveau_mathieu,
                    titreArgs = listOf(seuil),
                    descriptionRes = R.string.trophee_desc_defi_sans_faute_niveau_mathieu,
                    descriptionArgs = listOf(seuil),
                    categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                    palier = PALIERS_DEFI_NIVEAU_MATHIEU.getValue(seuil),
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
            ) { it.maxPartiesMemeJour > 20 },
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
            ) { it.premierePartieEntre5et7h },
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
            ) { it.dimancheQuatreSemainesDeSuite },
        )
        add(
            Trophee(
                "easter_ex_aequo",
                titreRes = R.string.trophee_titre_easter_ex_aequo,
                descriptionRes = R.string.trophee_desc_easter_ex_aequo,
                categorie = CategorieTrophee.EASTER_GENERAL,
                palier = null,
                niveauVisibilite = NiveauVisibilite.INVISIBLE,
                descriptionAvantDeblocageRes = R.string.easter_avant_invisible,
            ) { it.egaliteDuelDejaObtenue },
        )
        add(
            Trophee(
                "easter_symetrie",
                titreRes = R.string.trophee_titre_easter_symetrie,
                descriptionRes = R.string.trophee_desc_easter_symetrie,
                categorie = CategorieTrophee.EASTER_GENERAL,
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
                categorie = CategorieTrophee.EASTER_GENERAL,
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
            ) { it.alphabetComplet },
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
                "easter_aucune_idee",
                titreRes = R.string.trophee_titre_easter_aucune_idee,
                descriptionRes = R.string.trophee_desc_easter_aucune_idee,
                categorie = CategorieTrophee.EASTER_GENERAL,
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
                categorie = CategorieTrophee.EASTER_ULTIME,
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
