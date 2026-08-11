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
    val defisTotal: Int,
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
)

/** Palier de difficulté d'un trophée (retour utilisateur), du plus facile au plus rare. */
enum class Palier { BRONZE, ARGENT, OR, PLATINE, DIAMANT }

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
        Palier.DIAMANT -> R.string.palier_joueur_diamant
    }

/** Libellé court de la difficulté d'un trophée (retour utilisateur, ex. "Bronze"), affiché dans son détail. */
val Palier.libelleCourtRes: Int
    get() = when (this) {
        Palier.BRONZE -> R.string.palier_court_bronze
        Palier.ARGENT -> R.string.palier_court_argent
        Palier.OR -> R.string.palier_court_or
        Palier.PLATINE -> R.string.palier_court_platine
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
    DEFI_SANS_FAUTE(R.string.categorie_defi_sans_faute),
    DEFI_QUOTIDIEN(R.string.categorie_defi_quotidien),
}

/**
 * Argument de format (titreArgs/descriptionArgs de [Trophee]) qui est lui-même un ID de
 * ressource string à résoudre avant substitution (ex. nom de mode "chiffres"/"lettres") — ce
 * module n'a pas accès à Compose pour le résoudre lui-même, donc l'UI doit le faire (voir
 * `TropheesScreen.kt`) avant d'appeler `stringResource(titreRes, *args)`.
 */
data class ArgRes(val res: Int)

