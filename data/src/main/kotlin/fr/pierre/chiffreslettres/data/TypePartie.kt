package fr.pierre.chiffreslettres.data

/**
 * DUO : mode duel à barème indépendant (chacun son score, comme en solo). DUO_CONFRONTATION :
 * mode duel comparatif (le plus proche/le mot le plus long gagne la manche et ses points,
 * l'autre passe à 0 ; égalité → chacun garde son score) — retour utilisateur.
 *
 * DUO_RESEAU / DUO_CONFRONTATION_RESEAU : mêmes règles que DUO/DUO_CONFRONTATION, mais jouées
 * sur 2 téléphones séparés (synchronisées par réseau) plutôt que sur le même appareil.
 */
enum class TypePartie { LIBRE, STRUCTUREE, DUO, DUO_CONFRONTATION, DUO_RESEAU, DUO_CONFRONTATION_RESEAU }
