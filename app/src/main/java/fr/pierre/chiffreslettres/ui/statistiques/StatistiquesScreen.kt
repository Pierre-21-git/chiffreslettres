package fr.pierre.chiffreslettres.ui.statistiques

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ExportStatistiques
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.StatistiquesExport
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PalierArgent
import fr.pierre.chiffreslettres.ui.theme.PalierBronze
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val FORMAT_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).format(FORMAT_DATE)

/**
 * Fiche du profil actif (retour utilisateur : accès direct, plus de liste de profils à
 * parcourir — cloisonnement des profils, on ne voit que ses propres données) : accès à ses
 * propres statistiques ([MesStatistiquesScreen]), au classement général commun à tous les
 * profils ([StatistiquesGeneralesScreen]), à ses trophées, et à la réinitialisation.
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
    val context = LocalContext.current
    var confirmationReinitialisation by remember { mutableStateOf(false) }
    var uriImportEnAttente by remember { mutableStateOf<Uri?>(null) }
    var messageErreurFichier by remember { mutableStateOf<String?>(null) }

    val lanceurExport = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val export = ExportStatistiques(
                        sessions = historiqueRepository.exporterSessions(profilId),
                        defis = defiRepository.exporterDefis(profilId),
                        trophees = tropheeRepository.exporterTrophees(profilId),
                    )
                    val json = StatistiquesExport.versJson(export)
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                            ?: throw IOException("Impossible d'écrire le fichier.")
                    }
                } catch (e: IOException) {
                    messageErreurFichier = "L'export a échoué : ${e.message}"
                }
            }
        }
    }
    val lanceurImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) uriImportEnAttente = uri
    }

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
        Button(
            onClick = { lanceurExport.launch("statistiques-${nomFichier(profil?.pseudo)}.json") },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Exporter mes statistiques") }
        Button(
            onClick = { lanceurImport.launch(arrayOf("application/json")) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Importer mes statistiques") }
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

    uriImportEnAttente?.let { uri ->
        AlertDialog(
            onDismissRequest = { uriImportEnAttente = null },
            title = { Text("Importer mes statistiques") },
            text = {
                Text(
                    "Vos statistiques actuelles (parties, scores, défis et trophées) seront " +
                        "définitivement remplacées par celles du fichier importé. Continuer ?",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    uriImportEnAttente = null
                    scope.launch {
                        try {
                            val json = withContext(Dispatchers.IO) {
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                                    ?: throw IOException("Impossible de lire le fichier.")
                            }
                            val export = StatistiquesExport.depuisJson(json)
                            historiqueRepository.reinitialiserHistoriqueJoueur(profilId)
                            defiRepository.reinitialiserJoueur(profilId)
                            tropheeRepository.reinitialiserJoueur(profilId)
                            historiqueRepository.importerSessions(profilId, export.sessions)
                            defiRepository.importerDefis(profilId, export.defis)
                            tropheeRepository.importerTrophees(profilId, export.trophees)
                            tropheeRepository.reevaluer(profilId)
                        } catch (e: IllegalArgumentException) {
                            messageErreurFichier = e.message ?: "Fichier invalide."
                        } catch (e: IOException) {
                            messageErreurFichier = "L'import a échoué : ${e.message}"
                        }
                    }
                }) { Text("Remplacer") }
            },
            dismissButton = {
                TextButton(onClick = { uriImportEnAttente = null }) { Text("Annuler") }
            },
        )
    }

    messageErreurFichier?.let { message ->
        AlertDialog(
            onDismissRequest = { messageErreurFichier = null },
            title = { Text("Opération impossible") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { messageErreurFichier = null }) { Text("OK") }
            },
        )
    }
}

/** Nom de fichier suggéré pour l'export, sans caractères qui poseraient problème selon le système de fichiers. */
private fun nomFichier(pseudo: String?): String =
    (pseudo ?: "profil").map { if (it.isLetterOrDigit()) it else '_' }.joinToString("")

/**
 * Mêmes podiums que [StatistiquesGeneralesScreen], mais uniquement les meilleurs scores de ce
 * profil (retour utilisateur : même page, sans le détail entraînement/défi d'avant).
 */
@Composable
fun MesStatistiquesScreen(
    profilId: Long,
    historiqueRepository: HistoriqueRepository,
    onRetour: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        EnTeteEcran("Mes statistiques", onRetour)

        Text(
            "Mes meilleurs scores par niveau (parties classiques, chiffres et lettres confondus)",
            style = MaterialTheme.typography.titleMedium,
        )
        for ((position, niveau) in Niveau.entries.withIndex()) {
            val meilleuresFlow = remember(niveau) { historiqueRepository.meilleuresPartiesSoloParNiveau(profilId, niveau.name) }
            val meilleures by meilleuresFlow.collectAsState(initial = emptyList())
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(niveau.label, style = MaterialTheme.typography.titleSmall)
                Podium(meilleures.map { EntreePodium(null, it.score, it.date) })
            }
            if (position != Niveau.entries.lastIndex) HorizontalDivider()
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
            "Classement par niveau (parties classiques, chiffres et lettres confondus)",
            style = MaterialTheme.typography.titleMedium,
        )
        for ((position, niveau) in Niveau.entries.withIndex()) {
            val classementFlow = remember(niveau) { historiqueRepository.classementParNiveau(niveau.name) }
            val classement by classementFlow.collectAsState(initial = emptyList())
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(niveau.label, style = MaterialTheme.typography.titleSmall)
                Podium(classement.map { EntreePodium("${it.avatar} ${it.pseudo}", it.score, it.date) })
            }
            if (position != Niveau.entries.lastIndex) HorizontalDivider()
        }
    }
}

/** [label] à null pour "mes statistiques" (toujours le joueur courant, pas besoin de le nommer). */
private data class EntreePodium(val label: String?, val score: Int, val date: Long)

/** Podium des 3 meilleurs scores (retour utilisateur), remplace l'ancienne liste numérotée. */
@Composable
private fun Podium(entrees: List<EntreePodium>) {
    if (entrees.isEmpty()) {
        Text("Aucun score enregistré.", style = MaterialTheme.typography.bodyMedium)
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom,
    ) {
        for (rang in listOf(1, 0, 2)) {
            val entree = entrees.getOrNull(rang) ?: continue
            MarchePodium(rang, entree, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MarchePodium(rang: Int, entree: EntreePodium, modifier: Modifier = Modifier) {
    val (medaille, couleur, hauteur) = when (rang) {
        0 -> Triple("🥇", BrassBright, 88.dp)
        1 -> Triple("🥈", PalierArgent, 66.dp)
        else -> Triple("🥉", PalierBronze, 48.dp)
    }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(medaille, fontSize = 22.sp)
        if (entree.label != null) {
            Text(entree.label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, maxLines = 1)
        }
        Text("${entree.score} pts", color = Ivory, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hauteur)
                .background(couleur.copy(alpha = 0.3f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .border(1.5.dp, couleur, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        )
        Text(formatDate(entree.date), style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}
