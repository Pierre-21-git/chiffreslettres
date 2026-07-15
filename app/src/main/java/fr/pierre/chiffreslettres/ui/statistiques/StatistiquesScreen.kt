package fr.pierre.chiffreslettres.ui.statistiques

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

private val FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).format(FORMAT_DATE)

@Composable
fun StatistiquesScreen(
    historiqueRepository: HistoriqueRepository,
    profilRepository: ProfilRepository,
    onRetour: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    var confirmationReinitialisation by remember { mutableStateOf(false) }
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Statistiques", onRetour)

        Text(
            "Classement par niveau (parties solo, chiffres et lettres confondus)",
            style = MaterialTheme.typography.titleMedium,
        )
        for (niveau in Niveau.entries) {
            val classementFlow = remember(niveau) { historiqueRepository.classementParNiveau(niveau.name) }
            val classement by classementFlow.collectAsState(initial = emptyList())
            Text(niveau.label, style = MaterialTheme.typography.titleSmall)
            if (classement.isEmpty()) {
                Text("Aucun score enregistré.")
            } else {
                for ((position, ligne) in classement.withIndex()) {
                    Text("${position + 1}. ${ligne.pseudo} — ${ligne.score} points (${formatDate(ligne.date)})")
                }
            }
        }

        HorizontalDivider()
        Text("Statistiques par joueur", style = MaterialTheme.typography.titleMedium)
        for (profil in profils) {
            StatistiquesJoueur(historiqueRepository, profil.id, profil.pseudo)
        }

        HorizontalDivider()
        Button(onClick = { confirmationReinitialisation = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Réinitialiser les statistiques")
        }
    }

    if (confirmationReinitialisation) {
        AlertDialog(
            onDismissRequest = { confirmationReinitialisation = false },
            title = { Text("Réinitialiser les statistiques") },
            text = { Text("Tout l'historique des parties et scores sera définitivement supprimé, pour tous les joueurs. Continuer ?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { historiqueRepository.reinitialiserHistorique() }
                    confirmationReinitialisation = false
                }) { Text("Réinitialiser") }
            },
            dismissButton = {
                TextButton(onClick = { confirmationReinitialisation = false }) { Text("Annuler") }
            },
        )
    }
}

@Composable
private fun StatistiquesJoueur(historiqueRepository: HistoriqueRepository, profilId: Long, pseudo: String) {
    val entrainementChiffres by remember(profilId) {
        historiqueRepository.compterManchesEntrainement(profilId, ModeJeu.CHIFFRES)
    }.collectAsState(initial = 0)
    val entrainementLettres by remember(profilId) {
        historiqueRepository.compterManchesEntrainement(profilId, ModeJeu.LETTRES)
    }.collectAsState(initial = 0)
    val partiesSolo by remember(profilId) {
        historiqueRepository.compterPartiesSolo(profilId)
    }.collectAsState(initial = 0)
    val meilleursChiffres by remember(profilId) {
        historiqueRepository.meilleuresManches(profilId, ModeJeu.CHIFFRES)
    }.collectAsState(initial = emptyList())
    val meilleursLettres by remember(profilId) {
        historiqueRepository.meilleuresManches(profilId, ModeJeu.LETTRES)
    }.collectAsState(initial = emptyList())

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(pseudo, style = MaterialTheme.typography.titleSmall)
        Text("Entraînement : $entrainementChiffres manche(s) chiffres, $entrainementLettres manche(s) lettres")
        Text("Parties solo jouées : $partiesSolo")

        Text("5 meilleurs chiffres", style = MaterialTheme.typography.labelLarge)
        if (meilleursChiffres.isEmpty()) {
            Text("Aucun score enregistré.")
        } else {
            for ((position, manche) in meilleursChiffres.withIndex()) {
                Text("${position + 1}. ${manche.score} points (${formatDate(manche.date)})")
            }
        }

        Text("5 meilleurs lettres", style = MaterialTheme.typography.labelLarge)
        if (meilleursLettres.isEmpty()) {
            Text("Aucun score enregistré.")
        } else {
            for ((position, manche) in meilleursLettres.withIndex()) {
                Text("${position + 1}. ${manche.score} points (${formatDate(manche.date)})")
            }
        }
    }
}
