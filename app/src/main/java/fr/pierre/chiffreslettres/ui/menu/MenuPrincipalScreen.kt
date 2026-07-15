package fr.pierre.chiffreslettres.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.ui.theme.BandeDoree
import fr.pierre.chiffreslettres.ui.theme.LienDiscret
import fr.pierre.chiffreslettres.ui.theme.MarqueJeu
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

@Composable
fun MenuPrincipalScreen(
    pseudoActif: String,
    onEntrainementLibre: () -> Unit,
    onPartieStructuree: () -> Unit,
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
        PucePseudo(pseudoActif, modifier = Modifier.align(Alignment.CenterHorizontally))

        TuilePrincipale("Entraînement", onClick = onEntrainementLibre)
        TuilePrincipale("Partie", onClick = onPartieStructuree)
        TuilePrincipale("Statistiques", onClick = onStatistiques)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            LienDiscret("Profil", onClick = onChangerProfil)
            Spacer(Modifier.width(18.dp))
            LienDiscret("À propos", onClick = onAPropos)
        }
    }
}
