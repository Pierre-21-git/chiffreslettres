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
