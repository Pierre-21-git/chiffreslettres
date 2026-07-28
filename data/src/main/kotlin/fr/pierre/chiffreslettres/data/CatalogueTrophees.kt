package fr.pierre.chiffreslettres.data

/**
 * Statistiques agrégées d'un joueur (parties solo + défis, jamais l'entraînement libre) : la
 * base sur laquelle chaque trophée du catalogue évalue sa condition de déblocage. Un seul aller-
 * retour vers la base (voir `TropheeRepository`) plutôt qu'une requête par trophée.
 */
data class TropheeStats(
    val comptesExacts: Int,
    /** Clé = longueur exacte du mot (4 à 10), valeur = nombre de fois où un mot de cette longueur a été trouvé, en partie solo. */
    val motsParLongueur: Map<Int, Int>,
    val partieTousComptesExacts: Boolean,
    /** Clé = longueur minimale (4 à 8), valeur = une partie solo a-t-elle uniquement des mots d'au moins cette longueur. */
    val partiesMotsMin: Map<Int, Boolean>,
    /** Clé = seuil de points (20 à 90), valeur = nombre de parties solo atteignant au moins ce seuil. */
    val partiesParSeuilScore: Map<Int, Int>,
    val partiesSoloTotal: Int,
    val partiesDuoJouees: Int,
    val partiesDuoGagnees: Int,
    val partiesConfrontationJouees: Int,
    val partiesConfrontationGagnees: Int,
    val defisTotal: Int,
    /** Clé = nom du [ModeJeu] (ex. "CHIFFRES"), valeur = meilleure série en défi série, tous niveaux confondus. */
    val meilleuresSeriesDefi: Map<String, Int>,
    /** Clé = nom du [ModeJeu] (ex. "CHIFFRES"), valeur = meilleur nombre de réussites en défi chrono, tous niveaux confondus. */
    val meilleuresReussitesDefiChrono: Map<String, Int>,
    /** Plus longue série de jours consécutifs avec le défi quotidien réussi. */
    val meilleureSerieJoursDefiQuotidien: Int,
)

/** Palier de difficulté d'un trophée (retour utilisateur), du plus facile au plus rare. */
enum class Palier { BRONZE, ARGENT, OR, PLATINE, DIAMANT }

enum class CategorieTrophee(val titre: String) {
    PARTIES_TERMINEES("Parties terminées"),
    SCORE_PARTIE("Score de partie"),
    COMPTES_EXACTS("Comptes exacts"),
    PARTIE_PARFAITE("Partie parfaite"),
    MOTS("Mots"),
    DUO("Partie duo"),
    DEFI("Défi"),
    DEFI_CHRONO("Défi chrono"),
    DEFI_QUOTIDIEN("Défi quotidien"),
}

class Trophee(
    val id: String,
    val titre: String,
    val description: String,
    val categorie: CategorieTrophee,
    val palier: Palier,
    /** Regroupement visuel au sein d'une catégorie (ex. niveau du défi chrono), null si non applicable. */
    val sousTitre: String? = null,
    val estDebloque: (TropheeStats) -> Boolean,
)

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)
private val SEUILS_DEFI_SERIE = listOf(3, 5, 10, 15, 20, 30, 50)
private val SEUILS_DEFI_CHRONO = listOf(2, 3, 5, 10, 12, 15)
private val LONGUEURS_MOTS_TROPHEE = 4..10
private val SEUILS_DEFI_QUOTIDIEN = listOf(7, 30)

