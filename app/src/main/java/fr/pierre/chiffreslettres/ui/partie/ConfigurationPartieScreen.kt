package fr.pierre.chiffreslettres.ui.partie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.LienReglesDuJeu
import fr.pierre.chiffreslettres.ui.apropos.ReglesModePartieSolo
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

/**
 * Un seul choix de niveau, appliqué aux manches chiffres et lettres (retour utilisateur).
 * Le nombre de manches de chaque mode est fixe par niveau (`manchesParMode`, retour
 * utilisateur — pas de réglage à faire).
 */
@Composable
fun ConfigurationPartieScreen(
    pseudoActif: String,
    onDemarrer: (List<ManchePlanifiee>) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.configuration_partie_solo_titre), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)
        LienReglesDuJeu { ReglesModePartieSolo() }
        Text(stringResource(R.string.configuration_choisir_niveau), style = MaterialTheme.typography.titleMedium)

        for (niveau in Niveau.entries) {
            Button(
                onClick = {
                    val niveauLettres = NiveauLettres.valueOf(niveau.name)
                    onDemarrer(
                        sequenceAlternee(niveau.manchesParMode, niveauLettres, niveau.manchesParMode, niveau),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(niveau.libelle()) }
        }
    }
}

/**
 * Répartit les manches lettres et chiffres en alternance (spec §6.2). Quand les deux
 * comptes sont égaux (ce qui est toujours le cas, un seul niveau étant choisi pour les
 * deux modes), ça donne une stricte alternance L/C.
 */
internal fun sequenceAlternee(
    nombreLettres: Int,
    niveauLettres: NiveauLettres,
    nombreChiffres: Int,
    niveauChiffres: Niveau,
): List<ManchePlanifiee> {
    val sequence = mutableListOf<ManchePlanifiee>()
    var prisesLettres = 0
    var prisesChiffres = 0
    repeat(nombreLettres + nombreChiffres) {
        val prendreLettres = when {
            prisesLettres >= nombreLettres -> false
            prisesChiffres >= nombreChiffres -> true
            else -> prisesLettres.toDouble() / nombreLettres <= prisesChiffres.toDouble() / nombreChiffres
        }
        if (prendreLettres) {
            sequence += ManchePlanifiee.Lettres(niveauLettres)
            prisesLettres++
        } else {
            sequence += ManchePlanifiee.Chiffres(niveauChiffres)
            prisesChiffres++
        }
    }
    return sequence
}
