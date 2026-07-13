package fr.pierre.chiffreslettres.ui.navigation

import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

object Routes {
    const val MENU = "menu"
    const val ENTRAINEMENT_GRAPH = "entrainement"
    const val CHOIX_MODE = "entrainement/choixMode"
    const val CHOIX_NIVEAU_CHIFFRES = "entrainement/choixNiveauChiffres"
    const val CHOIX_NIVEAU_LETTRES = "entrainement/choixNiveauLettres"
    const val ARG_NIVEAU = "niveau"
    const val JEU_CHIFFRES_PATTERN = "entrainement/jeuChiffres/{$ARG_NIVEAU}"
    const val JEU_LETTRES_PATTERN = "entrainement/jeuLettres/{$ARG_NIVEAU}"

    fun jeuChiffres(niveau: Niveau) = "entrainement/jeuChiffres/${niveau.name}"
    fun jeuLettres(niveau: NiveauLettres) = "entrainement/jeuLettres/${niveau.name}"

    const val CHANGER_PROFIL = "profil/changer"
    const val CREER_PROFIL = "profil/creer"

    const val STATISTIQUES = "statistiques"

    const val PARTIE_GRAPH = "partie"
    const val CONFIGURATION_PARTIE = "partie/configuration"
    const val JEU_PARTIE = "partie/jeu"
    const val RECAP_PARTIE = "partie/recap"

    const val REGLAGES = "reglages"
    const val A_PROPOS = "apropos"
    const val REGLES_DU_JEU = "apropos/reglesDuJeu"
    const val VERSIONS = "apropos/versions"
}
