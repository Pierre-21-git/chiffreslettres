package fr.pierre.chiffreslettres.ui.statistiques

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.ExportStatistiques
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.StatistiquesExport
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PalierArgent
import fr.pierre.chiffreslettres.ui.theme.PalierBronze
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.libelle
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatistiquesJoueurScreen(
    profilId: Long,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    defiQuotidienRepository: DefiQuotidienRepository,
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

    // Modèles de message capturés ici (contexte composable requis par stringResource), utilisés
    // tels quels dans les callbacks ci-dessous qui s'exécutent hors composition.
    val exportEchecTemplate = stringResource(R.string.statistiques_export_echec)
    val importEchecTemplate = stringResource(R.string.statistiques_import_echec)
    val erreurEcritureMessage = stringResource(R.string.statistiques_erreur_ecriture)
    val erreurLectureMessage = stringResource(R.string.statistiques_erreur_lecture)
    val fichierInvalideMessage = stringResource(R.string.statistiques_fichier_invalide)

    val lanceurExport = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val export = ExportStatistiques(
                        sessions = historiqueRepository.exporterSessions(profilId),
                        defis = defiRepository.exporterDefis(profilId),
                        defisQuotidiens = defiQuotidienRepository.exporterReussites(profilId),
                        trophees = tropheeRepository.exporterTrophees(profilId),
                    )
                    val json = StatistiquesExport.versJson(export)
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                            ?: throw IOException(erreurEcritureMessage)
                    }
                } catch (e: IOException) {
                    messageErreurFichier = exportEchecTemplate.format(e.message)
                }
            }
        }
    }
    val lanceurImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) uriImportEnAttente = uri
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // stickyHeader (retour utilisateur : le titre doit rester visible en scrollant).
        stickyHeader {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                EnTeteEcran(profil?.pseudo ?: stringResource(R.string.statistiques_titre_defaut), onRetour)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Button(onClick = onMesStatistiques, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.statistiques_bouton_mes_stats))
                }
                Button(onClick = onStatistiquesGenerales, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.statistiques_bouton_generales))
                }
            }
        }

        item { HorizontalDivider() }
        item {
            Button(onClick = onVoirTrophees, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.statistiques_bouton_voir_trophees))
            }
        }

        item { HorizontalDivider() }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Button(
                    onClick = { lanceurExport.launch("statistiques-${nomFichier(profil?.pseudo)}.json") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.statistiques_bouton_exporter)) }
                Button(
                    onClick = { lanceurImport.launch(arrayOf("application/json")) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(stringResource(R.string.statistiques_bouton_importer)) }
                Button(onClick = { confirmationReinitialisation = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.statistiques_bouton_reinitialiser))
                }
            }
        }
    }

    if (confirmationReinitialisation && profil != null) {
        AlertDialog(
            onDismissRequest = { confirmationReinitialisation = false },
            title = { Text(stringResource(R.string.statistiques_bouton_reinitialiser)) },
            text = {
                Text(stringResource(R.string.statistiques_confirm_reinit_message, profil.pseudo))
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        historiqueRepository.reinitialiserHistoriqueJoueur(profilId)
                        defiRepository.reinitialiserJoueur(profilId)
                        defiQuotidienRepository.reinitialiserJoueur(profilId)
                        tropheeRepository.reinitialiserJoueur(profilId)
                    }
                    confirmationReinitialisation = false
                }) { Text(stringResource(R.string.action_reinitialiser)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmationReinitialisation = false }) { Text(stringResource(R.string.action_annuler)) }
            },
        )
    }

    uriImportEnAttente?.let { uri ->
        AlertDialog(
            onDismissRequest = { uriImportEnAttente = null },
            title = { Text(stringResource(R.string.statistiques_bouton_importer)) },
            text = {
                Text(stringResource(R.string.statistiques_importer_message))
            },
            confirmButton = {
                TextButton(onClick = {
                    uriImportEnAttente = null
                    scope.launch {
                        try {
                            val json = withContext(Dispatchers.IO) {
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                                    ?: throw IOException(erreurLectureMessage)
                            }
                            val export = StatistiquesExport.depuisJson(json)
                            historiqueRepository.reinitialiserHistoriqueJoueur(profilId)
                            defiRepository.reinitialiserJoueur(profilId)
                            defiQuotidienRepository.reinitialiserJoueur(profilId)
                            tropheeRepository.reinitialiserJoueur(profilId)
                            historiqueRepository.importerSessions(profilId, export.sessions)
                            defiRepository.importerDefis(profilId, export.defis)
                            defiQuotidienRepository.importerReussites(profilId, export.defisQuotidiens)
                            tropheeRepository.importerTrophees(profilId, export.trophees)
                            tropheeRepository.reevaluer(profilId)
                        } catch (e: IllegalArgumentException) {
                            messageErreurFichier = e.message ?: fichierInvalideMessage
                        } catch (e: IOException) {
                            messageErreurFichier = importEchecTemplate.format(e.message)
                        }
                    }
                }) { Text(stringResource(R.string.action_remplacer)) }
            },
            dismissButton = {
                TextButton(onClick = { uriImportEnAttente = null }) { Text(stringResource(R.string.action_annuler)) }
            },
        )
    }

    messageErreurFichier?.let { message ->
        AlertDialog(
            onDismissRequest = { messageErreurFichier = null },
            title = { Text(stringResource(R.string.statistiques_operation_impossible)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { messageErreurFichier = null }) { Text(stringResource(R.string.action_ok)) }
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MesStatistiquesScreen(
    profilId: Long,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    onRetour: (() -> Unit)? = null,
) {
    val typesPartieAffiches = typesPartieAffiches()
    val variantesDefi = variantesDefi()
    // Même principe que donneesParNiveau ci-dessous (Flow collectés avant le LazyColumn, retour
    // utilisateur : titre épinglé au scroll), pour les défis.
    val donneesDefisParNiveau = Niveau.entries.map { niveau ->
        niveau to variantesDefi.map { variante ->
            val podiumFlow = remember(niveau, variante) { defiRepository.podiumDefi(profilId, variante.type, variante.mode, niveau.name) }
            val podium by podiumFlow.collectAsState(initial = emptyList())
            val historiqueFlow = remember(niveau, variante) { defiRepository.historiqueDefi(profilId, variante.type, variante.mode, niveau.name) }
            val historique by historiqueFlow.collectAsState(initial = emptyList())
            Triple(variante, podium, historique)
        }
    }.filter { (_, donnees) -> donnees.any { it.second.isNotEmpty() } }
    // Calculé ici, avant le LazyColumn (retour utilisateur : titre épinglé au scroll) : les
    // Flow doivent être collectés à chaque recomposition, pas seulement quand un item devient
    // visible, pour savoir dès le départ quels niveaux ont des données (filtre ci-dessous).
    val donneesParNiveau = Niveau.entries.map { niveau ->
        niveau to typesPartieAffiches.map { (type, libelle) ->
            val meilleuresFlow = remember(niveau, type) { historiqueRepository.meilleuresPartiesSoloParNiveau(profilId, niveau.name, type) }
            val meilleures by meilleuresFlow.collectAsState(initial = emptyList())
            Triple(type, libelle, meilleures)
        }
        // Retour utilisateur : un niveau qu'aucun type de partie n'a encore alimenté (ex.
        // Mathieu jamais joué, ou mode réseau tout juste ajouté) ne doit pas polluer l'écran.
    }.filter { (_, donneesParType) -> donneesParType.any { it.third.isNotEmpty() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // stickyHeader (retour utilisateur : le titre doit rester visible en scrollant).
        stickyHeader {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                EnTeteEcran(stringResource(R.string.statistiques_bouton_mes_stats), onRetour)
            }
        }

        item {
            Text(
                stringResource(R.string.statistiques_mes_meilleurs_scores),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        for ((position, entry) in donneesParNiveau.withIndex()) {
            val (niveau, donneesParType) = entry
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(niveau.libelle(), style = MaterialTheme.typography.titleMedium)
                    for ((type, libelle, meilleures) in donneesParType) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(libelle, style = MaterialTheme.typography.titleSmall)
                            Podium(meilleures.map { EntreePodium(null, it.score, it.date) })
                            if (type == TypePartie.STRUCTUREE) {
                                val historiqueFlow = remember(niveau) { historiqueRepository.historiqueScoresParNiveau(profilId, niveau.name, type) }
                                val historique by historiqueFlow.collectAsState(initial = emptyList())
                                GraphiqueProgression(historique.map { it.score })
                            }
                        }
                    }
                }
            }
            if (position != donneesParNiveau.lastIndex) item { HorizontalDivider() }
        }

        if (donneesDefisParNiveau.isNotEmpty()) {
            item { HorizontalDivider() }
            item {
                Text(
                    stringResource(R.string.statistiques_mes_defis),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        for ((position, entry) in donneesDefisParNiveau.withIndex()) {
            val (niveau, donneesParVariante) = entry
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(niveau.libelle(), style = MaterialTheme.typography.titleMedium)
                    for ((variante, podium, historique) in donneesParVariante) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(variante.libelle, style = MaterialTheme.typography.titleSmall)
                            Podium(podium.map { EntreePodium(null, it.serie, it.date, variante.formatValeurRes) })
                            GraphiqueProgression(historique.map { it.serie })
                        }
                    }
                }
            }
            if (position != donneesDefisParNiveau.lastIndex) item { HorizontalDivider() }
        }
    }
}

