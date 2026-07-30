package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

private data class EntreeVersion(val version: String, val date: String, val changements: List<String>)

private val HISTORIQUE_VERSIONS = listOf(
    EntreeVersion(
        version = "1.52",
        date = "2026-07-30",
        changements = listOf(
            "Premier pas vers une déclinaison multilingue (retour utilisateur) : les libellés " +
                "des 4 niveaux de difficulté, l'alphabet du mode Lettres (distribution des " +
                "lettres, voyelles) et les lettres exclues par niveau vivent maintenant dans " +
                "strings.xml au lieu d'être codés en dur — rien ne change pour l'instant en " +
                "français, ça prépare juste le terrain.",
            "Les modules core-numbers et core-letters restent du Kotlin pur sans dépendance " +
                "Android : c'est l'appli qui lit ces ressources et les transmet.",
        ),
    ),
    EntreeVersion(
        version = "1.51",
        date = "2026-07-30",
        changements = listOf(
            "Chiffres, niveaux Monique et Mathieu : quand le tirage n'a pas de solution exacte, " +
                "atteindre la meilleure approche théoriquement possible rapporte maintenant 10 " +
                "points, comme un compte exact (au lieu de 7 points).",
            "Lettres, niveaux Monique et Mathieu, en défi série et défi chrono : si aucun mot du " +
                "tirage n'atteint la longueur minimale imposée, trouver le mot le plus long " +
                "possible pour ce tirage valide quand même la manche.",
            "Partie duo (locale et à distance) : une égalité en score compte maintenant comme " +
                "gagnée pour les deux joueurs, au lieu de n'être une victoire pour personne.",
            "Paliers des trophées \"Partie duo\" ajustés selon le retour utilisateur (ex. " +
                "\"première partie duo jouée\" repasse en argent, plusieurs trophées \"dixième... " +
                "gagnée\" montent en platine ou diamant).",
        ),
    ),
    EntreeVersion(
        version = "1.50",
        date = "2026-07-30",
        changements = listOf(
            "Refonte des paliers de trophées (bronze/argent/or/platine/diamant) selon le retour " +
                "utilisateur : plusieurs trophées remontés d'un ou deux paliers (ex. \"tous les " +
                "comptes exacts dans une partie\" repasse en bronze, \"centième compte exact\" et " +
                "\"centième partie terminée\" passent en platine).",
            "Nouveau rang joueur cumulatif (\"Joueur Bronze\", \"Joueur Argent\"...) affiché sur la " +
                "fiche trophées : obtenu quand TOUS les trophées de ce palier et des paliers " +
                "inférieurs sont débloqués.",
            "Mes statistiques / statistiques générales : un niveau (Emile, Nestor, Monique, " +
                "Mathieu) n'apparaît plus du tout si aucun type de partie (solo, duo, " +
                "confrontation, à distance) n'a encore de données pour ce niveau.",
        ),
    ),
    EntreeVersion(
        version = "1.49",
        date = "2026-07-30",
        changements = listOf(
            "\"Partie réseau\" jouable de bout en bout : les manches se jouent simultanément sur " +
                "les 2 téléphones, avec le même ordre A,B,B,A que la partie duo pour désigner qui " +
                "lance chaque manche (choix du nombre de voyelles en lettres, bouton \"Commencer " +
                "la manche\" en chiffres) ; révélation commune une fois les 2 résultats reçus, " +
                "récap final, historique et trophées enregistrés pour chaque joueur.",
            "6 nouveaux trophées (Duo à distance / Confrontation à distance, sur le modèle des " +
                "trophées Duo/Confrontation existants), et 2 nouvelles sections dans les " +
                "statistiques (perso et générales).",
        ),
    ),
    EntreeVersion(
        version = "1.48",
        date = "2026-07-30",
        changements = listOf(
            "\"Partie réseau\" : ajout du Bluetooth comme second mode de connexion, au choix " +
                "en plus du Wifi local (le Wifi peut être bloqué par une isolation des clients " +
                "sur certaines box domestiques). L'écran de choix du rôle permet maintenant de " +
                "sélectionner le transport avant d'héberger ou de rejoindre une partie.",
        ),
    ),
    EntreeVersion(
        version = "1.47",
        date = "2026-07-29",
        changements = listOf(
            "Nouveau : \"Partie réseau\" (première brique, connexion uniquement) — deux " +
                "téléphones sur le même réseau Wifi peuvent se découvrir et se connecter " +
                "(hôte/invité, via NsdManager) et échanger leur pseudo/avatar. La logique de " +
                "jeu synchronisée (manches, scores, trophées, statistiques) arrivera dans une " +
                "prochaine version : cet écran ne fait encore que valider la connexion entre " +
                "les deux appareils.",
        ),
    ),
    EntreeVersion(
        version = "1.46",
        date = "2026-07-29",
        changements = listOf(
            "Écran \"Choisir un profil\" (retour utilisateur, 1.45 encore trop bas) : le bloc " +
                "titre + vignettes + bouton est maintenant centré par rapport à toute la " +
                "hauteur de l'écran, et non plus seulement dans l'espace restant sous le titre " +
                "\"Chiffres & Lettres\" (qui, lui, ne bouge toujours pas).",
        ),
    ),
    EntreeVersion(
        version = "1.45",
        date = "2026-07-29",
        changements = listOf(
            "Écran \"Choisir un profil\" (correction 1.44, retour utilisateur \"verticalement\" " +
                "et non \"horizontalement\") : le bloc titre + vignettes + bouton reprend toute " +
                "la largeur de l'écran, mais est maintenant centré verticalement dans l'espace " +
                "restant sous le titre \"Chiffres & Lettres\" (qui, lui, ne bouge pas) — il " +
                "descend donc légèrement par rapport à avant.",
        ),
    ),
    EntreeVersion(
        version = "1.44",
        date = "2026-07-29",
        changements = listOf(
            "Écran \"Choisir un profil\" : la vignette de chaque joueur (avatar/pseudo + " +
                "icônes renommer/supprimer) forme désormais un bloc compact centré, aligné " +
                "avec le titre \"Choisir un profil\" et le bouton \"Créer un nouveau profil\" " +
                "au-dessus/en dessous — au lieu d'un cartouche étiré qui repoussait les icônes " +
                "tout à droite. Le titre \"Chiffres & Lettres\" garde sa position.",
        ),
    ),
    EntreeVersion(
        version = "1.43",
        date = "2026-07-29",
        changements = listOf(
            "Partie duo, mode Duo : le score de la partie en cours affiché sur l'écran de " +
                "transition n'avance plus dès que le premier joueur a fini une manche — il " +
                "n'est mis à jour qu'une fois les deux joueurs passés, comme c'était déjà le " +
                "cas en mode Confrontation.",
        ),
    ),
    EntreeVersion(
        version = "1.42",
        date = "2026-07-29",
        changements = listOf(
            "Cartouche avatar/pseudo cliquable pour changer de profil uniquement sur la page " +
                "d'accueil (retiré des écrans de configuration entraînement/partie/duo/défi, " +
                "où il n'est plus qu'informatif) ; avatar et pseudo désormais centrés à " +
                "l'intérieur du cartouche, sur tous les écrans.",
            "Titre \"Chiffres & Lettres\" remonté sur la page d'accueil pour être à la même " +
                "hauteur que sur l'écran \"Choisir un profil\".",
            "Écran \"Choisir un profil\" : titre centré (aligné avec le bloc des profils en " +
                "dessous), et la flèche de retour n'apparaît plus jamais sur cet écran, même " +
                "en y arrivant depuis le cartouche de l'accueil.",
        ),
    ),
    EntreeVersion(
        version = "1.41",
        date = "2026-07-29",
        changements = listOf(
            "Partie duo : un écran de transition annonce désormais aussi le premier joueur " +
                "juste après avoir démarré le duel, avant la toute première manche.",
            "Partie duo : le résultat d'une manche (score, calcul ou mot joué) ne s'affiche " +
                "plus dès que le premier joueur a fini — il reste caché jusqu'à ce que le " +
                "second joueur ait joué à son tour, pour ne pas lui donner un avantage.",
            "Partie duo : l'écran de transition indique clairement qui remporte chaque " +
                "manche, affiche désormais le score de la partie en cours en permanence, et " +
                "range le détail de la manche sous le bouton \"Prêt\" plutôt qu'au-dessus.",
            "Partie duo : correction de l'alternance du premier joueur, qui suit désormais " +
                "un cycle A,B,B,A répété (au lieu d'une simple alternance A,B,A,B) pour mieux " +
                "équilibrer l'avantage du premier joueur.",
        ),
    ),
    EntreeVersion(
        version = "1.40",
        date = "2026-07-29",
        changements = listOf(
            "Partie duo : écran de transition toujours affiché entre deux tours (même " +
                "quand le joueur ne change pas), avec le score et le calcul/mot de chaque " +
                "joueur déjà joué sur la manche — le cadre \"score obtenu\" n'apparaît plus " +
                "sur l'écran de jeu lui-même.",
            "Partie duo : récap final présenté comme le récap solo (un bloc par joueur), " +
                "avec le vainqueur en plus.",
            "Partie duo : confirmation demandée avant de quitter une partie en cours " +
                "depuis la flèche de retour.",
            "Trophées généraux (parties terminées, score, comptes exacts, mots, partie " +
                "parfaite) : comptent désormais aussi les parties duo et confrontation, pas " +
                "seulement le solo.",
            "Écran \"Choisir un profil\" : titre Chiffres & Lettres ajouté comme sur " +
                "l'accueil, mise en page revue pour que le titre reste bien au-dessus de la " +
                "liste des profils.",
            "Widget : rafraîchissement programmé à minuit en plus des déclenchements " +
                "existants, pour ne pas afficher le statut de la veille après le changement " +
                "de jour.",
            "Graphique de progression : correction du décalage d'un cran sur les repères " +
                "de l'axe des abscisses, repère fixé à toutes les 20 parties (ligne pleine " +
                "hauteur plutôt qu'un tiret), axe des scores resserré sur la plage réelle " +
                "des résultats plutôt que de toujours partir de 0.",
        ),
    ),
    EntreeVersion(
        version = "1.39",
        date = "2026-07-28",
        changements = listOf(
            "Nouveau mode Partie duo : affrontez un autre profil du foyer, à tour de rôle " +
                "sur le même tirage à chaque manche (le joueur qui commence change à " +
                "chaque manche). Deux façons de compter les points au choix : Duo (chacun " +
                "son score) ou Confrontation (le plus proche/le mot le plus long gagne la " +
                "manche).",
            "\"Partie classique\" renommée \"Partie solo\", pour la distinguer du nouveau " +
                "mode duo.",
            "Statistiques : un classement séparé par niveau pour le solo, le duo et la " +
                "confrontation (un score de confrontation peut être écrasé à 0, il ne doit " +
                "pas se mélanger au meilleur score solo).",
            "6 nouveaux trophées : première partie duo/confrontation jouée, première " +
                "gagnée, dixième gagnée.",
        ),
    ),
    EntreeVersion(
        version = "1.38",
        date = "2026-07-28",
        changements = listOf(
            "Trophées : couleurs des pastilles Argent/Platine/Diamant plus marquées et " +
                "mieux différenciées (retour utilisateur, elles se confondaient trop).",
        ),
    ),
    EntreeVersion(
        version = "1.37",
        date = "2026-07-28",
        changements = listOf(
            "Graphique de progression : quadrillage horizontal tous les 10 ou 20 points, " +
                "et tirets en abscisse toutes les 10 ou 20 parties, pour une meilleure " +
                "lisibilité (retour utilisateur).",
        ),
    ),
    EntreeVersion(
        version = "1.36",
        date = "2026-07-28",
        changements = listOf(
            "Mes statistiques : graphique de progression des scores dans le temps, par " +
                "niveau, en plus du podium des meilleurs scores.",
            "Rappel quotidien (notification) si un ou plusieurs profils n'ont pas encore " +
                "fait le défi du jour — un seul rappel groupé pour toute la famille, pas " +
                "un par profil.",
            "Nouveau widget écran d'accueil : statut du défi quotidien (fait / pas fait, " +
                "série en cours) pour chaque profil, jusqu'à 6.",
        ),
    ),
    EntreeVersion(
        version = "1.35",
        date = "2026-07-28",
        changements = listOf(
            "Compte approchant (Monique/Mathieu) : le palier de 7 points s'applique " +
                "désormais à tout écart d'au plus 100 avec la cible, et non plus " +
                "seulement à un écart de 1 (retour utilisateur).",
            "Correction d'un bug qui pouvait faire perdre silencieusement une partie " +
                "classique terminée (absente des statistiques et des trophées, même " +
                "après redémarrage) à cause d'une navigation trop rapide interrompant " +
                "son enregistrement en base.",
            "Fin de défi (série/chrono, chiffres/lettres) : le bouton \"Changer de " +
                "niveau\" est renommé \"Retour\", plus fidèle à ce qu'il fait réellement.",
        ),
    ),
    EntreeVersion(
        version = "1.34",
        date = "2026-07-28",
        changements = listOf(
            "Trophées : 5 paliers (bronze/argent/or/platine/diamant) choisis à la main, " +
                "affichés en pastille de couleur autour de chaque trophée.",
            "Statistiques générales : podium (3 premiers) à la place de la liste des scores.",
            "Mes statistiques : même page que Statistiques générales, mais avec uniquement " +
                "les meilleurs scores du profil actif (le détail entraînement/défi par " +
                "niveau, moins utilisé, a été retiré).",
            "Page de choix de profil : les boutons de profil reprennent le style des " +
                "plaquettes avatar/pseudo utilisées ailleurs dans l'app.",
        ),
    ),
    EntreeVersion(
        version = "1.33",
        date = "2026-07-28",
        changements = listOf(
            "Accueil : séparateur ajouté après le bouton \"Entraînement\".",
            "Défi quotidien : objectif toujours fixé à 3 en lettres (au lieu de 3 à 5), " +
                "trouver un mot valide étant plus dur qu'un compte exact.",
            "Avatar des joueurs affiché dans le classement des Statistiques générales.",
        ),
    ),
    EntreeVersion(
        version = "1.32",
        date = "2026-07-28",
        changements = listOf(
            "Accueil : le grand badge centré est remplacé par le même mini-bandeau que les " +
                "autres écrans (avatar + pseudo), avec un texte plus grand (44dp/34sp avatar, " +
                "20sp pseudo, contre 36dp/28sp/16sp ailleurs) — un seul style dans toute l'app.",
            "Retiré la tuile \"Profil\" de l'accueil, redondante : le bandeau avatar/pseudo " +
                "est cliquable pour changer de profil.",
            "Corrigé : la page d'accueil pouvait dépasser de l'écran sans pouvoir défiler.",
            "Chiffres, niveau Monique : cible relevée à 500 maximum (au lieu de 200).",
        ),
    ),
    EntreeVersion(
        version = "1.31",
        date = "2026-07-28",
        changements = listOf(
            "Avatar et pseudo nettement mis en avant, selon les propositions choisies : sur " +
                "l'accueil, grand badge circulaire (72dp, emoji 40sp) et pseudo (24sp) centrés " +
                "dans leur propre bloc sous le logo ; sur les autres écrans, mini-bandeau " +
                "avatar (36dp, 28sp) + pseudo (16sp), plus visible que la puce fine d'avant.",
        ),
    ),
    EntreeVersion(
        version = "1.30",
        date = "2026-07-28",
        changements = listOf(
            "Statistiques : la tuile de l'accueil mène désormais directement à sa propre " +
                "fiche, sans passer par une liste de tous les profils — cloisonnement encore " +
                "plus strict (retour utilisateur). Le classement général (Statistiques " +
                "générales), lui, reste commun à tous les profils.",
        ),
    ),
    EntreeVersion(
        version = "1.29",
        date = "2026-07-28",
        changements = listOf(
            "Corrigé : le défi quotidien continuait de proposer \"Continuer\" après avoir " +
                "atteint l'objectif du jour, sans jamais l'annoncer — il s'arrête désormais " +
                "immédiatement avec un message de victoire dès l'objectif atteint.",
            "Objectif du défi quotidien réduit (3 à 5, au lieu de 4 à 10) après retour " +
                "utilisateur : trop long pour un défi pensé pour être rapide.",
            "L'écran de choix de profil au démarrage est désormais sauté automatiquement " +
                "s'il n'y a qu'un seul profil (rien à choisir).",
            "Corrigé : le titre \"Choisir un profil\" passait sous la bande système au tout " +
                "premier écran affiché par l'app.",
            "Avatar et pseudo mis en valeur : gros badge circulaire sur l'accueil, avatar " +
                "légèrement agrandi dans la puce compacte des autres écrans.",
        ),
    ),
    EntreeVersion(
        version = "1.28",
        date = "2026-07-28",
        changements = listOf(
            "Corrigé : sur la fiche d'un joueur (écran Statistiques), exporter, importer ou " +
                "réinitialiser n'est désormais possible que pour le profil actif — plus de " +
                "risque de modifier par erreur les données d'un autre profil juste en " +
                "consultant sa fiche.",
            "Retiré le dialogue \"Continuer avec le profil actif ?\" sur les tuiles de " +
                "l'accueil, devenu redondant avec l'écran de sélection de profil affiché à " +
                "chaque lancement (v1.26).",
        ),
    ),
    EntreeVersion(
        version = "1.27",
        date = "2026-07-27",
        changements = listOf(
            "Nouveau : Défi quotidien. Un défi (série ou chrono, chiffres ou lettres) est " +
                "tiré au sort chaque jour avec un objectif à atteindre, à un niveau choisi " +
                "librement ; une fois réussi, il se verrouille jusqu'au lendemain.",
            "Deux nouveaux trophées de série de jours consécutifs au défi quotidien (7 et " +
                "30 jours), affichés avec la progression en cours sur l'écran du défi.",
        ),
    ),
    EntreeVersion(
        version = "1.26",
        date = "2026-07-27",
        changements = listOf(
            "Un écran de sélection de profil s'affiche désormais à chaque lancement de " +
                "l'app, pour éviter de jouer sous le mauvais profil par erreur (retour " +
                "utilisateur, cloisonnement des profils).",
            "Ajout d'un avatar (emoji, choisi parmi 16) sur chaque profil, visible sur le " +
                "pseudo actif et modifiable en le renommant.",
        ),
    ),
    EntreeVersion(
        version = "1.25",
        date = "2026-07-27",
        changements = listOf(
            "Écran Trophées réordonné : \"Parties terminées\" puis \"Score de partie\" en " +
                "tête de liste, et \"Mots\" déplacé après \"Partie parfaite\".",
        ),
    ),
    EntreeVersion(
        version = "1.24",
        date = "2026-07-27",
        changements = listOf(
            "Trophées \"Mots\" étendus : un palier premier/dixième mot existe désormais pour " +
                "chaque longueur de 4 à 10 lettres (au lieu de 10 lettres seulement).",
            "Trophées \"Défi\" : les paliers de meilleure série (3/5/10/15/20) sont désormais " +
                "séparés par mode (Chiffres / Lettres) comme le défi chrono, et deux nouveaux " +
                "paliers 30 et 50 ont été ajoutés.",
        ),
    ),
    EntreeVersion(
        version = "1.23",
        date = "2026-07-27",
        changements = listOf(
            "Dictionnaire : les noms propres, sigles/abréviations et formes de verbes " +
                "purement conjuguées (indicatif, subjonctif, conditionnel, impératif) sont " +
                "désormais exclus, conformément à la règle du jeu télévisé ; infinitifs et " +
                "participes présent/passé restent acceptés — 245 576 → 115 489 mots. " +
                "Filtrage croisé avec le lexique Morphalou3 (CNRS/ATILF, licence LGPL-LR).",
            "Écran \"Règles du jeu\" : ajout d'une section \"Mots acceptés\" dans le mode " +
                "Lettres détaillant ce filtrage.",
        ),
    ),
    EntreeVersion(
        version = "1.22",
        date = "2026-07-23",
        changements = listOf(
            "Dictionnaire reconstruit depuis le pipeline Hunspell/spylls complet, sans le " +
                "filtrage par fréquence appliqué jusqu'ici (origine non documentée) : 152 626 " +
                "→ 245 576 mots reconnus",
            "Corrigé : un mot de 10 lettres (la longueur maximale du tirage) ne pouvait " +
                "jamais être reconnu valide, le dictionnaire s'arrêtant à 9 lettres — ce qui " +
                "rendait aussi le trophée \"Premier mot de 10 lettres\" impossible à obtenir",
        ),
    ),
    EntreeVersion(
        version = "1.21",
        date = "2026-07-23",
        changements = listOf(
            "Lettres : les boutons \"Consonne\"/\"Voyelle\" (tirage lettre par lettre) sont " +
                "remplacés par un choix unique du nombre de voyelles souhaité (2, 3, 4 ou 5), " +
                "les 10 lettres étant ensuite tirées d'un coup ; boutons au même style " +
                "ivoire/doré que ceux de l'accueil",
            "Corrigé : \"repérées\" et 16 autres participes passés féminins pluriels en " +
                "\"-érées\" (altérées, digérées, espérées, libérées, tolérées, etc.) manquaient " +
                "du dictionnaire et étaient refusés à tort",
            "Trophées \"Un niveau terminé partout\" et \"Un défi terminé partout\" retirés ; " +
                "nouveau palier de 15 (comptes exacts / mots) pour le défi chrono, et nouveaux " +
                "paliers de 15 et 20 pour les séries de défi — 48 trophées au total",
        ),
    ),
    EntreeVersion(
        version = "1.20",
        date = "2026-07-22",
        changements = listOf(
            "Le bouton \"Versions\" de l'écran À propos n'apparaît plus qu'en build de " +
                "débogage, l'historique des versions n'étant utile qu'en interne",
        ),
    ),
    EntreeVersion(
        version = "1.19",
        date = "2026-07-20",
        changements = listOf(
            "Trophées \"Défi chrono\" simplifiés : un seul jeu de paliers (2/3/5/10/12 " +
                "réussites) par mode chiffres/lettres, tous niveaux confondus, au lieu de " +
                "paliers distincts par niveau — 46 trophées au total",
            "Icône badgée d'un bandeau vert \"debug\" en build de débogage, pour la " +
                "distinguer visuellement de la version release installée en parallèle",
        ),
    ),
    EntreeVersion(
        version = "1.18",
        date = "2026-07-18",
        changements = listOf(
            "\"Partie solo\" renommé en \"Partie classique\" partout dans l'application",
            "Nouveaux boutons \"Exporter mes statistiques\" et \"Importer mes statistiques\" sur " +
                "la fiche d'un profil (statistiques, défis et trophées dans un fichier JSON) ; " +
                "l'import remplace les statistiques actuelles du profil après confirmation",
        ),
    ),
    EntreeVersion(
        version = "1.17",
        date = "2026-07-18",
        changements = listOf(
            "Trophées \"Défi chrono\" : paliers plus accessibles — Émile et Nestor passent à " +
                "2/3/5 réussites (au lieu de 5/10), Monique et Mathieu à 5/10 (au lieu de " +
                "5/10/20 et 10/20/30/40) — 56 trophées au total",
            "Règles du jeu : chaque mode est désormais présenté dans un panneau distinct, " +
                "plus lisible et aéré, dans le même esprit que l'écran Partie solo ; les " +
                "énumérations par niveau (dont la longueur minimale des mots en défi " +
                "chrono) sont de vraies listes à pastilles, et les références au jeu télé " +
                "ont été retirées",
            "Nouvelle icône de l'application",
            "Code source placé sous licence GNU GPL-3.0 (fichier LICENSE, mention dans " +
                "\"À propos\")",
        ),
    ),
    EntreeVersion(
        version = "1.16",
        date = "2026-07-18",
        changements = listOf(
            "Statistiques d'un profil : \"Mes statistiques\" (ses stats par niveau) et " +
                "\"Statistiques générales\" (le classement commun à tous les profils) sont " +
                "désormais deux boutons vers deux écrans dédiés, au lieu d'une seule page",
            "Le bouton \"Défi\" de l'accueil est remplacé par deux boutons \"Défi série\" et " +
                "\"Défi chrono\", menant chacun directement au choix du niveau (plus d'onglets " +
                "à l'intérieur de l'écran Défi)",
        ),
    ),
    EntreeVersion(
        version = "1.15",
        date = "2026-07-18",
        changements = listOf(
            "Trophées \"Score de partie\" : le seuil de points passe de \"strictement plus de\" " +
                "à \"au moins\" (ex. \"Première partie à au moins 20 points\")",
            "Trophées \"Défi chrono\" : regroupés par niveau (Émile, Nestor, Monique, Mathieu " +
                "en sous-titre) au lieu d'un seul bloc mélangeant chiffres et lettres",
            "Statistiques entièrement revues : l'écran n'affiche plus que la liste des " +
                "profils, cliquer sur un profil ouvre sa fiche complète (ses statistiques par " +
                "niveau, ses trophées, la réinitialisation, puis le classement général) — plus " +
                "d'onglets \"Général\"/\"Joueurs\"",
            "Un dialogue demande désormais de confirmer le profil actif avant d'entrer en " +
                "Entraînement, Partie solo ou Défi (\"Non\" redirige vers l'écran Profil)",
            "Le mot \"joueur\" est remplacé par \"profil\" dans les textes de l'application",
        ),
    ),
    EntreeVersion(
        version = "1.14",
        date = "2026-07-18",
        changements = listOf(
            "Nouveau : le défi chrono, en plus du défi série existant — un budget de temps " +
                "global par niveau (2 min sur Émile, 3 sur Nestor, 4 sur Monique, 5 sur " +
                "Mathieu) où une erreur ne met plus fin au défi, l'objectif étant d'aligner " +
                "le plus de réussites possible avant la fin du temps",
            "22 nouveaux trophées pour le défi chrono (58 au total), à paliers de réussites " +
                "par niveau et par mode (chiffres et lettres)",
            "Les meilleures performances en défi chrono apparaissent désormais dans les " +
                "statistiques par joueur, à côté des meilleures séries du défi série",
            "Défi (série et chrono) : le seuil de longueur d'un mot réussi en lettres est " +
                "désormais \"au moins N lettres\" au lieu de \"strictement plus de N lettres\", " +
                "pour correspondre exactement au niveau choisi",
        ),
    ),
    EntreeVersion(
        version = "1.13",
        date = "2026-07-16",
        changements = listOf(
            "Le pseudo du profil actif est désormais affiché (et modifiable en un clic) sur " +
                "les pages Entraînement, Partie solo et Défi, comme sur l'accueil",
            "Trophées : liste passée en une seule colonne, avec le texte à droite de l'icône " +
                "au lieu d'une grille",
        ),
    ),
    EntreeVersion(
        version = "1.12",
        date = "2026-07-16",
        changements = listOf(
            "Nouveaux trophées : 36 succès à débloquer en partie solo et en défi (comptes " +
                "exacts, mots de 10 lettres, parties parfaites, seuils de points, parties et " +
                "défis terminés, couverture de tous les niveaux)",
            "Bouton \"Voir mes trophées\" sur la fiche d'un joueur (onglet Joueurs des " +
                "statistiques), avec la date d'obtention de chaque trophée débloqué",
            "Bouton \"Trophées\" dans À propos pour consulter la liste complète des trophées " +
                "possibles",
            "\"Réinitialiser mes statistiques\" efface désormais aussi les trophées du joueur",
        ),
    ),
    EntreeVersion(
        version = "1.11",
        date = "2026-07-16",
        changements = listOf(
            "Corrigé : en défi, cliquer sur \"Recommencer\" après un échec rejouait l'ancien " +
                "tirage déjà terminé au lieu d'un nouveau, ce qui permettait de continuer la " +
                "série sans rejouer un mot ou un compte valide",
            "Corrigé : en défi chiffres, valider sans avoir fait aucune opération pouvait " +
                "rapporter des points comme s'il s'agissait d'une vraie proposition",
            "Affichage de la série en défi simplifié (pastille \"Série\" au lieu de " +
                "\"Manche\" / \"Série : X\")",
            "Le pseudo du profil actif est désormais affiché sur les pages de jeu, pas " +
                "seulement sur l'accueil",
            "Statistiques réorganisées en deux onglets \"Général\" et \"Joueurs\", plus " +
                "lisibles et aérés (séparateurs entre niveaux)",
            "La réinitialisation des statistiques se fait désormais joueur par joueur, " +
                "depuis l'onglet Joueurs (le bouton de réinitialisation globale a disparu)",
            "Accueil réorganisé : Profil et À propos ont le même style que les autres " +
                "boutons, avec un nouvel ordre et un séparateur",
            "Retrait de la mention \"Mode 2 joueurs\" des règles du jeu (fonctionnalité non " +
                "développée pour l'instant)",
        ),
    ),
    EntreeVersion(
        version = "1.10",
        date = "2026-07-15",
        changements = listOf(
            "Nouveau mode Défi : enchaînez les manches d'un niveau (chiffres ou lettres) et " +
                "alignez le plus de réussites possible d'affilée, avec le même chrono qu'en " +
                "partie solo pour ce niveau",
            "En défi chiffres, une solution exacte est toujours garantie, même sur Monique " +
                "et Mathieu, pour que seule une erreur du joueur puisse arrêter la série",
            "En défi lettres, la longueur minimale d'un mot valide augmente avec le niveau " +
                "(5, 6, 7 ou 8 lettres)",
            "Les meilleures séries de défi (chiffres et lettres) apparaissent désormais " +
                "dans les statistiques par joueur, groupées par niveau comme le reste",
        ),
    ),
    EntreeVersion(
        version = "1.9",
        date = "2026-07-15",
        changements = listOf(
            "Statistiques par joueur déplacées sur un écran dédié, avec un sélecteur pour " +
                "n'afficher qu'un joueur à la fois",
            "Stats désormais groupées par niveau : nombre de manches d'entraînement " +
                "chiffres/lettres, nombre de parties solo jouées et top 3 des meilleures " +
                "parties solo (au lieu du top 5 des meilleures manches individuelles, tous " +
                "niveaux confondus)",
            "Seuls les niveaux comportant des données sont affichés pour chaque joueur",
        ),
    ),
    EntreeVersion(
        version = "1.8",
        date = "2026-07-15",
        changements = listOf(
            "Statistiques par joueur : nombre de manches d'entraînement (chiffres/lettres), " +
                "nombre de parties solo jouées, et top 5 des meilleurs scores individuels " +
                "chiffres/lettres avec leur date",
            "\"Partie\" renommé en \"Partie solo\" partout dans l'application, en prévision " +
                "d'un futur mode multijoueur",
        ),
    ),
    EntreeVersion(
        version = "1.7",
        date = "2026-07-15",
        changements = listOf(
            "Correction du barème chiffres sur Monique/Mathieu : un compte approchant (écart " +
                "de 1) rapporte désormais 7 points même quand une solution exacte existait pour " +
                "le tirage et n'a pas été trouvée, au lieu de 0",
            "Titres \"Entraînement\" et \"Partie\" raccourcis (retrait de \"libre\"/\"structurée\")",
            "Partie : le niveau Monique passe de 5 à 4 manches par mode",
        ),
    ),
    EntreeVersion(
        version = "1.6",
        date = "2026-07-15",
        changements = listOf(
            "Lettres : la grille de tuiles, le cadre \"Votre mot\" et le cadre Tirage/Temps " +
                "sont affichés dès le début de la manche à leur position finale, plus rien ne " +
                "se déplace au fil du tirage",
            "Chiffres : plaquettes de nombres, boutons d'opérations et panneau \"Vos " +
                "opérations\" centrés et à hauteur fixe dès le début de la manche",
            "Partie structurée : affichage du numéro de manche en cours sur le total (ex. " +
                "\"2 / 4\"), écran de manche désormais doté d'un titre et d'un bouton retour",
            "Profil : boutons \"Renommer\"/\"Supprimer\" remplacés par des icônes alignées à " +
                "droite",
            "Boutons ivoire (page d'accueil, Valider) : largeur et hauteur ajustées pour un " +
                "liseré doré visible sur les 4 côtés",
            "Lettres : boutons \"Consonne\" et \"Voyelle\" recolorés en sarcelle/terracotta, " +
                "deux teintes accordées entre elles et distinctes du doré/ivoire",
        ),
    ),
    EntreeVersion(
        version = "1.5",
        date = "2026-07-15",
        changements = listOf(
            "Correction : un mot ne composé que de lettres tirées mais absent du dictionnaire " +
                "était accepté à tort aux lettres",
            "Correction : aux chiffres, sur Monique et Mathieu, quand aucune solution exacte " +
                "n'existe pour la cible tirée, l'application cherche désormais la meilleure " +
                "approche possible (7 points si vous l'atteignez, au lieu d'un barème dégressif " +
                "peu fidèle au jeu télé)",
            "Chiffres : liste des opérations affichée pour la solution possible (comme pour le " +
                "joueur), nouveau bouton \"Effacer\"",
            "Lettres : nombre de lettres affiché à côté du meilleur mot trouvé, tuiles tirées " +
                "centrées, bouton \"Consonne\" en bleu",
            "Statistiques : le classement affiche désormais le score final de la partie (somme " +
                "des manches) et sa date, plus récent en priorité à score égal",
            "Entraînement libre : retrait du score cumulé et de l'affichage \"Illimité\" ; les " +
                "boutons \"Changer de niveau\"/\"Arrêter\"/\"Quitter l'entraînement\" sont " +
                "retirés (sortie via la flèche de retour)",
            "Confirmation demandée avant la suppression d'un profil (comme pour la " +
                "réinitialisation des statistiques)",
            "Règles du jeu : contenu réorganisé (Chiffres, Lettres, Entraînement, Partie " +
                "structurée avec détail par niveau) et mis en forme de façon plus aérée",
            "Boutons \"Valider\" systématiquement pleine largeur, page d'accueil et boutons " +
                "revus",
        ),
    ),
    EntreeVersion(
        version = "1.4",
        date = "2026-07-15",
        changements = listOf(
            "Partie structurée : le nombre de manches par mode est désormais fixé par " +
                "niveau (2 pour Émile, 3 pour Nestor, 5 pour Monique et Mathieu comme le " +
                "jeu télé) au lieu d'un réglage global",
            "Suppression de l'écran Réglages : plus rien à y configurer, la durée du " +
                "chrono en partie structurée est elle aussi fixée par niveau",
            "Gestion des profils (renommer/supprimer) désormais uniquement sur l'écran " +
                "\"Changer de profil\"",
            "Statistiques : retrait de la section \"Stats par joueur\", ne reste que le " +
                "classement par niveau",
        ),
    ),
    EntreeVersion(
        version = "1.3",
        date = "2026-07-14",
        changements = listOf(
            "Entraînement libre sans limite de temps ; nouveau bouton \"Quitter " +
                "l'entraînement\" visible pendant la manche",
            "Durées de chrono par défaut différenciées par niveau, pour la partie " +
                "structurée (de 45s à 120s selon le niveau et le mode)",
            "Correction : le score d'une manche de chiffres prenait parfois le nombre " +
                "sélectionné au lieu du dernier résultat calculé",
        ),
    ),
    EntreeVersion(
        version = "1.2",
        date = "2026-07-14",
        changements = listOf(
            "Partie structurée simplifiée : un seul choix de niveau, le nombre de manches " +
                "par mode se règle désormais dans Réglages",
            "Mode Chiffres : panneau affichant les opérations effectuées pendant la manche, " +
                "titre d'écran avec retour vers l'entraînement",
            "Mode Lettres : mot construit en touchant les lettres tirées (plus de clavier), " +
                "tuiles plus grosses (5 par ligne), boutons Annuler et Effacer, titre d'écran " +
                "avec retour vers l'entraînement",
            "Statistiques : classement par niveau en scores bruts (un joueur peut apparaître " +
                "plusieurs fois), bouton pour réinitialiser les statistiques",
            "Réglages recentrés sur le jeu (durée par niveau, nombre de manches) ; la gestion " +
                "des profils (créer/renommer/supprimer) est désormais sur l'écran \"Changer de " +
                "profil\"",
            "Correction : la durée de chrono réglée dans Réglages n'était pas toujours prise " +
                "en compte en entraînement libre",
        ),
    ),
    EntreeVersion(
        version = "1.1",
        date = "2026-07-14",
        changements = listOf(
            "Niveaux Chiffres et Lettres renommés et réduits à 4 : Assez facile (Émile), " +
                "Ça va encore (Nestor), Ça se complique (Monique), Là c'est sérieux (Mathieu)",
            "Mode Lettres : tirage à 10 lettres au lieu de 9",
            "Nouveau barème de points sur les niveaux Émile et Nestor (5 points pour un compte " +
                "approchant, 10 pour un compte bon)",
            "Durée du chrono et nombre de jetons/lettres réglables niveau par niveau",
            "Entraînement libre : les niveaux Chiffres et Lettres sont listés directement",
            "Classement par niveau fusionné entre Chiffres et Lettres (parties structurées " +
                "uniquement, top 5) ; statistiques par joueur affichées pour tous les joueurs",
        ),
    ),
    EntreeVersion(
        version = "1.0",
        date = "2026-07-13",
        changements = listOf(
            "Mode Chiffres et mode Lettres jouables en entraînement libre",
            "Partie structurée configurable (nombre de manches, niveaux)",
            "Profils joueurs, historique et statistiques",
            "Réglages (durée du chrono, gestion des profils) et écrans d'information",
        ),
    ),
)

@Composable
fun VersionsScreen(onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Versions", onRetour)
        for (entree in HISTORIQUE_VERSIONS) {
            Text("${entree.version} — ${entree.date}", style = MaterialTheme.typography.titleMedium)
            for (changement in entree.changements) {
                Text("• $changement")
            }
        }
    }
}
