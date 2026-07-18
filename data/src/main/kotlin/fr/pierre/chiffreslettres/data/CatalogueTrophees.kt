package fr.pierre.chiffreslettres.data

/**
 * Statistiques agrégées d'un joueur (parties solo + défis, jamais l'entraînement libre) : la
 * base sur laquelle chaque trophée du catalogue évalue sa condition de déblocage. Un seul aller-
 * retour vers la base (voir `TropheeRepository`) plutôt qu'une requête par trophée.
 */
data class TropheeStats(
    val comptesExacts: Int,
    val motsDixLettres: Int,
    val partieTousComptesExacts: Boolean,
    /** Clé = longueur minimale (4 à 8), valeur = une partie solo a-t-elle uniquement des mots d'au moins cette longueur. */
    val partiesMotsMin: Map<Int, Boolean>,
    /** Clé = seuil de points (20 à 90), valeur = nombre de parties solo atteignant au moins ce seuil. */
    val partiesParSeuilScore: Map<Int, Int>,
    val partiesSoloTotal: Int,
    /** Nombre de niveaux (0 à 4) avec au moins une partie solo terminée. */
    val niveauxSoloCouverts: Int,
    val defisTotal: Int,
    val meilleureSerieDefi: Int,
    /** Nombre de combinaisons niveau × mode (0 à 8) avec au moins un défi série terminé. */
    val combinaisonsDefiCouvertes: Int,
    /** Clé = "MODE_NIVEAUCODE" (ex. "CHIFFRES_EMILE"), valeur = meilleur nombre de réussites en défi chrono. */
    val meilleuresReussitesDefiChrono: Map<String, Int>,
)

enum class CategorieTrophee(val titre: String) {
    COMPTES_EXACTS("Comptes exacts"),
    MOTS("Mots"),
    PARTIE_PARFAITE("Partie parfaite"),
    SCORE_PARTIE("Score de partie"),
    PARTIES_TERMINEES("Parties terminées"),
    NIVEAUX_SOLO("Tous les niveaux"),
    DEFI("Défi"),
    NIVEAUX_DEFI("Tous les niveaux en défi"),
    DEFI_CHRONO("Défi chrono"),
}

class Trophee(
    val id: String,
    val titre: String,
    val description: String,
    val categorie: CategorieTrophee,
    /** Regroupement visuel au sein d'une catégorie (ex. niveau du défi chrono), null si non applicable. */
    val sousTitre: String? = null,
    val estDebloque: (TropheeStats) -> Boolean,
)

private val SEUILS_MOTS = listOf(4, 5, 6, 7, 8)
private val SEUILS_SCORE = listOf(20, 30, 40, 50, 60, 70, 80, 90)

/** Un niveau de défi chrono : code d'enum, libellé, durée du budget et paliers de trophées. */
private data class NiveauDefiChrono(val code: String, val label: String, val minutes: Int, val seuils: List<Int>)

private val NIVEAUX_DEFI_CHRONO = listOf(
    NiveauDefiChrono("EMILE", "Émile", 2, listOf(2, 3, 5)),
    NiveauDefiChrono("NESTOR", "Nestor", 3, listOf(2, 3, 5)),
    NiveauDefiChrono("MONIQUE", "Monique", 4, listOf(5, 10)),
    NiveauDefiChrono("MATHIEU", "Mathieu", 5, listOf(5, 10)),
)

/** Catalogue complet des trophées possibles (spec produit, retour utilisateur) : 56 au total. */
object CatalogueTrophees {