/** Classements distincts par niveau (retour utilisateur) : le score d'une confrontation peut être écrasé à 0 par l'adversaire, il ne doit pas se mélanger au meilleur score solo. */
@Composable
private fun typesPartieAffiches(): List<Pair<TypePartie, String>> = listOf(
    TypePartie.STRUCTUREE to stringResource(R.string.type_partie_solo),
    TypePartie.DUO to stringResource(R.string.mode_duo_libelle),
    TypePartie.DUO_CONFRONTATION to stringResource(R.string.mode_confrontation_libelle),
    TypePartie.DUO_RESEAU to stringResource(R.string.type_partie_duo_distance),
    TypePartie.DUO_CONFRONTATION_RESEAU to stringResource(R.string.type_partie_confrontation_distance),
)

/**
 * Une combinaison (type de défi, mode) affichée comme une section distincte (retour utilisateur :
 * défi série et défi chrono existent en chiffres ET en lettres, mots max est lettres uniquement,
 * sans faute mélange les deux mais stocké sous `ModeJeu.CHIFFRES` par convention, cf. DefiEntity).
 * [formatValeurRes] : "X réussites" pour série/chrono/sans faute, "X mots" pour mots max.
 */
private data class VarianteDefi(val type: TypeDefi, val mode: ModeJeu, val libelle: String, val formatValeurRes: Int)

