package fr.pierre.chiffreslettres.data

enum class ModeJeu { CHIFFRES, LETTRES }

/**
 * SERIE : le défi historique, enchaîne les manches jusqu'à la première erreur.
 * CHRONO : budget de temps global par niveau, un échec ne l'arrête pas (retour utilisateur).
 */
enum class TypeDefi { SERIE, CHRONO }
