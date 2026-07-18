package fr.pierre.chiffreslettres.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.BandeDoree
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.MarqueJeu
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/**
 * Avant d'entrer en Entraînement/Partie solo/Défi, demande confirmation du profil actif (retour
 * utilisateur) : "Oui" enchaîne normalement, "Non" redirige vers l'écran Profil au lieu d'y
 * accéder par erreur avec le mauvais profil actif.
 */
@Composable
fun MenuPrincipalScreen(
    pseudoActif: String,
    onEntrainementLibre: () -> Unit,
    onPartieStructuree: () -> Unit,
    onDefiSerie: () -> Unit,
    onDefiChrono: () -> Unit,
    onStatistiques: () -> Unit,
    onChangerProfil: () -> Unit,
    onAPropos: () -> Unit,
) {
    var actionEnAttente by remember { mutableStateOf<(() -> Unit)?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        MarqueJeu(modifier = Modifier.fillMaxWidth())
        BandeDoree(modifier = Modifier.padding(horizontal = 16.dp))
        PucePseudo(pseudoActif)

        TuilePrincipale("Entraînement", onClick = { actionEnAttente = onEntrainementLibre })
        TuilePrincipale("Partie solo", onClick = { actionEnAttente = onPartieStructuree })
        TuilePrincipale("Défi série", onClick = { actionEnAttente = onDefiSerie })
        TuilePrincipale("Défi chrono", onClick = { actionEnAttente = onDefiChrono })

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Ivory.copy(alpha = 0.15f))

        TuilePrincipale("Profil", onClick = onChangerProfil)
        TuilePrincipale("Statistiques", onClick = onStatistiques)
        TuilePrincipale("À propos", onClick = onAPropos)
    }

    actionEnAttente?.let { action ->
        AlertDialog(
            onDismissRequest = { actionEnAttente = null },
            title = { Text("Profil actif") },
            text = { Text("Continuer avec le profil actif, $pseudoActif ?") },
            confirmButton = {
                TextButton(onClick = {
                    actionEnAttente = null
                    action()
                }) { Text("Oui") }
            },
            dismissButton = {
                TextButton(onClick = {
                    actionEnAttente = null
                    onChangerProfil()
                }) { Text("Non") }
            },
        )
    }
}
