package fr.pierre.chiffreslettres.data

enum class ModeJeu { CHIFFRES, LETTRES }

/**
 * SERIE : le défi historique, enchaîne les manches jusqu'à la première erreur.
 * CHRONO : budget de temps global par niveau, un échec ne l'arrête pas (retour utilisateur).
 * MOTS_MAX : un seul tirage de lettres, 5 minutes, le plus de mots distincts possible sur ce même
 * tirage (retour utilisateur) — s'arrête sur un mot refusé par le dictionnaire ou une validation
 * à vide, jamais sur un mot déjà trouvé (qui ne compte simplement pas de point supplémentaire).
 * SANS_FAUTE : alterne strictement manches chiffres et lettres (retour utilisateur), sans le
 * plafond de manches du niveau, jusqu'à la première erreur — mode mixte, donc `DefiEntity.mode`
 * n'est pas signifiant pour ce type (toujours CHIFFRES par convention, cf. sa doc).
 * OBJECTIFS_POINTS ("Défi Points") : un seul tirage de lettres, chronométré, avec des objectifs
 * de points à atteindre (`BaremeLettres`, core-letters) — `DefiEntity.serie` compte
 * le nombre d'objectifs atteints, toujours `ModeJeu.LETTRES`.
 */
enum class TypeDefi { SERIE, CHRONO, MOTS_MAX, SANS_FAUTE, OBJECTIFS_POINTS }