@Composable
private fun variantesDefi(): List<VarianteDefi> {
    val chiffres = stringResource(R.string.mode_chiffres)
    val lettres = stringResource(R.string.mode_lettres)
    val serie = stringResource(R.string.defi_type_serie)
    val chrono = stringResource(R.string.defi_type_chrono)
    val mots = stringResource(R.string.defi_type_mots_max)
    val sansFaute = stringResource(R.string.defi_type_sans_faute)
    val formatReussites = R.string.statistiques_defi_valeur_reussites
    val formatMots = R.string.statistiques_defi_valeur_mots
    return listOf(
        VarianteDefi(TypeDefi.SERIE, ModeJeu.CHIFFRES, "$serie — $chiffres", formatReussites),
        VarianteDefi(TypeDefi.SERIE, ModeJeu.LETTRES, "$serie — $lettres", formatReussites),
        VarianteDefi(TypeDefi.CHRONO, ModeJeu.CHIFFRES, "$chrono — $chiffres", formatReussites),
        VarianteDefi(TypeDefi.CHRONO, ModeJeu.LETTRES, "$chrono — $lettres", formatReussites),
        VarianteDefi(TypeDefi.MOTS_MAX, ModeJeu.LETTRES, mots, formatMots),
        VarianteDefi(TypeDefi.SANS_FAUTE, ModeJeu.CHIFFRES, sansFaute, formatReussites),
    )
}

