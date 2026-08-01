package fr.pierre.chiffreslettres.ui.partieduo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.partie.ManchePlanifiee
import fr.pierre.chiffreslettres.ui.partie.sequenceAlternee
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.description
import fr.pierre.chiffreslettres.ui.theme.libelle

/**
 * Configuration d'une partie duo (retour utilisateur) : choix de l'adversaire (parmi les autres
 * profils du foyer), du niveau (comme en solo, un seul niveau pour chiffres et lettres), et du
 * mode de calcul des points (Duo = barème indépendant, Confrontation = comparatif).
 */
@Composable
fun ConfigurationPartieDuoScreen(
    pseudoActif: String,
    autresProfils: List<ProfilEntity>,
    onDemarrer: (profil2Id: Long, sequence: List<ManchePlanifiee>, mode: ModeScoreDuo) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    var profil2 by remember { mutableStateOf<ProfilEntity?>(null) }
    var niveau by remember { mutableStateOf<Niveau?>(null) }
    var mode by remember { mutableStateOf<ModeScoreDuo?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.configuration_duo_titre), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)

        if (autresProfils.isEmpty()) {
            Text(
                stringResource(R.string.configuration_duo_pas_de_second_profil),
                style = MaterialTheme.typography.bodyMedium,
            )
            return@Column
        }

        Text(stringResource(R.string.configuration_duo_contre_qui), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in autresProfils) {
                val selectionne = candidat.id == profil2?.id
                BoutonChoix(
                    texte = "${candidat.avatar} ${candidat.pseudo}",
                    selectionne = selectionne,
                    onClick = { profil2 = candidat },
                )
            }
        }

        Text(stringResource(R.string.configuration_quel_niveau), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in Niveau.entries) {
                BoutonChoix(
                    texte = candidat.libelle(),
                    selectionne = candidat == niveau,
                    onClick = { niveau = candidat },
                )
            }
        }

        Text(stringResource(R.string.configuration_quel_mode), style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (candidat in ModeScoreDuo.entries) {
                BoutonChoix(
                    texte = candidat.libelle(),
                    selectionne = candidat == mode,
                    onClick = { mode = candidat },
                )
            }
        }
        val modeChoisi = mode
        if (modeChoisi != null) {
            Text(modeChoisi.description(), style = MaterialTheme.typography.bodySmall)
        }

        val niveauChoisi = niveau
        Button(
            onClick = {
                val profil2Choisi = profil2 ?: return@Button
                val niveauChoisi2 = niveauChoisi ?: return@Button
                val modeChoisi2 = modeChoisi ?: return@Button
                val niveauLettres = NiveauLettres.valueOf(niveauChoisi2.name)
                val sequence = sequenceAlternee(
                    niveauChoisi2.manchesParMode,
                    niveauLettres,
                    niveauChoisi2.manchesParMode,
                    niveauChoisi2,
                )
                onDemarrer(profil2Choisi.id, sequence, modeChoisi2)
            },
            enabled = profil2 != null && niveauChoisi != null && modeChoisi != null,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.configuration_duo_demarrer)) }
    }
}

@Composable
private fun BoutonChoix(texte: String, selectionne: Boolean, onClick: () -> Unit) {
    if (selectionne) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texte) }
    }
}
