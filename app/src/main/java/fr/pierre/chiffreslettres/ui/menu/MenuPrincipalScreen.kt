package fr.pierre.chiffreslettres.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.ui.theme.BandeDoree
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.MarqueJeu
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.couleurRangJoueur
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/**
 * Plus de confirmation de profil actif ici (retour utilisateur) : redondant depuis que l'écran
 * de sélection de profil s'affiche déjà une fois à chaque lancement de l'app (v1.26). Plus de
 * tuile "Profil" séparée non plus : le bandeau avatar/pseudo lui-même est cliquable pour changer
 * de profil (retour utilisateur, redondance retirée).
 */
@Composable
fun MenuPrincipalScreen(
    pseudoActif: String,
    profilId: Long,
    tropheeRepository: TropheeRepository,
    onEntrainementLibre: () -> Unit,
    onPartieStructuree: () -> Unit,
    onPartieDuo: () -> Unit,
    onPartieReseau: () -> Unit,
    onDefiSerie: () -> Unit,
    onDefiChrono: () -> Unit,
    onDefiQuotidien: () -> Unit,
    onStatistiques: () -> Unit,
    onChangerProfil: () -> Unit,
    onAPropos: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        MarqueJeu(modifier = Modifier.fillMaxWidth())
        BandeDoree(modifier = Modifier.padding(horizontal = 16.dp))
        PucePseudo(
            pseudoActif,
            onClick = onChangerProfil,
            grand = true,
            couleurRang = couleurRangJoueur(profilId, tropheeRepository),
        )

        TuilePrincipale(stringResource(R.string.entrainement_titre), onClick = onEntrainementLibre)

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Ivory.copy(alpha = 0.15f))

        TuilePrincipale(stringResource(R.string.menu_partie_solo), onClick = onPartieStructuree)
        TuilePrincipale(stringResource(R.string.menu_partie_duo), onClick = onPartieDuo)
        TuilePrincipale(stringResource(R.string.menu_partie_reseau), onClick = onPartieReseau)
        TuilePrincipale(stringResource(R.string.defi_type_serie), onClick = onDefiSerie)
        TuilePrincipale(stringResource(R.string.defi_type_chrono), onClick = onDefiChrono)
        TuilePrincipale(stringResource(R.string.defi_quotidien_titre), onClick = onDefiQuotidien)

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Ivory.copy(alpha = 0.15f))

        TuilePrincipale(stringResource(R.string.statistiques_titre_defaut), onClick = onStatistiques)
        TuilePrincipale(stringResource(R.string.apropos_titre), onClick = onAPropos)

        // Marge de fin dédiée à la barre système (retour utilisateur : la tuile "À propos"
        // passait encore un peu sous la barre malgré systemBarsPadding() en amont) : réservée en
        // plus du padding(24.dp) ci-dessus, pas à sa place, pour ne pas retoucher l'espacement
        // des autres écrans.
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
