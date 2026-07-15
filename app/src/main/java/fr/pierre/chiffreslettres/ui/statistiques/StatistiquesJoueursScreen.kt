package fr.pierre.chiffreslettres.ui.statistiques

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran

/**
 * Détail des statistiques d'un joueur, regroupées par niveau (retour utilisateur) : un
 * sélecteur de profil affiche les stats d'un seul joueur à la fois, et seuls les niveaux
 * comportant des données sont affichés.
 */
@Composable
fun StatistiquesJoueursScreen(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilRepository: ProfilRepository,
    onRetour: (() -> Unit)? = null,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    var profilSelectionneId by remember { mutableStateOf<Long?>(null) }
    val profilSelectionne = profils.find { it.id == profilSelectionneId } ?: profils.firstOrNull()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Statistiques par joueur", onRetour)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (profil in profils) {
                if (profil.id == profilSelectionne?.id) {
                    Button(onClick = { profilSelectionneId = profil.id }) { Text(profil.pseudo) }
                } else {
                    OutlinedButton(onClick = { profilSelectionneId = profil.id }) { Text(profil.pseudo) }
                }
            }
        }

        if (profilSelectionne == null) {
            Text("Aucun profil.")
        } else {
            var uneDonneeAffichee = false
            for (niveau in Niveau.entries) {
                if (StatistiquesJoueurNiveau(historiqueRepository, defiRepository, profilSelectionne.id, niveau)) {
                    uneDonneeAffichee = true
                }
            }
            if (!uneDonneeAffichee) {
                Text("Aucune donnée enregistrée pour ce joueur.")
            }
        }
    }
}

/** Affiche le bloc de stats du niveau s'il comporte des données, et renvoie s'il a été affiché. */
@Composable
private fun StatistiquesJoueurNiveau(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilId: Long,
    niveau: Niveau,
): Boolean {
    val entrainementChiffres by remember(profilId, niveau) {
        historiqueRepository.compterManchesEntrainementParNiveau(profilId, ModeJeu.CHIFFRES, niveau.name)
    }.collectAsState(initial = 0)
    val entrainementLettres by remember(profilId, niveau) {
        historiqueRepository.compterManchesEntrainementParNiveau(profilId, ModeJeu.LETTRES, niveau.name)
    }.collectAsState(initial = 0)
    val partiesSolo by remember(profilId, niveau) {
        historiqueRepository.compterPartiesSoloParNiveau(profilId, niveau.name)
    }.collectAsState(initial = 0)
    val meilleuresParties by remember(profilId, niveau) {
        historiqueRepository.meilleuresPartiesSoloParNiveau(profilId, niveau.name)
    }.collectAsState(initial = emptyList())
    val meilleursDefisChiffres by remember(profilId, niveau) {
        defiRepository.meilleursDefisParNiveau(profilId, ModeJeu.CHIFFRES, niveau.name)
    }.collectAsState(initial = emptyList())
    val meilleursDefisLettres by remember(profilId, niveau) {
        defiRepository.meilleursDefisParNiveau(profilId, ModeJeu.LETTRES, niveau.name)
    }.collectAsState(initial = emptyList())

    val aDesDonnees = entrainementChiffres > 0 || entrainementLettres > 0 || partiesSolo > 0 ||
        meilleuresParties.isNotEmpty() || meilleursDefisChiffres.isNotEmpty() || meilleursDefisLettres.isNotEmpty()
    if (!aDesDonnees) return false

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(niveau.label, style = MaterialTheme.typography.titleSmall)
        Text("Entraînement chiffres : $entrainementChiffres manche(s)")
        Text("Entraînement lettres : $entrainementLettres manche(s)")
        Text("Parties solo jouées : $partiesSolo")
        if (meilleuresParties.isNotEmpty()) {
            Text("3 meilleures parties solo", style = MaterialTheme.typography.labelLarge)
            for ((position, partie) in meilleuresParties.withIndex()) {
                Text("${position + 1}. ${partie.score} points (${formatDate(partie.date)})")
            }
        }
        if (meilleursDefisChiffres.isNotEmpty()) {
            Text("Défi chiffres — meilleures séries", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleursDefisChiffres.withIndex()) {
                Text("${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})")
            }
        }
        if (meilleursDefisLettres.isNotEmpty()) {
            Text("Défi lettres — meilleures séries", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleursDefisLettres.withIndex()) {
                Text("${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})")
            }
        }
    }
    return true
}