    val TOUS: List<Trophee> = buildList {
        add(
            Trophee(
                "compte_exact_1",
                "Premier compte exact",
                "Obtenir un compte exact en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
            ) { it.comptesExacts >= 1 },
        )
        add(
            Trophee(
                "compte_exact_10",
                "Dixième compte exact",
                "Obtenir 10 comptes exacts en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
            ) { it.comptesExacts >= 10 },
        )
        add(
            Trophee(
                "compte_exact_100",
                "Centième compte exact",
                "Obtenir 100 comptes exacts en chiffres, en partie solo.",
                CategorieTrophee.COMPTES_EXACTS,
            ) { it.comptesExacts >= 100 },
        )

        add(
            Trophee(
                "mot_10_1",
                "Premier mot de 10 lettres",
                "Trouver un mot de 10 lettres (la longueur maximale du tirage), en partie solo.",
                CategorieTrophee.MOTS,
            ) { it.motsDixLettres >= 1 },
        )
        add(
            Trophee(
                "mot_10_10",
                "Dixième mot de 10 lettres",
                "Trouver 10 mots de 10 lettres, en partie solo.",
                CategorieTrophee.MOTS,
            ) { it.motsDixLettres >= 10 },
        )

        add(
            Trophee(
                "partie_parfaite_chiffres",
                "Tous les comptes exacts dans une partie",
                "Terminer une partie solo où toutes les manches chiffres ont un compte exact.",
                CategorieTrophee.PARTIE_PARFAITE,
            ) { it.partieTousComptesExacts },
        )
        for (seuil in SEUILS_MOTS) {
            add(
                Trophee(
                    "partie_mots_min_$seuil",
                    "Que des mots de $seuil lettres ou plus dans une partie",
                    "Terminer une partie solo où toutes les manches lettres ont un mot valide d'au moins $seuil lettres.",
                    CategorieTrophee.PARTIE_PARFAITE,
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
                ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 1 },
            )
            add(
                Trophee(
                    "score_${seuil}_10",
                    "Dixième partie à au moins $seuil points",
                    "Terminer 10 parties solo avec au moins $seuil points.",
                    CategorieTrophee.SCORE_PARTIE,
                ) { (it.partiesParSeuilScore[seuil] ?: 0) >= 10 },
            )
        }

        add(
            Trophee(
                "parties_1",
                "Première partie terminée",
                "Terminer une partie solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
            ) { it.partiesSoloTotal >= 1 },
        )
        add(
            Trophee(
                "parties_10",
                "Dixième partie terminée",
                "Terminer 10 parties solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
            ) { it.partiesSoloTotal >= 10 },
        )
        add(
            Trophee(
                "parties_100",
                "Centième partie terminée",
                "Terminer 100 parties solo, tous niveaux confondus.",
                CategorieTrophee.PARTIES_TERMINEES,
            ) { it.partiesSoloTotal >= 100 },
        )

        add(
            Trophee(
                "niveaux_solo_complets",
                "Un niveau terminé partout",
                "Terminer au moins une partie solo dans chacun des 4 niveaux (Émile, Nestor, Monique, Mathieu).",
                CategorieTrophee.NIVEAUX_SOLO,
            ) { it.niveauxSoloCouverts >= 4 },
        )

        add(
            Trophee(
                "defi_1",
                "Premier défi terminé",
                "Aller jusqu'au bout d'un défi (chiffres ou lettres, tout niveau).",
                CategorieTrophee.DEFI,
            ) { it.defisTotal >= 1 },
        )
        add(
            Trophee(
                "defi_serie_3",
                "Série de 3 en défi",
                "Aligner 3 réussites ou plus d'affilée dans un même défi.",
                CategorieTrophee.DEFI,
            ) { it.meilleureSerieDefi >= 3 },
        )
        add(
            Trophee(
                "defi_serie_5",
                "Série de 5 en défi",
                "Aligner 5 réussites ou plus d'affilée dans un même défi.",
                CategorieTrophee.DEFI,
            ) { it.meilleureSerieDefi >= 5 },
        )
        add(
            Trophee(
                "defi_serie_10",
                "Série de 10 en défi",
                "Aligner 10 réussites ou plus d'affilée dans un même défi.",
                CategorieTrophee.DEFI,
            ) { it.meilleureSerieDefi >= 10 },
        )

        add(
            Trophee(
                "defi_niveaux_complets",
                "Un défi terminé partout",
                "Terminer un défi chiffres et un défi lettres pour chacun des 4 niveaux (8 défis au total).",
                CategorieTrophee.NIVEAUX_DEFI,
            ) { it.combinaisonsDefiCouvertes >= 8 },
        )

        for (niveau in NIVEAUX_DEFI_CHRONO) {
            for (mode in ModeJeu.entries) {
                val nature = if (mode == ModeJeu.CHIFFRES) "comptes exacts" else "mots"
                val modeCode = mode.name.lowercase()
                val cle = "${mode.name}_${niveau.code}"
                for (seuil in niveau.seuils) {
                    add(
                        Trophee(
                            "defi_chrono_${modeCode}_${niveau.code.lowercase()}_$seuil",
                            "$seuil $nature en moins de ${niveau.minutes} min",
                            "Obtenir $seuil $nature en défi chrono $modeCode, niveau ${niveau.label} " +
                                "(${niveau.minutes} minute(s)).",
                            CategorieTrophee.DEFI_CHRONO,
                            sousTitre = niveau.label,
                        ) { (it.meilleuresReussitesDefiChrono[cle] ?: 0) >= seuil },
                    )
                }
            }
        }
    }
}
