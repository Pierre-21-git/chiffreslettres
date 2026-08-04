package fr.pierre.chiffreslettres.ui.defi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

/**
 * Choix du niveau du défi sans faute (retour utilisateur) : un seul niveau pour les deux modes
 * (alternance stricte chiffres/lettres), pas de section séparée par mode comme `ChoixDefiScreen` —
 * `Niveau` et `NiveauLettres` partagent les mêmes noms (EMILE/NESTOR/MONIQUE/MATHIEU), donc [Niveau]
 * suffit à représenter le choix, résolu dans les deux enums côté navigation.
 */
@Composable
fun ChoixDefiSansFauteScreen(
    pseudoActif: String,
    onNiveauChoisi: (Niveau) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.defi_type_sans_faute), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)

        for (niveau in Niveau.entries) {
            Button(onClick = { onNiveauChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                Text(niveau.libelle())
            }
        }
    }
}
