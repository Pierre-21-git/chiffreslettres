package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R

/**
 * Dialogue générique "Règles du jeu" (retour utilisateur) : affiche une ou plusieurs sections de
 * [ReglesDuJeuScreen] dans une fenêtre modale scrollable, sans quitter l'écran de choix de niveau.
 */
@Composable
fun ReglesDialog(onDismiss: () -> Unit, contenu: @Composable ColumnScope.() -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_fermer)) }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                contenu()
            }
        },
    )
}

/**
 * Lien "Règles du jeu" (retour utilisateur) : à placer sur chaque écran de choix de niveau, ouvre
 * [contenu] (une ou plusieurs sections `ReglesMode...` de [ReglesDuJeuScreen]) dans [ReglesDialog].
 */
@Composable
fun LienReglesDuJeu(modifier: Modifier = Modifier, contenu: @Composable ColumnScope.() -> Unit) {
    var afficher by remember { mutableStateOf(false) }
    TextButton(onClick = { afficher = true }, modifier = modifier) {
        Text(stringResource(R.string.regles_titre))
    }
    if (afficher) {
        ReglesDialog(onDismiss = { afficher = false }, contenu = contenu)
    }
}