// Paliers choisis par l'utilisateur (retour utilisateur, fichier trophees.txt réorganisé à la main).
private val PALIERS_PARTIE_MOTS_MIN = mapOf(4 to Palier.BRONZE, 5 to Palier.ARGENT, 6 to Palier.ARGENT, 7 to Palier.OR, 8 to Palier.DIAMANT)
private val PALIERS_MOTS_1 = mapOf(
    4 to Palier.BRONZE, 5 to Palier.BRONZE, 6 to Palier.ARGENT, 7 to Palier.ARGENT,
    8 to Palier.OR, 9 to Palier.OR, 10 to Palier.PLATINE,
)
private val PALIERS_MOTS_10 = mapOf(
    4 to Palier.ARGENT, 5 to Palier.ARGENT, 6 to Palier.ARGENT, 7 to Palier.OR,
    8 to Palier.OR, 9 to Palier.PLATINE, 10 to Palier.DIAMANT,
)
private val PALIERS_SCORE_1 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.OR, 70 to Palier.OR, 80 to Palier.OR, 90 to Palier.PLATINE,
)
private val PALIERS_SCORE_10 = mapOf(
    20 to Palier.BRONZE, 30 to Palier.ARGENT, 40 to Palier.ARGENT, 50 to Palier.OR,
    60 to Palier.OR, 70 to Palier.OR, 80 to Palier.PLATINE, 90 to Palier.DIAMANT,
)
private val PALIERS_DEFI_SERIE = mapOf(
    3 to Palier.BRONZE, 5 to Palier.ARGENT, 10 to Palier.ARGENT, 15 to Palier.OR,
    20 to Palier.OR, 30 to Palier.PLATINE, 50 to Palier.DIAMANT,
)
private val PALIERS_DEFI_CHRONO = mapOf(
    2 to Palier.BRONZE, 3 to Palier.BRONZE, 5 to Palier.ARGENT, 10 to Palier.OR,
    12 to Palier.OR, 15 to Palier.PLATINE,
)
private val PALIERS_DEFI_QUOTIDIEN = mapOf(7 to Palier.ARGENT, 30 to Palier.OR)

/** Catalogue complet des trophées possibles (spec produit, retour utilisateur) : 71 au total. */
object CatalogueTrophees {

