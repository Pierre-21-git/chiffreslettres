package fr.pierre.chiffreslettres.ui.trophees

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.Trophee
import fr.pierre.chiffreslettres.data.libelleCourtRes
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PalierEasterEgg
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.couleurPalier

/**
 * Affiché en fin de manche/partie quand `TropheeRepository.reevaluer` retourne au moins un
 * trophée nouvellement débloqué (retour utilisateur) — au-dessus de l'écran de récap habituel,
 * pas à sa place. [nomJoueur] identifie le joueur concerné (retour utilisateur : indispensable
 * en duo, où deux dialogues successifs peuvent s'afficher pour deux joueurs différents). Ne se
 * ferme que via le bouton "Continuer" (retour utilisateur : un tapotement à côté ne doit pas
 * fermer le dialogue par erreur avant d'avoir vu tous les trophées).
 */
@Composable
fun TropheesDebloquesDialog(trophees: List<Trophee>, nomJoueur: String? = null, onDismiss: () -> Unit) {
    if (trophees.isEmpty()) return
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
        title = {
            Column {
                Text(
                    stringResource(
                        if (trophees.size == 1) R.string.trophees_debloques_titre_singulier else R.string.trophees_debloques_titre_pluriel,
                    ),
                )
                if (nomJoueur != null) {
                    Text(nomJoueur, style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                for (trophee in trophees) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val couleur = trophee.palier?.let { couleurPalier(it) } ?: PalierEasterEgg
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(couleur.copy(alpha = 0.35f), CircleShape)
                                .border(2.dp, couleur, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(CatalogueTrophees.iconeTrophee(trophee.id), fontSize = 20.sp)
                        }
                        Column {
                            Text(texteTrophee(trophee.titreRes, trophee.titreArgs), color = Ivory)
                            trophee.palier?.let {
                                Text(
                                    stringResource(it.libelleCourtRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = couleur,
                                )
                            } ?: Text(
                                texteTrophee(trophee.descriptionRes, trophee.descriptionArgs),
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_continuer)) }
        },
    )
}
