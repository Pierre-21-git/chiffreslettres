package fr.pierre.chiffreslettres.ui.navigation

import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

object Routes {
    const val MENU = "menu"
    const val ENTRAINEMENT_GRAPH = "entrainement"
    const val CHOIX_NIVEAU_ENTRAINEMENT = "entrainement/choixNiveau"
    const val ARG_NIVEAU = "niveau"
    const val JEU_CHIFFRES_PATTERN = "entrainement/jeuChiffres/{$ARG_NIVEAU}"
    const val JEU_LETTRES_PATTERN = "entrainement/jeuLettres/{$ARG_NIVEAU}"

    fun jeuChiffres(niveau: Niveau) = "entrainement/jeuChiffres/${niveau.name}"
    fun jeuLettres(niveau: NiveauLettres) = "entrainement/jeuLettres/${niveau.name}"

    const val CHANGER_PROFIL = "profil/changer"
    const val CREER_PROFIL = "profil/creer"

    const val ARG_PROFIL_ID = "profilId"
    const val STATISTIQUES_JOUEUR_PATTERN = "statistiques/joueur/{$ARG_PROFIL_ID}"
    const val MES_STATISTIQUES_PATTERN = "statistiques/joueur/{$ARG_PROFIL_ID}/mesStatistiques"
    const val STATISTIQUES_GENERALES = "statistiques/generales"
    const val TROPHEES_JOUEUR_PATTERN = "statistiques/trophees/{$ARG_PROFIL_ID}"
    const val TROPHEES_CATALOGUE = "apropos/trophees"

    fun statistiquesJoueur(profilId: Long) = "statistiques/joueur/$profilId"
    fun mesStatistiques(profilId: Long) = "statistiques/joueur/$profilId/mesStatistiques"
    fun tropheesJoueur(profilId: Long) = "statistiques/trophees/$profilId"

    const val PARTIE_GRAPH = "partie"
    const val CONFIGURATION_PARTIE = "partie/configuration"
    const val JEU_PARTIE = "partie/jeu"
    const val RECAP_PARTIE = "partie/recap"

    const val PARTIE_DUO_GRAPH = "partieDuo"
    const val CONFIGURATION_PARTIE_DUO = "partieDuo/configuration"
    const val JEU_PARTIE_DUO = "partieDuo/jeu"
    const val RECAP_PARTIE_DUO = "partieDuo/recap"

    const val RESEAU_GRAPH = "reseau"
    const val CHOIX_ROLE_RESEAU = "reseau/choixRole"
    const val HOTE_ATTENTE_RESEAU = "reseau/hote/attente"
    const val INVITE_RECHERCHE_RESEAU = "reseau/invite/recherche"
    const val RESEAU_CONNEXION = "reseau/connexion"
    const val CONFIGURATION_PARTIE_RESEAU = "reseau/configuration"
    const val JEU_PARTIE_RESEAU = "reseau/jeu"
    const val RECAP_PARTIE_RESEAU = "reseau/recap"

    const val CHOIX_DEFI_SERIE = "defi/choixNiveauSerie"
    const val CHOIX_DEFI_CHRONO = "defi/choixNiveauChrono"
    const val CHOIX_DEFI_MOTS_MAX = "defi/choixNiveauMotsMax"
    const val CHOIX_DEFI_QUOTIDIEN = "defi/quotidien"

    // Arguments optionnels portés par les 4 routes de jeu défi ci-dessous, uniquement
    // renseignés quand on vient du défi quotidien (retour utilisateur) : permettent de
    // vérifier l'objectif du jour à la fin du défi sans dupliquer les routes/écrans.
    const val ARG_OBJECTIF_QUOTIDIEN = "objectifQuotidien"
    const val ARG_JOUR_QUOTIDIEN = "jourQuotidien"
    private const val SUFFIXE_QUOTIDIEN = "?$ARG_OBJECTIF_QUOTIDIEN={$ARG_OBJECTIF_QUOTIDIEN}&$ARG_JOUR_QUOTIDIEN={$ARG_JOUR_QUOTIDIEN}"

    const val JEU_DEFI_CHIFFRES_PATTERN = "defi/jeuChiffres/{$ARG_NIVEAU}$SUFFIXE_QUOTIDIEN"
    const val JEU_DEFI_LETTRES_PATTERN = "defi/jeuLettres/{$ARG_NIVEAU}$SUFFIXE_QUOTIDIEN"
    const val JEU_DEFI_CHRONO_CHIFFRES_PATTERN = "defi/chrono/jeuChiffres/{$ARG_NIVEAU}$SUFFIXE_QUOTIDIEN"
    const val JEU_DEFI_CHRONO_LETTRES_PATTERN = "defi/chrono/jeuLettres/{$ARG_NIVEAU}$SUFFIXE_QUOTIDIEN"

    private fun suffixeQuotidien(objectifQuotidien: Int?, jourQuotidien: String?): String =
        if (objectifQuotidien != null && jourQuotidien != null) "?$ARG_OBJECTIF_QUOTIDIEN=$objectifQuotidien&$ARG_JOUR_QUOTIDIEN=$jourQuotidien" else ""

    fun jeuDefiChiffres(niveau: Niveau, objectifQuotidien: Int? = null, jourQuotidien: String? = null) =
        "defi/jeuChiffres/${niveau.name}${suffixeQuotidien(objectifQuotidien, jourQuotidien)}"
    fun jeuDefiLettres(niveau: NiveauLettres, objectifQuotidien: Int? = null, jourQuotidien: String? = null) =
        "defi/jeuLettres/${niveau.name}${suffixeQuotidien(objectifQuotidien, jourQuotidien)}"
    fun jeuDefiChronoChiffres(niveau: Niveau, objectifQuotidien: Int? = null, jourQuotidien: String? = null) =
        "defi/chrono/jeuChiffres/${niveau.name}${suffixeQuotidien(objectifQuotidien, jourQuotidien)}"
    fun jeuDefiChronoLettres(niveau: NiveauLettres, objectifQuotidien: Int? = null, jourQuotidien: String? = null) =
        "defi/chrono/jeuLettres/${niveau.name}${suffixeQuotidien(objectifQuotidien, jourQuotidien)}"

    // Un seul tirage, pas de chaînage de manches (retour utilisateur) : pas de variante défi
    // quotidien pour ce type de défi, donc pas de suffixe optionnel comme les 4 routes ci-dessus.
    const val JEU_DEFI_MOTS_MAX_PATTERN = "defi/motsMax/jeuLettres/{$ARG_NIVEAU}"
    fun jeuDefiMotsMax(niveau: NiveauLettres) = "defi/motsMax/jeuLettres/${niveau.name}"

    const val A_PROPOS = "apropos"
    const val REGLES_DU_JEU = "apropos/reglesDuJeu"
    const val VERSIONS = "apropos/versions"
}
