package fr.pierre.chiffreslettres.ui.partiereseau

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.network.ProfilReseau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale

/**
 * Écran final de cette sous-version : confirme la connexion établie avec l'adversaire. Pas de
 * bouton "Continuer vers le jeu" — la logique de jeu synchronisée n'existe pas encore (sous-version
 * suivante), un tel bouton serait un stub inutile pour l'instant.
 */
@Composable
fun ConfirmationConnexionScreen(profilDistant: ProfilReseau, onTerminer: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran("Connexion établie")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Connecté !", style = MaterialTheme.typography.titleLarge)
            PucePseudo("${profilDistant.avatar} ${profilDistant.pseudo}", grand = true)
            Text(
                "La partie en réseau n'est pas encore jouable : cet écran valide seulement la connexion.",
                style = MaterialTheme.typography.bodySmall,
            )
            TuilePrincipale("Terminer", onClick = onTerminer)
        }
    }
}
