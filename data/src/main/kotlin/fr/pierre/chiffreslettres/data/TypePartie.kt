package fr.pierre.chiffreslettres.data

/**
 * DUO : mode duel à barème indépendant (chacun son score, comme en solo). DUO_CONFRONTATION :
 * mode duel comparatif (le plus proche/le mot le plus long gagne la manche et ses points,
 * l'autre passe à 0 ; égalité → chacun garde son score) — retour utilisateur.
 *
 * DUO_RESEAU / DUO_CONFRONTATION_RESEAU : mêmes règles que DUO/DUO_CONFRONTATION, mais jouées
 * sur 2 téléphones séparés (synchronisées par réseau) plutôt que sur le même appareil.
 *
 * DUEL_MOTS_RESEAU / DUEL_MOTS_CONFRONTATION_RESEAU : jeu "duel mots" (retour utilisateur, le
 * plus de mots trouvés en 5 minutes / le premier à N mots trouvés), 100 % réseau — pas de
 * variante sur le même téléphone, contrairement à DUO/DUO_CONFRONTATION.
 *
 * DUEL_MOTS_POINTS_RESEAU : variante "Duel points" de Confrontation (retour utilisateur, même
 * tirage partagé en direct) — le premier à atteindre un total de points choisi gagne, au lieu
 * d'un nombre de mots.
 */
enum class TypePartie {
    LIBRE,
    STRUCTUREE,
    DUO,
    DUO_CONFRONTATION,
    DUO_RESEAU,
    DUO_CONFRONTATION_RESEAU,
    DUEL_MOTS_RESEAU,
    DUEL_MOTS_CONFRONTATION_RESEAU,
    DUEL_MOTS_POINTS_RESEAU,
}
