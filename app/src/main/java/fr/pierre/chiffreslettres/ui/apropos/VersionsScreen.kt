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