/**
 * Courbe de progression des scores dans l'ordre chronologique des parties (retour utilisateur :
 * complément du podium, pour voir la tendance plutôt que seulement les 3 meilleurs scores).
 * Dessinée à la main (Canvas) plutôt qu'avec une dépendance de graphique, l'usage étant limité
 * à une simple courbe de points reliés. Quadrillage horizontal tous les 10 ou 20 points et
 * tirets en abscisse toutes les 10 ou 20 parties (retour utilisateur, pour la lisibilité).
 */
@Composable
private fun GraphiqueProgression(scores: List<Int>) {
    if (scores.size < 2) {
        Text(
            stringResource(R.string.statistiques_pas_assez_de_parties),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        return
    }
    val scoreDataMin = scores.min()
    val scoreDataMax = scores.max()
    // Axe resserré sur la plage réelle des scores (retour utilisateur) plutôt que de toujours
    // partir de 0 : un pas en dessous du minimum, un pas au-dessus du maximum.
    val pasGrille = if (scoreDataMax - scoreDataMin > 100) 20 else 10
    val axisMin = (scoreDataMin / pasGrille) * pasGrille
    val axisMax = (scoreDataMax / pasGrille + 1) * pasGrille
    // Fixe à 20 (retour utilisateur) ; l'index de la N-ième partie est N-1 (base 0), sinon le
    // repère "20 parties" tombe visuellement sur la 21e.
    val pasAbscisse = 20
    val couleurGrille = TextMuted.copy(alpha = 0.35f)
    val couleurGrilleArgb = couleurGrille.toArgb()
    val decalageLabel = 22.dp

    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
        val decalageLabelPx = decalageLabel.toPx()
        val largeurGraphe = size.width - decalageLabelPx
        val pasX = if (scores.size > 1) largeurGraphe / (scores.size - 1) else 0f
        fun x(index: Int) = decalageLabelPx + index * pasX
        fun y(score: Int) = size.height - ((score - axisMin).toFloat() / (axisMax - axisMin)) * size.height

        val paintLabel = android.graphics.Paint().apply {
            color = couleurGrilleArgb
            textSize = 9.dp.toPx()
        }
        var valeurGrille = axisMin
        while (valeurGrille <= axisMax) {
            val yGrille = y(valeurGrille)
            drawLine(couleurGrille, Offset(decalageLabelPx, yGrille), Offset(size.width, yGrille), strokeWidth = 1.dp.toPx())
            drawContext.canvas.nativeCanvas.drawText("$valeurGrille", 0f, yGrille + 3.dp.toPx(), paintLabel)
            valeurGrille += pasGrille
        }

        var compte = pasAbscisse
        while (compte <= scores.size) {
            val xLigne = x(compte - 1)
            drawLine(couleurGrille, Offset(xLigne, 0f), Offset(xLigne, size.height), strokeWidth = 1.dp.toPx())
            compte += pasAbscisse
        }

        val chemin = Path()
        scores.forEachIndexed { index, score ->
            val point = Offset(x(index), y(score))
            if (index == 0) chemin.moveTo(point.x, point.y) else chemin.lineTo(point.x, point.y)
        }
        drawPath(chemin, color = BrassBright, style = Stroke(width = 3.dp.toPx()))
        scores.forEachIndexed { index, score ->
            drawCircle(color = BrassBright, radius = 3.dp.toPx(), center = Offset(x(index), y(score)))
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(stringResource(R.string.statistiques_nb_parties, scores.size), style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(stringResource(R.string.statistiques_meilleur_score, scoreDataMax), style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

/** Classement général par niveau, commun à tous les profils (retour utilisateur : écran dédié, séparé de la fiche). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatistiquesGeneralesScreen(
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    onRetour: (() -> Unit)? = null,
) {
    val typesPartieAffiches = typesPartieAffiches()
    val variantesDefi = variantesDefi()
    val classementsDefisParNiveau = Niveau.entries.map { niveau ->
        niveau to variantesDefi.map { variante ->
            val classementFlow = remember(niveau, variante) { defiRepository.classementDefi(variante.type, variante.mode, niveau.name) }
            val classement by classementFlow.collectAsState(initial = emptyList())
            Pair(variante, classement)
        }
    }.filter { (_, donnees) -> donnees.any { it.second.isNotEmpty() } }
    // Calculé ici, avant le LazyColumn (retour utilisateur : titre épinglé au scroll) : cf.
    // commentaire équivalent sur MesStatistiquesScreen.
    val donneesParNiveau = Niveau.entries.map { niveau ->
        niveau to typesPartieAffiches.map { (type, libelle) ->
            val classementFlow = remember(niveau, type) { historiqueRepository.classementParNiveau(niveau.name, type) }
            val classement by classementFlow.collectAsState(initial = emptyList())
            Triple(type, libelle, classement)
        }
        // Retour utilisateur : un niveau sans aucune donnée, tous types confondus, ne doit
        // pas s'afficher.
    }.filter { (_, donneesParType) -> donneesParType.any { it.third.isNotEmpty() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // stickyHeader (retour utilisateur : le titre doit rester visible en scrollant).
        stickyHeader {
            Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                EnTeteEcran(stringResource(R.string.statistiques_bouton_generales), onRetour)
            }
        }

        item {
            Text(
                stringResource(R.string.statistiques_classement_par_niveau),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        for ((position, entry) in donneesParNiveau.withIndex()) {
            val (niveau, donneesParType) = entry
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(niveau.libelle(), style = MaterialTheme.typography.titleMedium)
                    for ((_, libelle, classement) in donneesParType) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(libelle, style = MaterialTheme.typography.titleSmall)
                            Podium(classement.map { EntreePodium("${it.avatar} ${it.pseudo}", it.score, it.date) })
                        }
                    }
                }
            }
            if (position != donneesParNiveau.lastIndex) item { HorizontalDivider() }
        }

        if (classementsDefisParNiveau.isNotEmpty()) {
            item { HorizontalDivider() }
            item {
                Text(
                    stringResource(R.string.statistiques_classement_defis),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        for ((position, entry) in classementsDefisParNiveau.withIndex()) {
            val (niveau, donneesParVariante) = entry
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(niveau.libelle(), style = MaterialTheme.typography.titleMedium)
                    for ((variante, classement) in donneesParVariante) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(variante.libelle, style = MaterialTheme.typography.titleSmall)
                            Podium(
                                classement.map {
                                    EntreePodium("${it.avatar} ${it.pseudo}", it.serie, it.date, variante.formatValeurRes)
                                },
                            )
                        }
                    }
                }
            }
            if (position != classementsDefisParNiveau.lastIndex) item { HorizontalDivider() }
        }
    }
}

/**
 * [label] à null pour "mes statistiques" (toujours le joueur courant, pas besoin de le nommer).
 * [formatValeurRes] : ID de ressource string à un paramètre `%1$d` pour afficher [score] (retour
 * utilisateur : "X pts" pour une partie, "X réussites"/"X mots" pour un défi — la valeur n'est
 * pas toujours un score de points).
 */
private data class EntreePodium(val label: String?, val score: Int, val date: Long, val formatValeurRes: Int = R.string.revelation_score)

/** Podium des 3 meilleurs scores (retour utilisateur), remplace l'ancienne liste numérotée. */
@Composable
private fun Podium(entrees: List<EntreePodium>) {
    if (entrees.isEmpty()) {
        Text(stringResource(R.string.statistiques_aucun_score), style = MaterialTheme.typography.bodyMedium)
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
        Text(stringResource(entree.formatValeurRes, entree.score), color = Ivory, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