    val TOUS: List<Trophee> = buildList {
        add(
            Trophee(
                "compte_exact_1",
                "Premier compte exact",
                "Obtenir un compte exact en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.BRONZE,
            ) { it.comptesExacts >= 1 },
        )
        add(
            Trophee(
                "compte_exact_10",
                "Dixième compte exact",
                "Obtenir 10 comptes exacts en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.ARGENT,
            ) { it.comptesExacts >= 10 },
        )
        add(
            Trophee(
                "compte_exact_100",
                "Centième compte exact",
                "Obtenir 100 comptes exacts en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
                palier = Palier.OR,
            ) { it.comptesExacts >= 100 },
        )

        for (longueur in LONGUEURS_MOTS_TROPHEE) {
            val precision = if (longueur == 10) " (la longueur maximale du tirage)" else ""
            add(
                Trophee(
                    "mot_${longueur}_1",
                    "Premier mot de $longueur lettres",
                    "Trouver un mot de $longueur lettres$precision, en partie solo.",
                    CategorieTrophee.MOTS,
                    palier = PALIERS_MOTS_1.getValue(longueur),
                ) { (it.motsParLongueur[longueur] ?: 0) >= 1 },
            )
            add(
                Trophee(
                    "mot_${longueur}_10",
                    "Dixième mot de $longueur lettres",
                    "Trouver 10 mots de $longueur lettres, en partie solo.",
                    CategorieTrophee.MOTS,
                    palier = PALIERS_MOTS_10.getValue(longueur),
                ) { (it.motsParLongueur[longueur] ?: 0) >= 10 },
            )
        }

        add(
            Trophee(
                "partie_parfaite_chiffres",
                "Tous les comptes exacts dans une partie",
                "Terminer une partie solo où toutes les manches chiffres ont un compte exact.",
                CategorieTrophee.PARTIE_PARFAITE,
                palier = Palier.ARGENT,
            ) { it.partieTousComptesExacts },
        )
        for (seuil in SEUILS_MOTS) {
            add(
                Trophee(
                    "partie_mots_min_$seuil",
                    "Que des mots de $seuil lettres ou plus dans une partie",
                    "Terminer une partie solo où toutes les manches lettres ont un mot valide d'au moins $seuil lettres.",
                    CategorieTrophee.PARTIE_PARFAITE,
                    palier = PALIERS_PARTIE_MOTS_MIN.getValue(seuil),
                ) { it.partiesMotsMin[seuil] == true },
            )
        }

        for (seuil in SEUILS_SCORE) {
            add(
                Trophee(
                    "score_${seuil}_1",
                    "Première partie à au moins $seuil points",
                    "Terminer une partie solo avec au moins $seuil points.",
                    CategorieTrophee.SCORE_PARTIE,
                    palier = PALIERS_SCORE_1.getValue(seuil),
                ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 1 },
            )
            add(
                Trophee(
                    "score_${seuil}_10",
                    "Dixième partie à au moins $seuil points",
                    "Terminer 10 parties solo avec au moins $seuil points.",
                    CategorieTrophee.SCORE_PARTIE,
                    palier = PALIERS_SCORE_10.getValue(seuil),
                ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 10 },
            )
        }

        add(
            Trophee(
                "parties_1",
                "Première partie terminée",
                "Terminer une partie solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.BRONZE,
            ) { it.partiesSoloTotal >= 1 },
        )
        add(
            Trophee(
                "parties_10",
                "Dixième partie terminée",
                "Terminer 10 parties solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.ARGENT,
            ) { it.partiesSoloTotal >= 10 },
        )
        add(
            Trophee(
                "parties_100",
                "Centième partie terminée",
                "Terminer 100 parties solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
                palier = Palier.OR,
            ) { it.partiesSoloTotal >= 100 },
        )

        add(
            Trophee(
                "duo_1",
                "Première partie duo jouée",
                "Terminer une partie en mode Duo, tous niveaux confondus.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Duo",
            ) { it.partiesDuoJouees >= 1 },
        )
        add(
            Trophee(
                "duo_gagnee_1",
                "Première partie duo gagnée",
                "Gagner une partie en mode Duo.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Duo",
            ) { it.partiesDuoGagnees >= 1 },
        )
        add(
            Trophee(
                "duo_gagnee_10",
                "Dixième partie duo gagnée",
                "Gagner 10 parties en mode Duo.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Duo",
            ) { it.partiesDuoGagnees >= 10 },
        )
        add(
            Trophee(
                "confrontation_1",
                "Première confrontation jouée",
                "Terminer une partie en mode Confrontation, tous niveaux confondus.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Confrontation",
            ) { it.partiesConfrontationJouees >= 1 },
        )
        add(
            Trophee(
                "confrontation_gagnee_1",
                "Première confrontation gagnée",
                "Gagner une partie en mode Confrontation.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Confrontation",
            ) { it.partiesConfrontationGagnees >= 1 },
        )
        add(
            Trophee(
                "confrontation_gagnee_10",
                "Dixième confrontation gagnée",
                "Gagner 10 parties en mode Confrontation.",
                CategorieTrophee.DUO,
                palier = Palier.OR,
                sousTitre = "Confrontation",
            ) { it.partiesConfrontationGagnees >= 10 },
        )

        add(
            Trophee(
                "defi_1",
                "Premier défi terminé",
                "Aller jusqu'au bout d'un défi (chiffres ou lettres, tout niveau).",
                CategorieTrophee.DEFI,
                palier = Palier.BRONZE,
            ) { it.defisTotal >= 1 },
        )
        for (mode in ModeJeu.entries) {
            val modeCode = mode.name.lowercase()
            val sousTitreMode = if (mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
            for (seuil in SEUILS_DEFI_SERIE) {
                add(
                    Trophee(
                        "defi_serie_${modeCode}_$seuil",
                        "Série de $seuil en défi $modeCode",
                        "Aligner $seuil réussites ou plus d'affilée dans un même défi $modeCode.",
                        CategorieTrophee.DEFI,
                        palier = PALIERS_DEFI_SERIE.getValue(seuil),
                        sousTitre = sousTitreMode,
                    ) { (it.meilleuresSeriesDefi[mode.name] ?: 0) >= seuil },
                )
            }
        }

        for (mode in ModeJeu.entries) {
            val nature = if (mode == ModeJeu.CHIFFRES) "comptes exacts" else "mots"
            val modeCode = mode.name.lowercase()
            val sousTitreMode = if (mode == ModeJeu.CHIFFRES) "Chiffres" else "Lettres"
            for (seuil in SEUILS_DEFI_CHRONO) {
                add(
                    Trophee(
                        "defi_chrono_${modeCode}_$seuil",
                        "$seuil $nature en défi chrono",
                        "Obtenir $seuil $nature en défi chrono $modeCode, tous niveaux confondus.",
                        CategorieTrophee.DEFI_CHRONO,
                        palier = PALIERS_DEFI_CHRONO.getValue(seuil),
                        sousTitre = sousTitreMode,
                    ) { (it.meilleuresReussitesDefiChrono[mode.name] ?: 0) >= seuil },
                )
            }
        }

        val libellesPaliersQuotidien = mapOf(7 to "Une semaine", 30 to "Un mois")
        for (seuil in SEUILS_DEFI_QUOTIDIEN) {
            add(
                Trophee(
                    "defi_quotidien_$seuil",
                    "${libellesPaliersQuotidien.getValue(seuil)} de défi quotidien",
                    "Réussir le défi quotidien $seuil jours d'affilée.",
                    CategorieTrophee.DEFI_QUOTIDIEN,
                    palier = PALIERS_DEFI_QUOTIDIEN.getValue(seuil),
                ) { it.meilleureSerieJoursDefiQuotidien >= seuil },
            )
        }
    }
}
