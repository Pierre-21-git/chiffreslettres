package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

@Composable
fun ReglesDuJeuScreen(onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("Règles du jeu", onRetour)

        Text("Mode Chiffres", style = MaterialTheme.typography.titleMedium)
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
        Text(
            "Comptage des points — sur Assez facile (Émile) et Ça va encore (Nestor) : " +
                "10 points si le compte est exact, 5 points pour toute proposition non " +
                "exacte. Sur Ça se complique (Monique) et Là c'est sérieux (Mathieu) : 10 " +
                "points si le compte est exact, 7 points pour un compte approchant (écart " +
                "de 1 avec la cible), 0 point au-delà.",
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text("Mode Lettres", style = MaterialTheme.typography.titleMedium)
        Text(
            "10 lettres sont tirées une à une : à chaque tirage, choisissez " +
                "\"Consonne\" ou \"Voyelle\" (le Y compte comme une voyelle). " +
                "L'application impose toujours au moins 2 voyelles parmi les lettres " +
                "tirées. Une fois le tirage terminé, construisez votre mot en touchant " +
                "les lettres dans l'ordre voulu (chaque lettre tirée ne peut être " +
                "utilisée qu'une fois) ; les boutons \"Annuler\" et \"Effacer\" " +
                "permettent de revenir en arrière. Trouvez le mot le plus long possible " +
                "avec ces lettres, avant la fin du chrono s'il y en a un.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "Comptage des points — sur Assez facile (Émile) et Ça va encore (Nestor) : " +
                "5 points pour un mot de 2 à 4 lettres, 10 points à partir de 5 lettres. " +
                "Sur Ça se complique (Monique) et Là c'est sérieux (Mathieu) : le score " +
                "correspond au nombre de lettres du mot validé — comme à la télé.",
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text("Mode Entraînement", style = MaterialTheme.typography.titleMedium)
        Text(
            "Pas de limite de temps ni de nombre de manches : choisissez un niveau " +
                "(chiffres ou lettres) et jouez autant de manches que vous voulez, à " +
                "votre rythme. Pour arrêter, utilisez la flèche de retour en haut de " +
                "l'écran ; la session est alors enregistrée dans les statistiques.",
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text("Mode Partie solo", style = MaterialTheme.typography.titleMedium)
        Text(
            "Un seul choix à faire : le niveau, appliqué aux manches chiffres et " +
                "lettres, jouées en alternance. La durée du chrono et le nombre de " +
                "manches sont fixés par niveau :",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "• Assez facile, Émile (2 manches par mode) — Chiffres : cible ≤ 100, " +
                "addition et soustraction seulement, solution garantie, 120s. Lettres : " +
                "X, Y, Z, W, K, Q, H, J exclues, 110s.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "• Ça va encore, Nestor (3 manches par mode) — Chiffres : cible ≤ 100, " +
                "les 4 opérations, solution garantie, 100s. Lettres : X, Y, Z, W, K, Q " +
                "exclues, 90s.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "• Ça se complique, Monique (4 manches par mode) — " +
                "Chiffres : cible ≤ 200, les 4 opérations, pas de garantie de solution, " +
                "60s. Lettres : X, Y, Z, W exclues, 50s.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            "• Là c'est sérieux, Mathieu (5 manches par mode, comme le jeu télé) — " +
                "Chiffres : cible entre 100 et 999, pas de garantie de solution, 45s. " +
                "Lettres : aucune lettre exclue, 40s.",
            style = MaterialTheme.typography.bodyMedium,
        )

        HorizontalDivider()

        Text("Mode Défi", style = MaterialTheme.typography.titleMedium)
        Text(
            "Choisissez un mode (chiffres ou lettres) et un niveau, puis enchaînez les " +
                "manches pour aligner le plus de réussites possible d'affilée, avec le même " +
                "chrono qu'en partie solo pour ce niveau. Une réussite, c'est un compte exact " +
                "en chiffres, ou un mot valide dépassant une longueur minimale en lettres " +
                "(plus de 4 lettres sur Émile, 5 sur Nestor, 6 sur Monique, 7 sur Mathieu). En " +
                "chiffres, une solution exacte est toujours garantie, même sur Monique et " +
                "Mathieu : le défi ne s'arrête que sur une erreur ou un temps écoulé. La " +
                "série obtenue est enregistrée dans les statistiques.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
