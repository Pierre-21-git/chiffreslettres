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

/**
 * Liste des profils (retour utilisateur : plus d'onglets) : un simple choix de profil, qui mène
 * à sa fiche ([StatistiquesJoueurScreen]).
 */
@Composable
fun StatistiquesScreen(
    profilRepository: ProfilRepository,
    onProfilChoisi: (profilId: Long) -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Statistiques", onRetour)

        if (profils.isEmpty()) {
            Text("Aucun profil.", style = MaterialTheme.typography.bodyMedium)
        } else {
            for (profil in profils) {
                Button(onClick = { onProfilChoisi(profil.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text(profil.pseudo)
                }
            }
        }
    }
}

/**
 * Fiche d'un profil (retour utilisateur : simple menu, plus d'onglets) : accès à ses propres
 * statistiques ([MesStatistiquesScreen]), au classement général commun à tous les profils
 * ([StatistiquesGeneralesScreen]), à ses trophées, et à la réinitialisation.
 */
@Composable
fun StatistiquesJoueurScreen(
    profilId: Long,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    profilRepository: ProfilRepository,
    tropheeRepository: TropheeRepository,
    onMesStatistiques: () -> Unit,
    onStatistiquesGenerales: () -> Unit,
    onVoirTrophees: () -> Unit,
    onRetour: (() -> Unit)? = null,
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val profil = profils.find { it.id == profilId }
    val scope = rememberCoroutineScope()
    var confirmationReinitialisation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EnTeteEcran(profil?.pseudo ?: "Statistiques", onRetour)

        Button(onClick = onMesStatistiques, modifier = Modifier.fillMaxWidth()) {
            Text("Mes statistiques")
        }
        Button(onClick = onStatistiquesGenerales, modifier = Modifier.fillMaxWidth()) {
            Text("Statistiques générales")
        }

        HorizontalDivider()
        Button(onClick = onVoirTrophees, modifier = Modifier.fillMaxWidth()) {
            Text("Voir mes trophées")
        }

        HorizontalDivider()
        Button(onClick = { confirmationReinitialisation = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Réinitialiser mes statistiques")
        }
    }

    if (confirmationReinitialisation && profil != null) {
        AlertDialog(
            onDismissRequest = { confirmationReinitialisation = false },
            title = { Text("Réinitialiser mes statistiques") },
            text = {
                Text(
                    "Tout l'historique de parties, scores et défis de " +
                        "${profil.pseudo} sera définitivement supprimé. Continuer ?",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        historiqueRepository.reinitialiserHistoriqueJoueur(profilId)
                        defiRepository.reinitialiserJoueur(profilId)
                        tropheeRepository.reinitialiserJoueur(profilId)
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

/** Statistiques propres à un profil, par niveau (retour utilisateur : écran dédié, séparé de la fiche). */
@Composable
fun MesStatistiquesScreen(
    profilId: Long,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EnTeteEcran("Mes statistiques", onRetour)

        var premierBlocAffiche = true
        var uneDonneeAffichee = false
        for (niveau in Niveau.entries) {
            val affiche = StatistiquesJoueurNiveau(
                historiqueRepository,
                defiRepository,
                profilId,
                niveau,
                afficherSeparateurAvant = !premierBlocAffiche,
            )
            if (affiche) {
                uneDonneeAffichee = true
                premierBlocAffiche = false
            }
        }
        if (!uneDonneeAffichee) {
            Text("Aucune donnée enregistrée pour ce profil.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Classement général par niveau, commun à tous les profils (retour utilisateur : écran dédié, séparé de la fiche). */
@Composable
fun StatistiquesGeneralesScreen(
    historiqueRepository: HistoriqueRepository,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EnTeteEcran("Statistiques générales", onRetour)

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
    val meilleuresChronoChiffres by remember(profilId, niveau) {
        defiRepository.meilleuresPerformancesChronoParNiveau(profilId, ModeJeu.CHIFFRES, niveau.name)
    }.collectAsState(initial = emptyList())
    val meilleuresChronoLettres by remember(profilId, niveau) {
        defiRepository.meilleuresPerformancesChronoParNiveau(profilId, ModeJeu.LETTRES, niveau.name)
    }.collectAsState(initial = emptyList())

    val aDesDonnees = entrainementChiffres > 0 || entrainementLettres > 0 || partiesSolo > 0 ||
        meilleuresParties.isNotEmpty() || meilleursDefisChiffres.isNotEmpty() || meilleursDefisLettres.isNotEmpty() ||
        meilleuresChronoChiffres.isNotEmpty() || meilleuresChronoLettres.isNotEmpty()
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
        if (meilleuresChronoChiffres.isNotEmpty()) {
            Text("Défi chrono chiffres — meilleures performances", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleuresChronoChiffres.withIndex()) {
                Text(
                    "${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        if (meilleuresChronoLettres.isNotEmpty()) {
            Text("Défi chrono lettres — meilleures performances", style = MaterialTheme.typography.labelLarge)
            for ((position, defi) in meilleuresChronoLettres.withIndex()) {
                Text(
                    "${position + 1}. ${defi.serie} réussite(s) (${formatDate(defi.date)})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
    return true
}
