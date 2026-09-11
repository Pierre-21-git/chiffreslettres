package fr.pierre.chiffreslettres.data

enum class ModeJeu { CHIFFRES, LETTRES }

/**
 * SERIE : le défi historique, enchaîne les manches jusqu'à la première erreur.
 * CHRONO : budget de temps global par niveau, un échec ne l'arrête pas (retour utilisateur).
 * MOTS_MAX : un seul tirage de lettres, 5 minutes, le plus de mots distincts possible sur ce même
 * tirage (retour utilisateur) — s'arrête sur un mot refusé par le dictionnaire ou une validation
 * à vide, jamais sur un mot déjà trouvé (qui ne compte simplement pas de point supplémentaire).
 * OBJECTIFS_POINTS ("Défi Points") : un seul tirage de lettres, chronométré, avec des objectifs
 * de points à atteindre (`BaremeLettres`, core-letters) — `DefiEntity.serie` compte
 * le nombre d'objectifs atteints, toujours `ModeJeu.LETTRES`.
 * SANS_FAUTE : mode retiré (retour utilisateur, 2026-09-10). Conservé uniquement pour que les
 * lignes `DefiEntity` déjà enregistrées avec ce type restent lisibles par Room (`valueOf` sur
 * l'enum planterait sinon la désérialisation de ces vieilles lignes, notamment à l'export des
 * statistiques) — plus aucun code ne crée de nouvelle ligne de ce type.
 */
enum class TypeDefi { SERIE, CHRONO, MOTS_MAX, OBJECTIFS_POINTS, SANS_FAUTE }
