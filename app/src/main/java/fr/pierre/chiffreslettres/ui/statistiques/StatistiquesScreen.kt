package fr.pierre.chiffreslettres.ui.statistiques

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

val FORMAT_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).format(FORMAT_DATE)

private val ONGLETS_STATISTIQUES = listOf("Général", "Joueurs")

/**
 * Écran unique à deux onglets (retour utilisateur) : "Général" pour le classement commun à tous
 * les joueurs, "Joueurs" pour le détail par profil (fusion de l'ancien écran dédié
 * "Statistiques par joueur"). Le reset est désormais scopé à un seul joueur, depuis l'onglet
 * Joueurs — plus de bouton de réinitialisation globale.
 */
@Composable
fun StatistiquesScreen(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilRepository: ProfilRepository,
    tropheeRepository: TropheeRepository,
    onVoirTrophees: (profilId: Long) -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    var ongletSelectionne by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Statistiques", onRetour)

        TabRow(selectedTabIndex = ongletSelectionne) {
            ONGLETS_STATISTIQUES.forEachIndexed { index, titre ->
                Tab(
                    selected = ongletSelectionne == index,
                    onClick = { ongletSelectionne = index },
                    text = { Text(titre) },
                )
            }
        }

        when (ongletSelectionne) {
            0 -> OngletGeneral(historiqueRepository)
            else -> OngletJoueurs(historiqueRepository, defiRepository, profilRepository, tropheeRepository, onVoirTrophees)
        }
    }
}

@Composable
private fun OngletGeneral(historiqueRepository: HistoriqueRepository) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            "Classement par niveau (parties solo, chiffres et lettres confondus)",
            style = MaterialTheme.typography.titleMedium,
        )
        for ((position, niveau) in Niveau.entries.withIndex()) {
            val classementFlow = remember(niveau) { historiqueRepository.classementParNiveau(niveau.name) }
            val classement by classementFlow.collectAsState(initial = emptyList())
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(niveau.label, style = MaterialTheme.typography.titleSmall)
                if (classement.isEmpty()) {
                    Text("Aucun score enregistré.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    for ((rang, ligne) in classement.withIndex()) {
                        Text(
                            "${rang + 1}. ${ligne.pseudo} — ${ligne.score} points (${formatDate(ligne.date)})",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            if (position != Niveau.entries.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun OngletJoueurs(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilRepository: ProfilRepository,
    tropheeRepository: TropheeRepository,
    onVoirTrophees: (profilId: Long) -> Unit,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    var profilSelectionneId by remember { mutableStateOf<Long?>(null) }
    val profilSelectionne = profils.find { it.id == profilSelectionneId } ?: profils.firstOrNull()
    val scope = rememberCoroutineScope()
    var confirmationReinitialisation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
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
            Text("Aucun profil.", style = MaterialTheme.typography.bodyMedium)
        } else {
            HorizontalDivider()

            var premierBlocAffiche = true
            var uneDonneeAffichee = false
            for (niveau in Niveau.entries) {
                val affiche = StatistiquesJoueurNiveau(
                    historiqueRepository,
                    defiRepository,
                    profilSelectionne.id,
                    niveau,
                    afficherSeparateurAvant = !premierBlocAffiche,
                )
                if (affiche) {
                    uneDonneeAffichee = true
                    premierBlocAffiche = false
                }
            }
            if (!uneDonneeAffichee) {
                Text("Aucune donnée enregistrée pour ce joueur.", style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider()
            Button(onClick = { onVoirTrophees(profilSelectionne.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Voir mes trophées")
            }

            HorizontalDivider()
            Button(onClick = { confirmationReinitialisation = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Réinitialiser mes statistiques")
            }
        }
    }

    if (confirmationReinitialisation && profilSelectionne != null) {
        AlertDialog(
            onDismissRequest = { confirmationReinitialisation = false },
            title = { Text("Réinitialiser mes statistiques") },
            text = {
                Text(
                    "Tout l'historique de parties, scores et défis de " +
                        "${profilSelectionne.pseudo} sera définitivement supprimé. Continuer ?",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        historiqueRepository.reinitialiserHistoriqueJoueur(profilSelectionne.id)
                        defiRepository.reinitialiserJoueur(profilSelectionne.id)
                        tropheeRepository.reinitialiserJoueur(profilSelectionne.id)
                    }
                    confirmationReinitialisation = false
                }) { Text("Réinitialiser") }
            },
            dismissButton = {
                TextButton(onClick = { confirmationReinitialisation = false }) { Text("Annuler") }
            },
        )
    }
}

/** Affiche le bloc de stats du niveau s'il comporte des données, et renvoie s'il a été affiché. */
@Composable
private fun StatistiquesJoueurNiveau(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilId: Long,
    niveau: Niveau,
    afficherSeparateurAvant: Boolean,
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

    if (afficherSeparateurAvant) HorizontalDivider()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(niveau.label, style = MaterialTheme.typography.titleSmall)
        Text("Entraînement chiffres : $entrainementChiffres manche(s)", style = MaterialTheme.typography.bodyMedium)
        Text("Entraînement lettres : $entrainementLettres manche(s)", style = MaterialTheme.typography.bodyMedium)
        Text("Parties solo jouées : $partiesSolo", style = MaterialTheme.typography.bodyMedium)
        if (meilleuresParties.isNotEmpty()) {
            Text("3 meilleures parties solo", style = MaterialTheme.typography.labelLarge)
            for ((position, partie) in meilleuresParties.withIndex()) {
                Text(
                    "${position + 1}. ${partie.score} points (${formatDate(partie.date)})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        if (meilleursDefisChiffres.isNotEmpty()) {
            Text("Défi chiffres — meilleures séries", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleursDefisChiffres.withIndex()) {
                Text(
                    "${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        if (meilleursDefisLettres.isNotEmpty()) {
            Text("Défi lettres — meilleures séries", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleursDefisLettres.withIndex()) {
                Text(
                    "${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
    return true
}
