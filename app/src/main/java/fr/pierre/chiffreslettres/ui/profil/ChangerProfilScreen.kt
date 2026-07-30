package fr.pierre.chiffreslettres.ui.profil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.ui.theme.BandeDoree
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.MarqueJeu
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.couleurRangJoueur
import kotlinx.coroutines.launch

@Composable
fun ChangerProfilScreen(
    profilRepository: ProfilRepository,
    profilActifStore: ProfilActifStore,
    tropheeRepository: TropheeRepository,
    onProfilChoisi: () -> Unit,
    onCreerNouveauProfil: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var profilARenommer by remember { mutableStateOf<ProfilEntity?>(null) }
    var profilASupprimer by remember { mutableStateOf<ProfilEntity?>(null) }

    Box(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MarqueJeu(modifier = Modifier.fillMaxWidth())
            BandeDoree(modifier = Modifier.padding(horizontal = 16.dp))
        }
        // Bloc profil (titre + vignettes + bouton) centré par rapport à toute la hauteur de
        // l'écran (retour utilisateur : pas seulement dans l'espace restant sous le titre du
        // jeu, qui le poussait trop bas) ; pleine largeur, avec son propre scroll si la liste
        // de profils dépasse l'espace disponible.
        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EnTeteEcran(stringResource(R.string.changer_profil_titre), centre = true)
            for (profil in profils) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    PucePseudo(
                        pseudo = "${profil.avatar} ${profil.pseudo}",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                profilActifStore.definirProfilActif(profil.id)
                                onProfilChoisi()
                            }
                        },
                        couleurRang = couleurRangJoueur(profil.id, tropheeRepository),
                    )
                    IconButton(onClick = { profilARenommer = profil }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_renommer))
                    }
                    IconButton(onClick = { profilASupprimer = profil }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_supprimer))
                    }
                }
            }
            Button(onClick = onCreerNouveauProfil, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.changer_profil_creer_nouveau)) }
        }
    }

    profilARenommer?.let { profil ->
        var nouveauPseudo by remember(profil.id) { mutableStateOf(profil.pseudo) }
        var nouvelAvatar by remember(profil.id) { mutableStateOf(profil.avatar) }
        AlertDialog(
            onDismissRequest = { profilARenommer = null },
            title = { Text(stringResource(R.string.changer_profil_renommer_titre)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = nouveauPseudo, onValueChange = { nouveauPseudo = it })
                    SelecteurAvatar(avatarSelectionne = nouvelAvatar, onAvatarChoisi = { nouvelAvatar = it })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val nom = nouveauPseudo.trim()
                    if (nom.isNotEmpty()) {
                        scope.launch {
                            profilRepository.renommerProfil(profil.id, nom)
                            profilRepository.definirAvatar(profil.id, nouvelAvatar)
                        }
                    }
                    profilARenommer = null
                }) { Text(stringResource(R.string.action_valider)) }
            },
            dismissButton = {
                TextButton(onClick = { profilARenommer = null }) { Text(stringResource(R.string.action_annuler)) }
            },
        )
    }

    profilASupprimer?.let { profil ->
        AlertDialog(
            onDismissRequest = { profilASupprimer = null },
            title = { Text(stringResource(R.string.changer_profil_supprimer_titre)) },
            text = { Text(stringResource(R.string.changer_profil_supprimer_message, profil.pseudo)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { profilRepository.supprimerProfil(profil) }
                    profilASupprimer = null
                }) { Text(stringResource(R.string.action_supprimer)) }
            },
            dismissButton = {
                TextButton(onClick = { profilASupprimer = null }) { Text(stringResource(R.string.action_annuler)) }
            },
        )
    }
}