class Trophee(
    val id: String,
    val titreRes: Int,
    val titreArgs: List<Any> = emptyList(),
    val descriptionRes: Int,
    val descriptionArgs: List<Any> = emptyList(),
    val categorie: CategorieTrophee,
    val palier: Palier,
    /** Regroupement visuel au sein d'une catégorie (ex. niveau du défi chrono), null si non applicable. */
    val sousTitreRes: Int? = null,
    /**
     * Objectif numérique affiché "X / objectif" dans le détail du trophée (retour utilisateur),
     * null si le trophée n'a pas de progression chiffrée (ex. partie parfaite, tout ou rien).
     */
    val objectif: Int? = null,
    /** Valeur courante du joueur pour [objectif] (retour utilisateur), null si non applicable. */
    val progression: ((TropheeStats) -> Int)? = null,
    val estDebloque: (TropheeStats) -> Boolean,
)

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
/** Seuils "partie parfaite" (lettres) qui exigent en plus le niveau Mathieu (retour utilisateur). */
private val SEUILS_MOTS_NIVEAU_MATHIEU = listOf(7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
// Barème (retour utilisateur) de série/chrono/sans-faute : 3/5/8 tous niveaux (Bronze/Argent/Or),
// puis 2 jalons supplémentaires (Platine/Diamant) gagnés par le niveau atteint, chacun avec son
// propre seuil de comptage (cf. SEUIL_DEFI_NIVEAU_MONIQUE / SEUIL_DEFI_NIVEAU_MATHIEU).
private val SEUILS_DEFI_SERIE = listOf(3, 5, 8)
private val SEUILS_DEFI_CHRONO = listOf(3, 5, 8)
private val SEUILS_DEFI_SANS_FAUTE = listOf(3, 5, 8)
/** Seuils des jalons "niveau" (Platine à Monique+, Diamant à Mathieu) de série/chrono/sans-faute. */
private const val SEUIL_DEFI_NIVEAU_MONIQUE = 10
private const val SEUIL_DEFI_NIVEAU_MATHIEU = 12
// Barème (retour utilisateur) du défi mots : 3/5/10 tous niveaux (Bronze/Argent/Or), puis 2 jalons
// "niveau" à seuils propres (plus élevés que les autres familles de défi).
private val SEUILS_DEFI_MOTS_MAX = listOf(3, 5, 10)
private const val SEUIL_DEFI_MOTS_MAX_NIVEAU_MONIQUE = 15
private const val SEUIL_DEFI_MOTS_MAX_NIVEAU_MATHIEU = 20
private val LONGUEURS_MOTS_TROPHEE = 4..10
private val SEUILS_DEFI_QUOTIDIEN = listOf(7, 14, 30)
/** Seuil du jalon "niveau" (Platine à Monique+, Diamant à Mathieu) du défi quotidien. */
private const val SEUIL_DEFI_QUOTIDIEN_NIVEAU = 30

// Paliers choisis par l'utilisateur (retour utilisateur, fichier trophees.txt réorganisé à la main).
private val PALIERS_PARTIE_MOTS_MIN = mapOf(4 to Palier.BRONZE, 5 to Palier.ARGENT, 6 to Palier.OR, 7 to Palier.PLATINE, 8 to Palier.DIAMANT)
private val PALIERS_MOTS_1 = mapOf(
    4 to Palier.BRONZE, 5 to Palier.BRONZE, 6 to Palier.ARGENT, 7 to Palier.ARGENT,
    8 to Palier.OR, 9 to Palier.PLATINE, 10 to Palier.PLATINE,
)
private val PALIERS_MOTS_10 = mapOf(
    4 to Palier.ARGENT, 5 to Palier.ARGENT, 6 to Palier.ARGENT, 7 to Palier.OR,
    8 to Palier.PLATINE, 9 to Palier.PLATINE, 10 to Palier.DIAMANT,
)
private val PALIERS_SCORE_1 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.OR, 70 to Palier.OR, 80 to Palier.PLATINE, 90 to Palier.PLATINE,
)
private val PALIERS_SCORE_10 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.OR, 70 to Palier.PLATINE, 80 to Palier.PLATINE, 90 to Palier.DIAMANT,
)
// Barème (retour utilisateur) de série/chrono/sans-faute : 3=Bronze, 5=Argent, 8=Or.
private val PALIERS_DEFI_UNIFIE = mapOf(3 to Palier.BRONZE, 5 to Palier.ARGENT, 8 to Palier.OR)
private val PALIERS_DEFI_SERIE = PALIERS_DEFI_UNIFIE
private val PALIERS_DEFI_CHRONO = PALIERS_DEFI_UNIFIE
private val PALIERS_DEFI_SANS_FAUTE = PALIERS_DEFI_UNIFIE
// Barème (retour utilisateur) du défi mots : 3=Bronze, 5=Argent, 10=Or.
private val PALIERS_DEFI_MOTS_MAX = mapOf(3 to Palier.BRONZE, 5 to Palier.ARGENT, 10 to Palier.OR)
private val PALIERS_DEFI_QUOTIDIEN = mapOf(7 to Palier.BRONZE, 14 to Palier.ARGENT, 30 to Palier.OR)

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
                palier = Palier.DIAMANT,
                objectif = 200,
                progression = { it.comptesExacts },
            ) { it.comptesExacts >= 200 },
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
                "parties_200",
                titreRes = R.string.trophee_titre_parties_200,
                descriptionRes = R.string.trophee_desc_parties_200,
                categorie = CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.DIAMANT,
                objectif = 200,
                progression = { it.partiesSoloTotal },
            ) { it.partiesSoloTotal >= 200 },
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
                "defi_1",
                titreRes = R.string.trophee_titre_defi_1,
                descriptionRes = R.string.trophee_desc_defi_1,
                categorie = CategorieTrophee.DEFI,
                palier = Palier.BRONZE,
                objectif = 1,
                progression = { it.defisTotal },
            ) { it.defisTotal >= 1 },
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
                        palier = PALIERS_DEFI_SERIE.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresSeriesDefi[mode.name] ?: 0) },
                    ) { (it.meilleuresSeriesDefi[mode.name] ?: 0) >= seuil },
                )
            }
            // Retour utilisateur : au-delà de Or, le palier dépend du niveau où la série a été
            // réalisée, avec un seuil de comptage propre à chacun (Platine/Monique+ < Diamant/Mathieu).
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
            add(
                Trophee(
                    "defi_serie_${modeCode}_12_mathieu",
                    titreRes = R.string.trophee_titre_defi_serie_niveau_mathieu,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU, ArgRes(modeMinusculeRes)),
                    descriptionRes = R.string.trophee_desc_defi_serie_niveau_mathieu,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU, ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI,
                    palier = Palier.DIAMANT,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MATHIEU,
                    progression = { (it.meilleuresSeriesDefiNiveauMathieu[mode.name] ?: 0) },
                ) { (it.meilleuresSeriesDefiNiveauMathieu[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MATHIEU },
            )
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
                        palier = PALIERS_DEFI_CHRONO.getValue(seuil),
                        sousTitreRes = sousTitreModeRes,
                        objectif = seuil,
                        progression = { (it.meilleuresReussitesDefiChrono[mode.name] ?: 0) },
                    ) { (it.meilleuresReussitesDefiChrono[mode.name] ?: 0) >= seuil },
                )
            }
            add(
                Trophee(
                    "defi_chrono_${modeCode}_10_monique",
                    titreRes = R.string.trophee_titre_defi_chrono_niveau_monique,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE, ArgRes(natureRes)),
                    descriptionRes = R.string.trophee_desc_defi_chrono_niveau_monique,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MONIQUE, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI_CHRONO,
                    palier = Palier.PLATINE,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MONIQUE,
                    progression = { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) },
                ) { (it.meilleuresReussitesDefiChronoNiveauMonique[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MONIQUE },
            )
            add(
                Trophee(
                    "defi_chrono_${modeCode}_12_mathieu",
                    titreRes = R.string.trophee_titre_defi_chrono_niveau_mathieu,
                    titreArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU, ArgRes(natureRes)),
                    descriptionRes = R.string.trophee_desc_defi_chrono_niveau_mathieu,
                    descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU, ArgRes(natureRes), ArgRes(modeMinusculeRes)),
                    categorie = CategorieTrophee.DEFI_CHRONO,
                    palier = Palier.DIAMANT,
                    sousTitreRes = sousTitreModeRes,
                    objectif = SEUIL_DEFI_NIVEAU_MATHIEU,
                    progression = { (it.meilleuresReussitesDefiChronoNiveauMathieu[mode.name] ?: 0) },
                ) { (it.meilleuresReussitesDefiChronoNiveauMathieu[mode.name] ?: 0) >= SEUIL_DEFI_NIVEAU_MATHIEU },
            )
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
                    palier = PALIERS_DEFI_MOTS_MAX.getValue(seuil),
                    objectif = seuil,
                    progression = { it.meilleurScoreDefiMotsMax },
                ) { it.meilleurScoreDefiMotsMax >= seuil },
            )
        }
        add(
            Trophee(
                "defi_mots_max_15_monique",
                titreRes = R.string.trophee_titre_defi_mots_max_niveau_monique,
                titreArgs = listOf(SEUIL_DEFI_MOTS_MAX_NIVEAU_MONIQUE),
                descriptionRes = R.string.trophee_desc_defi_mots_max_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_MOTS_MAX_NIVEAU_MONIQUE),
                categorie = CategorieTrophee.DEFI_MOTS_MAX,
                palier = Palier.PLATINE,
                objectif = SEUIL_DEFI_MOTS_MAX_NIVEAU_MONIQUE,
                progression = { it.meilleurScoreDefiMotsMaxNiveauMonique },
            ) { it.meilleurScoreDefiMotsMaxNiveauMonique >= SEUIL_DEFI_MOTS_MAX_NIVEAU_MONIQUE },
        )
        add(
            Trophee(
                "defi_mots_max_20_mathieu",
                titreRes = R.string.trophee_titre_defi_mots_max_niveau_mathieu,
                titreArgs = listOf(SEUIL_DEFI_MOTS_MAX_NIVEAU_MATHIEU),
                descriptionRes = R.string.trophee_desc_defi_mots_max_niveau_mathieu,
                descriptionArgs = listOf(SEUIL_DEFI_MOTS_MAX_NIVEAU_MATHIEU),
                categorie = CategorieTrophee.DEFI_MOTS_MAX,
                palier = Palier.DIAMANT,
                objectif = SEUIL_DEFI_MOTS_MAX_NIVEAU_MATHIEU,
                progression = { it.meilleurScoreDefiMotsMaxNiveauMathieu },
            ) { it.meilleurScoreDefiMotsMaxNiveauMathieu >= SEUIL_DEFI_MOTS_MAX_NIVEAU_MATHIEU },
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
                    palier = PALIERS_DEFI_SANS_FAUTE.getValue(seuil),
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
        add(
            Trophee(
                "defi_sans_faute_12_mathieu",
                titreRes = R.string.trophee_titre_defi_sans_faute_niveau_mathieu,
                titreArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU),
                descriptionRes = R.string.trophee_desc_defi_sans_faute_niveau_mathieu,
                descriptionArgs = listOf(SEUIL_DEFI_NIVEAU_MATHIEU),
                categorie = CategorieTrophee.DEFI_SANS_FAUTE,
                palier = Palier.DIAMANT,
                objectif = SEUIL_DEFI_NIVEAU_MATHIEU,
                progression = { it.meilleureSerieSansFauteNiveauMathieu },
            ) { it.meilleureSerieSansFauteNiveauMathieu >= SEUIL_DEFI_NIVEAU_MATHIEU },
        )

        val titresPaliersQuotidien = mapOf(
            7 to R.string.trophee_titre_defi_quotidien_semaine,
            14 to R.string.trophee_titre_defi_quotidien_deux_semaines,
            30 to R.string.trophee_titre_defi_quotidien_mois,
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
                    progression = { it.meilleureSerieJoursDefiQuotidien },
                ) { it.meilleureSerieJoursDefiQuotidien >= seuil },
            )
        }
        add(
            Trophee(
                "defi_quotidien_30_monique",
                titreRes = R.string.trophee_titre_defi_quotidien_niveau_monique,
                titreArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU),
                descriptionRes = R.string.trophee_desc_defi_quotidien_niveau_monique,
                descriptionArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU),
                categorie = CategorieTrophee.DEFI_QUOTIDIEN,
                palier = Palier.PLATINE,
                objectif = SEUIL_DEFI_QUOTIDIEN_NIVEAU,
                progression = { it.meilleureSerieJoursDefiQuotidienNiveauMonique },
            ) { it.meilleureSerieJoursDefiQuotidienNiveauMonique >= SEUIL_DEFI_QUOTIDIEN_NIVEAU },
        )
        add(
            Trophee(
                "defi_quotidien_30_mathieu",
                titreRes = R.string.trophee_titre_defi_quotidien_niveau_mathieu,
                titreArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU),
                descriptionRes = R.string.trophee_desc_defi_quotidien_niveau_mathieu,
                descriptionArgs = listOf(SEUIL_DEFI_QUOTIDIEN_NIVEAU),
                categorie = CategorieTrophee.DEFI_QUOTIDIEN,
                palier = Palier.DIAMANT,
                objectif = SEUIL_DEFI_QUOTIDIEN_NIVEAU,
                progression = { it.meilleureSerieJoursDefiQuotidienNiveauMathieu },
            ) { it.meilleureSerieJoursDefiQuotidienNiveauMathieu >= SEUIL_DEFI_QUOTIDIEN_NIVEAU },
        )
    }

    /**
     * Rang global d'un joueur (retour utilisateur) : le palier le plus haut dont TOUS les
     * trophées de ce palier ET des paliers inférieurs sont débloqués — cumulatif, pas juste le
     * palier isolé. Null si même le bronze n'est pas complet.
     */
    fun rangJoueur(idsDebloques: Set<String>): Palier? {
        var rang: Palier? = null
        for (palier in Palier.entries) {
            val complet = TOUS.filter { it.palier.ordinal <= palier.ordinal }.all { it.id in idsDebloques }
            if (!complet) break
            rang = palier
        }
        return rang
    }
}
