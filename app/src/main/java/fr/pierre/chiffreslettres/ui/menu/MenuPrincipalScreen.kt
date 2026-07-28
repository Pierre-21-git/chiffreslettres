package fr.pierre.chiffreslettres.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.BandeDoree
import fr.pierre.chiffreslettres.ui.theme.BandeauProfilAccueil
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.MarqueJeu
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/**
 * Plus de confirmation de profil actif ici (retour utilisateur) : redondant depuis que l'écran
 * de sélection de profil s'affiche déjà une fois à chaque lancement de l'app (v1.26).
 */
@Composable
fun MenuPrincipalScreen(
    pseudoActif: String,
    onEntrainementLibre: () -> Unit,
    onPartieStructuree: () -> Unit,
    onDefiSerie: () -> Unit,
    onDefiChrono: () -> Unit,
    onDefiQuotidien: () -> Unit,
    onStatistiques: () -> Unit,
    onChangerProfil: () -> Unit,
    onAPropos: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        MarqueJeu(modifier = Modifier.fillMaxWidth())
        BandeDoree(modifier = Modifier.padding(horizontal = 16.dp))
        BandeauProfilAccueil(pseudoActif, onClick = onChangerProfil)

        TuilePrincipale("Entraînement", onClick = onEntrainementLibre)
        TuilePrincipale("Partie classique", onClick = onPartieStructuree)
        TuilePrincipale("Défi série", onClick = onDefiSerie)
        TuilePrincipale("Défi chrono", onClick = onDefiChrono)
        TuilePrincipale("Défi quotidien", onClick = onDefiQuotidien)

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Ivory.copy(alpha = 0.15f))

        TuilePrincipale("Profil", onClick = onChangerProfil)
        TuilePrincipale("Statistiques", onClick = onStatistiques)
        TuilePrincipale("À propos", onClick = onAPropos)
    }
}
