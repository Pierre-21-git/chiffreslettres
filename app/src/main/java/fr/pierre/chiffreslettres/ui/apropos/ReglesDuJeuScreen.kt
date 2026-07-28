package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted

@Composable
fun ReglesDuJeuScreen(onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("Règles du jeu", onRetour)

        SectionRegle("Mode Chiffres") {
            Text(
                "6 nombres sont tirés (parmi 1 à 10 en double exemplaire, et " +
                    "25/50/75/100 en simple exemplaire), ainsi qu'une cible à atteindre. " +
                    "Construisez votre calcul pas à pas façon calculatrice : touchez un " +
                    "premier nombre, un opérateur, puis un second nombre — le résultat " +
                    "remplace les deux nombres utilisés et peut resservir. Les boutons " +
                    "\"Annuler\" et \"Effacer\" permettent de revenir en arrière. Le dernier " +
                    "résultat obtenu est votre proposition : validez dès que vous êtes prêt, " +
                    "ou laissez le chrono s'écouler s'il y en a un.",
                style = MaterialTheme.typography.bodyMedium,
            )
            SousTitreRegle("Comptage des points")
            ListeAPuces(
                "Assez facile (Émile) et Ça va encore (Nestor) : 10 points si le compte " +
                    "est exact, 5 points pour toute proposition non exacte.",
                "Ça se complique (Monique) et Là c'est sérieux (Mathieu) : 10 points si " +
                    "le compte est exact, 7 points pour un compte approchant (écart de 1 " +
                    "avec la cible), 0 point au-delà.",
            )
        }

        SectionRegle("Mode Lettres") {
            Text(
                "Choisissez le nombre de voyelles souhaité (2, 3, 4 ou 5 ; le Y compte " +
                    "comme une voyelle), puis les 10 lettres sont tirées d'un coup. " +
                    "Construisez ensuite votre mot en touchant " +
                    "les lettres dans l'ordre voulu (chaque lettre tirée ne peut être " +
                    "utilisée qu'une fois) ; les boutons \"Annuler\" et \"Effacer\" " +
                    "permettent de revenir en arrière. Trouvez le mot le plus long possible " +
                    "avec ces lettres, avant la fin du chrono s'il y en a un.",
                style = MaterialTheme.typography.bodyMedium,
            )
            SousTitreRegle("Comptage des points")
            ListeAPuces(
                "Assez facile (Émile) et Ça va encore (Nestor) : 5 points pour un mot de " +
                    "2 à 4 lettres, 10 points à partir de 5 lettres.",
                "Ça se complique (Monique) et Là c'est sérieux (Mathieu) : le score " +
                    "correspond au nombre de lettres du mot validé.",
            )
            SousTitreRegle("Mots acceptés")
            Text(
                "Le dictionnaire embarqué exclut les noms propres, les sigles/" +
                    "abréviations, et les formes de verbes purement conjuguées : seuls " +
                    "l'infinitif et les participes présent/passé restent acceptés " +
                    "(\"aimer\", \"aimant\", \"aimé\" sont valides, pas \"aimerait\" ni " +
                    "\"aimons\").",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        SectionRegle("Mode Entraînement") {
            Text(
                "Pas de limite de temps ni de nombre de manches : choisissez un niveau " +
                    "(chiffres ou lettres) et jouez autant de manches que vous voulez, à " +
                    "votre rythme. Pour arrêter, utilisez la flèche de retour en haut de " +
                    "l'écran ; la session est alors enregistrée dans les statistiques.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        SectionRegle("Mode Partie classique") {
            Text(
                "Un seul choix à faire : le niveau, appliqué aux manches chiffres et " +
                    "lettres, jouées en alternance. La durée du chrono et le nombre de " +
                    "manches sont fixés par niveau :",
                style = MaterialTheme.typography.bodyMedium,
            )
            ListeAPuces(
                "Assez facile, Émile (2 manches par mode) — Chiffres : cible ≤ 100, " +
                    "addition et soustraction seulement, solution garantie, 120s. " +
                    "Lettres : X, Y, Z, W, K, Q, H, J exclues, 110s.",
                "Ça va encore, Nestor (3 manches par mode) — Chiffres : cible ≤ 100, les " +
                    "4 opérations, solution garantie, 100s. Lettres : X, Y, Z, W, K, Q " +
                    "exclues, 90s.",
                "Ça se complique, Monique (4 manches par mode) — Chiffres : cible ≤ 500, " +
                    "les 4 opérations, pas de garantie de solution, 60s. Lettres : X, Y, " +
                    "Z, W exclues, 50s.",
                "Là c'est sérieux, Mathieu (5 manches par mode) — " +
                    "Chiffres : cible entre 100 et 999, pas de garantie de solution, 45s. " +
                    "Lettres : aucune lettre exclue, 40s.",
            )
        }

        SectionRegle("Mode Défi série") {
            Text(
                "Choisissez un mode (chiffres ou lettres) et un niveau, puis enchaînez " +
                    "les manches pour aligner le plus de réussites possible d'affilée, avec " +
                    "le même chrono qu'en partie classique pour ce niveau. Une réussite, c'est " +
                    "un compte exact en chiffres, ou un mot valide d'au moins une longueur " +
                    "minimale en lettres :",
                style = MaterialTheme.typography.bodyMedium,
            )
            ListeAPuces(
                "Émile : 4 lettres",
                "Nestor : 5 lettres",
                "Monique : 6 lettres",
                "Mathieu : 7 lettres",
            )
            Text(
                "En chiffres, une solution exacte est toujours garantie, même sur " +
                    "Monique et Mathieu : le défi ne s'arrête que sur une erreur ou un " +
                    "temps écoulé. La série obtenue est enregistrée dans les statistiques.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        SectionRegle("Mode Défi chrono") {
            Text(
                "Même principe que le défi série, mais contre la montre : un budget de " +
                    "temps global est fixé par niveau :",
                style = MaterialTheme.typography.bodyMedium,
            )
            ListeAPuces(
                "Émile : 2 minutes",
                "Nestor : 3 minutes",
                "Monique : 4 minutes",
                "Mathieu : 5 minutes",
            )
            Text(
                "Une réussite, c'est un compte exact en chiffres, ou un mot valide d'au " +
                    "moins une longueur minimale en lettres (la même qu'en défi série) :",
                style = MaterialTheme.typography.bodyMedium,
            )
            ListeAPuces(
                "Émile : 4 lettres",
                "Nestor : 5 lettres",
                "Monique : 6 lettres",
                "Mathieu : 7 lettres",
            )
            Text(
                "L'objectif est d'aligner le plus de réussites possible avant que le " +
                    "temps ne soit épuisé. Contrairement au défi série, une erreur ne met " +
                    "pas fin au défi : elle compte simplement pour zéro, et la manche " +
                    "suivante démarre aussitôt avec le temps restant. La manche en cours " +
                    "peut être coupée en plein milieu si le temps s'épuise. Les règles du " +
                    "niveau choisi s'appliquent normalement (cible et opérations en " +
                    "chiffres, lettres exclues en lettres, sans garantie de solution " +
                    "forcée sur Monique et Mathieu). Le nombre de réussites obtenu est " +
                    "enregistré dans les statistiques.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        SectionRegle("Mode Défi quotidien") {
            Text(
                "Un défi série ou chrono, en chiffres ou en lettres, est tiré au sort " +
                    "chaque jour avec un objectif à atteindre (nombre de réussites, ou de " +
                    "réussites d'affilée selon le type tiré) ; le tirage est le même toute " +
                    "la journée, mais différent d'un profil à l'autre. Le niveau reste " +
                    "libre : lui seul détermine la difficulté pour atteindre l'objectif.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Une fois l'objectif du jour atteint, le défi se verrouille jusqu'au " +
                    "lendemain ; en cas d'échec avant de l'atteindre, il reste retentable " +
                    "le même jour. Deux trophées récompensent une série de jours " +
                    "consécutifs réussie (7 et 30 jours).",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SousTitreRegle(texte: String) {
    Text(texte, style = MaterialTheme.typography.labelLarge, color = TextMuted)
}

/** Vraie liste à puces (retour utilisateur) : une pastille alignée en tête de chaque ligne, au lieu d'un simple "• " collé au texte. */
@Composable
private fun ListeAPuces(vararg items: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (item in items) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BrassBright),
                )
                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}

/** Regroupe les règles d'un mode dans un panneau distinct (lisibilité, retour utilisateur). */
@Composable
private fun SectionRegle(titre: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PanelDeep)
            .border(1.dp, Ivory.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(titre, style = MaterialTheme.typography.titleMedium, color = Ivory)
        content()
    }
}
