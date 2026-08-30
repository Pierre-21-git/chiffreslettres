package fr.pierre.chiffreslettres.ui.navigation

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.pierre.chiffreslettres.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.DefiQuotidienTirage
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.ReglagesStore
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TropheeStats
import fr.pierre.chiffreslettres.data.VisitesEcranStore
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.letters.SacLettres
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.AProposScreen
import fr.pierre.chiffreslettres.ui.apropos.ReglesDuJeuScreen
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiChrono
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiMots
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiPoints
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiSansFaute
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiSerie
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDuelMots
import fr.pierre.chiffreslettres.ui.apropos.ReglesModePartieDuo
import fr.pierre.chiffreslettres.ui.apropos.VersionsScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundViewModel
import fr.pierre.chiffreslettres.ui.defi.ChoixDefiSansFauteScreen
import fr.pierre.chiffreslettres.ui.defi.ChoixDefiScreen
import fr.pierre.chiffreslettres.ui.defi.DefiMotsMaxScreen
import fr.pierre.chiffreslettres.ui.defi.DefiMotsMaxViewModel
import fr.pierre.chiffreslettres.ui.defi.DefiObjectifsPointsScreen
import fr.pierre.chiffreslettres.ui.defi.DefiObjectifsPointsViewModel
import fr.pierre.chiffreslettres.ui.defi.DefiQuotidienScreen
import fr.pierre.chiffreslettres.ui.defi.DefiViewModel
import fr.pierre.chiffreslettres.ui.defi.RaisonFinDefiObjectifsPoints
import fr.pierre.chiffreslettres.ui.defi.budgetSecondesDefiChrono
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.ui.defi.motEstReussiDefiLettres
import fr.pierre.chiffreslettres.ui.defi.seuilLongueurDefiLettres
import fr.pierre.chiffreslettres.ui.entrainement.ChoixNiveauEntrainementScreen
import fr.pierre.chiffreslettres.ui.entrainement.EntrainementLibreViewModel
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundScreen
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundViewModel
import fr.pierre.chiffreslettres.network.DuelMotsReseauViewModel
import fr.pierre.chiffreslettres.network.EtatManche
import fr.pierre.chiffreslettres.network.EtatPartieReseau
import fr.pierre.chiffreslettres.network.NOMBRE_VOYELLES_DUEL_MOTS
import fr.pierre.chiffreslettres.network.PartieReseauViewModel
import fr.pierre.chiffreslettres.network.RoleReseau
import fr.pierre.chiffreslettres.network.SousModeDuelMots
import fr.pierre.chiffreslettres.ui.duelmots.ChoixModeDuelMotsScreen
import fr.pierre.chiffreslettres.ui.duelmots.DuelMotsConfrontationScreen
import fr.pierre.chiffreslettres.ui.duelmots.DuelMotsResultatsScreen
import fr.pierre.chiffreslettres.ui.menu.MenuPrincipalScreen
import fr.pierre.chiffreslettres.ui.partie.ConfigurationPartieScreen
import fr.pierre.chiffreslettres.ui.partie.ManchePlanifiee
import fr.pierre.chiffreslettres.ui.partie.PartieStructureeViewModel
import fr.pierre.chiffreslettres.ui.partie.RecapPartieScreen
import fr.pierre.chiffreslettres.ui.partieduo.ConfigurationPartieDuoScreen
import fr.pierre.chiffreslettres.ui.partieduo.ModeScoreDuo
import fr.pierre.chiffreslettres.ui.partieduo.PartieDuoViewModel
import fr.pierre.chiffreslettres.ui.partieduo.RecapPartieDuoScreen
import fr.pierre.chiffreslettres.ui.partieduo.ResultatAffichage
import fr.pierre.chiffreslettres.ui.partieduo.ResultatDuoManche
import fr.pierre.chiffreslettres.ui.partieduo.TourDuo
import fr.pierre.chiffreslettres.ui.partieduo.TransitionJoueurScreen
import fr.pierre.chiffreslettres.ui.partieduo.VainqueurManche
import fr.pierre.chiffreslettres.ui.partieduo.premierJoueurManche
import fr.pierre.chiffreslettres.ui.partieduo.vainqueurMancheChiffres
import fr.pierre.chiffreslettres.ui.partieduo.vainqueurMancheLettres
import fr.pierre.chiffreslettres.ui.partiereseau.AttenteHoteScreen
import fr.pierre.chiffreslettres.ui.partiereseau.AttenteReseauScreen
import fr.pierre.chiffreslettres.ui.partiereseau.ChoixRoleReseauScreen
import fr.pierre.chiffreslettres.ui.partiereseau.ConfigurationPartieReseauScreen
import fr.pierre.chiffreslettres.ui.partiereseau.ConfirmationConnexionScreen
import fr.pierre.chiffreslettres.ui.partiereseau.DeclencherMancheChiffresScreen
import fr.pierre.chiffreslettres.ui.partiereseau.RechercheInviteScreen
import fr.pierre.chiffreslettres.ui.partiereseau.RevelationMancheReseauScreen
import fr.pierre.chiffreslettres.ui.profil.ChangerProfilScreen
import fr.pierre.chiffreslettres.ui.profil.CreerProfilScreen
import fr.pierre.chiffreslettres.ui.reglages.ReglagesScreen
import fr.pierre.chiffreslettres.ui.statistiques.MesStatistiquesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesGeneralesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesJoueurScreen
import fr.pierre.chiffreslettres.ui.theme.couleurRangJoueur
import fr.pierre.chiffreslettres.ui.trophees.TropheesDebloquesDialog
import fr.pierre.chiffreslettres.ui.trophees.StatutJoueurScreen
import fr.pierre.chiffreslettres.ui.trophees.TropheesScreen
import java.time.LocalDate
import kotlin.random.Random

@Composable
private fun entrainementViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    historiqueRepository: HistoriqueRepository,
    profilId: Long,
): EntrainementLibreViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.ENTRAINEMENT_GRAPH) }
    return viewModel(parentEntry) { EntrainementLibreViewModel(historiqueRepository, profilId) }
}

@Composable
private fun partieViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    historiqueRepository: HistoriqueRepository,
    tropheeRepository: TropheeRepository,
    profilId: Long,
): PartieStructureeViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.PARTIE_GRAPH) }
    return viewModel(parentEntry) { PartieStructureeViewModel(historiqueRepository, tropheeRepository, profilId) }
}

@Composable
private fun partieDuoViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    historiqueRepository: HistoriqueRepository,
    tropheeRepository: TropheeRepository,
    profilId: Long,
): PartieDuoViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.PARTIE_DUO_GRAPH) }
    return viewModel(parentEntry) { PartieDuoViewModel(historiqueRepository, tropheeRepository, profilId) }
}

@Composable
private fun partieReseauViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    context: Context,
    pseudo: String,
    avatar: String,
    historiqueRepository: HistoriqueRepository,
    tropheeRepository: TropheeRepository,
    profilId: Long,
): PartieReseauViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.RESEAU_GRAPH) }
    return viewModel(parentEntry) { PartieReseauViewModel(context, pseudo, avatar, historiqueRepository, tropheeRepository, profilId) }
}

@Composable
private fun duelMotsReseauViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    context: Context,
    pseudo: String,
    avatar: String,
    dictionnaire: DictionnaireIndex,
    configurationAlphabet: ConfigurationAlphabetLettres,
    historiqueRepository: HistoriqueRepository,
    tropheeRepository: TropheeRepository,
    profilId: Long,
): DuelMotsReseauViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.RESEAU_GRAPH) }
    return viewModel(parentEntry) {
        DuelMotsReseauViewModel(context, pseudo, avatar, dictionnaire, configurationAlphabet, historiqueRepository, tropheeRepository, profilId)
    }
}

@Composable
fun AppNavHost(
    dictionnaire: DictionnaireIndex,
    configurationAlphabet: ConfigurationAlphabetLettres,
    profilRepository: ProfilRepository,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    tropheeRepository: TropheeRepository,
    defiQuotidienRepository: DefiQuotidienRepository,
    profilActifStore: ProfilActifStore,
    reglagesStore: ReglagesStore,
    visitesEcranStore: VisitesEcranStore,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val profilActifIdStore by profilActifStore.profilActifId.collectAsState(initial = null)
    val profilActif = profils.find { it.id == profilActifIdStore } ?: profils.firstOrNull()
    val profilId = profilActif?.id ?: -1L

    // Langue par profil (retour utilisateur) : la langue d'affichage de toute l'app suit le
    // profil actif, via l'API per-app language d'Android 13+ (LocaleManager, pas besoin
    // d'AppCompat, minSdk = 33). Provoque une recréation automatique de l'activité par le
    // système si la langue change réellement.
    LaunchedEffect(profilActif?.langue) {
        val langue = profilActif?.langue ?: return@LaunchedEffect
        val localeManager = context.getSystemService(LocaleManager::class.java)
        localeManager.applicationLocales = LocaleList.forLanguageTags(langue)
    }

    NavHost(navController = navController, startDestination = Routes.MENU, modifier = modifier) {
        composable(Routes.MENU) {
            MenuPrincipalScreen(
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                profilId = profilId,
                tropheeRepository = tropheeRepository,
                onEntrainementLibre = { navController.navigate(Routes.ENTRAINEMENT_GRAPH) },
                onPartieStructuree = { navController.navigate(Routes.PARTIE_GRAPH) },
                onPartieDuo = { navController.navigate(Routes.PARTIE_DUO_GRAPH) },
                onPartieReseau = { navController.navigate(Routes.RESEAU_GRAPH) },
                onDuelMots = { navController.navigate(Routes.CHOIX_ROLE_DUEL_MOTS) },
                onDuelPoints = { navController.navigate(Routes.CHOIX_ROLE_DUEL_POINTS) },
                onDefiSerie = { navController.navigate(Routes.CHOIX_DEFI_SERIE) },
                onDefiChrono = { navController.navigate(Routes.CHOIX_DEFI_CHRONO) },
                onDefiMotsMax = { navController.navigate(Routes.CHOIX_DEFI_MOTS_MAX) },
                onDefiPoints = { navController.navigate(Routes.CHOIX_DEFI_POINTS) },
                onDefiSansFaute = { navController.navigate(Routes.CHOIX_DEFI_SANS_FAUTE) },
                onDefiQuotidien = { navController.navigate(Routes.CHOIX_DEFI_QUOTIDIEN) },
                onStatistiques = { navController.navigate(Routes.statistiquesJoueur(profilId)) },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                onAPropos = { navController.navigate(Routes.A_PROPOS) },
                onReglesDuJeu = { navController.navigate(Routes.REGLES_DU_JEU) },
                onVersions = { navController.navigate(Routes.VERSIONS) },
                onReglages = { navController.navigate(Routes.REGLAGES) },
            )
        }

        composable(Routes.REGLAGES) {
            ReglagesScreen(
                reglagesStore = reglagesStore,
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.A_PROPOS) {
            AProposScreen(
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.REGLES_DU_JEU) {
            // Easter egg "Curieux" (retour mainteneur) : marque la page comme consultée dès
            // l'ouverture, pas besoin d'attendre une action particulière sur l'écran.
            LaunchedEffect(profilId) { if (profilId != -1L) visitesEcranStore.marquerReglesVues(profilId) }
            ReglesDuJeuScreen(onRetour = { navController.popBackStack() })
        }

        composable(Routes.VERSIONS) {
            VersionsScreen(onRetour = { navController.popBackStack() })
        }

        composable(
            route = Routes.TROPHEES_JOUEUR_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            LaunchedEffect(profilIdArg) { tropheeRepository.reevaluer(profilIdArg) }
            val debloques by tropheeRepository.tropheesDebloques(profilIdArg).collectAsState(initial = null)
            val tropheesDebloques = debloques?.associate { it.trophyId to it.dateDebloque } ?: emptyMap()
            // Stats brutes pour la progression ("X / objectif") des trophées non débloqués, dans
            // leur détail (retour utilisateur) — recalculées à chaque changement de trophées
            // débloqués pour rester cohérentes avec l'affichage.
            var stats by remember { mutableStateOf<TropheeStats?>(null) }
            LaunchedEffect(profilIdArg, debloques) { stats = tropheeRepository.stats(profilIdArg) }
            TropheesScreen(
                titre = stringResource(R.string.mes_trophees_titre),
                tropheesDebloques = tropheesDebloques,
                stats = stats,
                onRetour = { navController.popBackStack() },
                onVoirStatutJoueur = { navController.navigate(Routes.statutJoueur(profilIdArg)) },
            )
        }

        composable(
            route = Routes.STATUT_JOUEUR_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            val debloques by tropheeRepository.tropheesDebloques(profilIdArg).collectAsState(initial = null)
            val tropheesDebloques = debloques?.associate { it.trophyId to it.dateDebloque } ?: emptyMap()
            StatutJoueurScreen(
                tropheesDebloques = tropheesDebloques,
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.CHANGER_PROFIL) {
            ChangerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                tropheeRepository = tropheeRepository,
                onProfilChoisi = { navController.popBackStack() },
                onCreerNouveauProfil = { navController.navigate(Routes.CREER_PROFIL) },
            )
        }

        composable(Routes.CREER_PROFIL) {
            CreerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                premierLancement = false,
                onProfilCree = { navController.popBackStack(Routes.MENU, inclusive = false) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.STATISTIQUES_JOUEUR_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            // Easter egg "Data-lover" (retour mainteneur) : compte les clics sur "Statistiques" au menu principal.
            LaunchedEffect(profilIdArg) { visitesEcranStore.incrementerVisitesStats(profilIdArg) }
            StatistiquesJoueurScreen(
                profilId = profilIdArg,
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
                defiQuotidienRepository = defiQuotidienRepository,
                profilRepository = profilRepository,
                tropheeRepository = tropheeRepository,
                onMesStatistiques = { navController.navigate(Routes.mesStatistiques(profilIdArg)) },
                onStatistiquesGenerales = { navController.navigate(Routes.STATISTIQUES_GENERALES) },
                onVoirTrophees = { navController.navigate(Routes.tropheesJoueur(profilIdArg)) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.MES_STATISTIQUES_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            MesStatistiquesScreen(
                profilId = profilIdArg,
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
                visitesEcranStore = visitesEcranStore,
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.STATISTIQUES_GENERALES) {
            StatistiquesGeneralesScreen(
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
                onRetour = { navController.popBackStack() },
            )
        }

        navigation(startDestination = Routes.CHOIX_NIVEAU_ENTRAINEMENT, route = Routes.ENTRAINEMENT_GRAPH) {
            composable(Routes.CHOIX_NIVEAU_ENTRAINEMENT) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                ChoixNiveauEntrainementScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuChiffres(niveau)) },
                    onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuLettres(niveau)) },
                    onRetour = {
                        // Sortie complète du mode entraînement : c'est ici (et seulement ici,
                        // pas au retour d'un écran de manche vers cette liste) que la session
                        // est enregistrée dans l'historique (retour utilisateur : plus de
                        // bouton dédié "Quitter l'entraînement"/"Arrêter").
                        entrainementVm.terminerEtEnregistrer()
                        navController.popBackStack()
                    },
                )
            }

            composable(
                route = Routes.JEU_CHIFFRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                // Pas de limite de temps en entraînement libre (retour utilisateur) : dureeSecondes = null.
                val roundVm: ChiffresRoundViewModel =
                    viewModel(backStackEntry) { ChiffresRoundViewModel(niveau) }
                ChiffresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onMancheTerminee = { obtenu, _ -> entrainementVm.enregistrerMancheChiffres(niveau, obtenu) },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_NIVEAU_ENTRAINEMENT, inclusive = false) },
                    actionsFinManche = {
                        Button(
                            onClick = {
                                navController.navigate(Routes.jeuChiffres(niveau)) {
                                    popUpTo(Routes.JEU_CHIFFRES_PATTERN) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.action_rejouer)) }
                    },
                )
            }

            composable(
                route = Routes.JEU_LETTRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                // Pas de limite de temps en entraînement libre (retour utilisateur) : dureeSecondes = null.
                val roundVm: LettresRoundViewModel =
                    viewModel(backStackEntry) { LettresRoundViewModel(niveau, dictionnaire, configurationAlphabet) }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onMancheTerminee = { obtenu, motValide, _, _, _, _ -> entrainementVm.enregistrerMancheLettres(niveau, obtenu, motValide) },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_NIVEAU_ENTRAINEMENT, inclusive = false) },
                    actionsFinManche = {
                        Button(
                            onClick = {
                                navController.navigate(Routes.jeuLettres(niveau)) {
                                    popUpTo(Routes.JEU_LETTRES_PATTERN) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.action_rejouer)) }
                    },
                )
            }
        }

        navigation(startDestination = Routes.CONFIGURATION_PARTIE, route = Routes.PARTIE_GRAPH) {
            composable(Routes.CONFIGURATION_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                ConfigurationPartieScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onDemarrer = { sequence ->
                        partieVm.demarrer(sequence)
                        navController.navigate(Routes.JEU_PARTIE)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.JEU_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                val sequence by partieVm.sequence.collectAsState()
                val index by partieVm.index.collectAsState()
                val resultats by partieVm.resultats.collectAsState()
                val manche = sequence.getOrNull(index)
                var demanderConfirmationRetour by remember { mutableStateOf(false) }
                val onRetourAvecConfirmation = { demanderConfirmationRetour = true }

                if (manche == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.RECAP_PARTIE) {
                            popUpTo(Routes.JEU_PARTIE) { inclusive = true }
                        }
                    }
                } else {
                    val estDerniere = index == sequence.lastIndex
                    val scoreCumule = resultats.sumOf { it.score }
                    val progressionManche = "${index + 1} / ${sequence.size}"
                    val actionsFinManche: @Composable () -> Unit = {
                        Button(onClick = { partieVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(if (estDerniere) R.string.revelation_voir_resultats else R.string.revelation_manche_suivante))
                        }
                    }
                    when (manche) {
                        is ManchePlanifiee.Chiffres -> {
                            val roundVm: ChiffresRoundViewModel =
                                viewModel(key = "partie-chiffres-$index") {
                                    ChiffresRoundViewModel(manche.niveau, manche.niveau.dureeSecondesPartieStructuree)
                                }
                            ChiffresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                                onMancheTerminee = { obtenu, detail ->
                                    partieVm.enregistrerResultat(
                                        ResultatManche(
                                            ModeJeu.CHIFFRES, manche.niveau.name, obtenu,
                                            cibleChiffres = detail?.cible,
                                            nombreOperationsChiffres = detail?.nombreOperations,
                                            maxEtapeIntermediaireChiffres = detail?.maxEtapeIntermediaire,
                                            dureeSecondesManche = detail?.dureeSecondesEcoulees,
                                            tempsRestantSecondesValidation = detail?.tempsRestantSecondes,
                                            ecartCibleChiffres = detail?.ecartCible,
                                            operateursUtilisesChiffres = detail?.operateursUtilises,
                                        ),
                                    )
                                },
                                onRetourEntrainement = onRetourAvecConfirmation,
                                progressionManche = progressionManche,
                                actionsFinManche = actionsFinManche,
                            )
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel =
                                viewModel(key = "partie-lettres-$index") {
                                    LettresRoundViewModel(manche.niveau, dictionnaire, configurationAlphabet, manche.niveau.dureeSecondesPartieStructuree)
                                }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                                onMancheTerminee = { obtenu, motValide, _, _, longueurMotInvalide, _ ->
                                    partieVm.enregistrerResultat(
                                        ResultatManche(
                                            ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide, longueurMotInvalide,
                                            dureeSecondesManche = roundVm.uiState.value.dureeSecondesEcoulees,
                                        ),
                                    )
                                },
                                onRetourEntrainement = onRetourAvecConfirmation,
                                progressionManche = progressionManche,
                                actionsFinManche = actionsFinManche,
                            )
                        }
                    }
                }

                if (demanderConfirmationRetour) {
                    AlertDialog(
                        onDismissRequest = { demanderConfirmationRetour = false },
                        title = { Text(stringResource(R.string.quitter_partie_titre)) },
                        text = { Text(stringResource(R.string.quitter_partie_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                demanderConfirmationRetour = false
                                navController.popBackStack(Routes.CONFIGURATION_PARTIE, inclusive = false)
                            }) { Text(stringResource(R.string.action_quitter)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                        },
                    )
                }
            }

            composable(Routes.RECAP_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                val resultats by partieVm.resultats.collectAsState()
                RecapPartieScreen(
                    resultats = resultats,
                    // La partie est déjà enregistrée en base dès la dernière manche jouée (voir
                    // PartieStructureeViewModel.enregistrerResultat) : ce bouton ne fait plus que
                    // naviguer, il ne peut donc plus perdre la partie en cas de retour arrière.
                    onTerminer = { navController.popBackStack(Routes.MENU, inclusive = false) },
                    onRetour = { navController.popBackStack() },
                )
                val tropheesDebloques by partieVm.tropheesDebloques.collectAsState()
                TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { partieVm.effacerTropheesDebloques() })
            }
        }

        navigation(startDestination = Routes.CONFIGURATION_PARTIE_DUO, route = Routes.PARTIE_DUO_GRAPH) {
            composable(Routes.CONFIGURATION_PARTIE_DUO) { backStackEntry ->
                val duoVm = partieDuoViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                ConfigurationPartieDuoScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    autresProfils = profils.filter { it.id != profilId },
                    onDemarrer = { profil2Id, sequence, mode ->
                        duoVm.demarrer(profil2Id, sequence, mode)
                        navController.navigate(Routes.JEU_PARTIE_DUO)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.JEU_PARTIE_DUO) { backStackEntry ->
                val duoVm = partieDuoViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                val sequence by duoVm.sequence.collectAsState()
                val seeds by duoVm.seeds.collectAsState()
                val index by duoVm.index.collectAsState()
                val tour by duoVm.tour.collectAsState()
                val enTransition by duoVm.enTransition.collectAsState()
                val resultats1 by duoVm.resultatsJoueur1.collectAsState()
                val resultats2 by duoVm.resultatsJoueur2.collectAsState()
                val profil2 = profils.find { it.id == duoVm.profil2Id }
                val manche = sequence.getOrNull(index)
                var demanderConfirmationRetour by remember { mutableStateOf(false) }
                val texteAucunMot = stringResource(R.string.revelation_aucun_mot)
                val patronMotInvalide = stringResource(R.string.revelation_mot_invalide)

                val onRetourAvecConfirmation = { demanderConfirmationRetour = true }

                if (manche == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.RECAP_PARTIE_DUO) {
                            popUpTo(Routes.JEU_PARTIE_DUO) { inclusive = true }
                        }
                    }
                } else if (enTransition) {
                    val r1Manche = resultats1.getOrNull(index)
                    val r2Manche = resultats2.getOrNull(index)
                    // Personne n'a encore joué cette manche : c'est l'écran d'annonce initial du
                    // premier joueur (démarrage de la partie), pas une passation après un tour.
                    val personnePasEncoreJoue = r1Manche == null && r2Manche == null
                    val premier = premierJoueurManche(index)
                    val finDePartie = !personnePasEncoreJoue && tour != premier && index == sequence.lastIndex
                    val prochainTour = when {
                        personnePasEncoreJoue -> tour
                        finDePartie -> null
                        tour == premier -> if (premier == TourDuo.JOUEUR1) TourDuo.JOUEUR2 else TourDuo.JOUEUR1
                        else -> premierJoueurManche(index + 1)
                    }
                    val prochainJoueur = when (prochainTour) {
                        TourDuo.JOUEUR1 -> profilActif
                        TourDuo.JOUEUR2 -> profil2
                        null -> null
                    }
                    // Le résultat de la manche ne se révèle que quand les deux joueurs ont joué
                    // (retour utilisateur : pas de résultat du 1er joueur avant le 2e).
                    val resultatsAffiches = if (r1Manche != null && r2Manche != null) {
                        val vainqueur = when (manche) {
                            is ManchePlanifiee.Chiffres -> vainqueurMancheChiffres(r1Manche.ecartCible, r2Manche.ecartCible)
                            is ManchePlanifiee.Lettres -> vainqueurMancheLettres(r1Manche.resultat.motJoue, r2Manche.resultat.motJoue)
                        }
                        fun scoreAffiche(brut: Int, perdant: Boolean) =
                            if (duoVm.mode == ModeScoreDuo.CONFRONTATION && perdant) 0 else brut
                        listOf(
                            ResultatAffichage(
                                profilActif?.pseudo ?: "Joueur 1",
                                scoreAffiche(r1Manche.resultat.score, vainqueur == VainqueurManche.JOUEUR2),
                                r1Manche.detail,
                                vainqueur == VainqueurManche.JOUEUR1,
                            ),
                            ResultatAffichage(
                                profil2?.pseudo ?: "Joueur 2",
                                scoreAffiche(r2Manche.resultat.score, vainqueur == VainqueurManche.JOUEUR1),
                                r2Manche.detail,
                                vainqueur == VainqueurManche.JOUEUR2,
                            ),
                        )
                    } else {
                        emptyList()
                    }
                    val (scoreFinal1, scoreFinal2) = duoVm.resultatsFinaux()
                    TransitionJoueurScreen(
                        prochainPseudo = prochainJoueur?.let { "${it.avatar} ${it.pseudo}" },
                        resultats = resultatsAffiches,
                        pseudo1 = profilActif?.pseudo ?: "Joueur 1",
                        pseudo2 = profil2?.pseudo ?: "Joueur 2",
                        scorePartie1 = scoreFinal1.sumOf { it.score },
                        scorePartie2 = scoreFinal2.sumOf { it.score },
                        onPret = { duoVm.confirmerTransition() },
                        mode = r1Manche?.resultat?.mode,
                        dixMeilleursMots = r1Manche?.dixMeilleursMots ?: emptyList(),
                        solutionPossible = r1Manche?.solutionPossible,
                    )
                } else {
                    val joueurActif = if (tour == TourDuo.JOUEUR1) profilActif else profil2
                    val seedManche = seeds.getOrNull(index) ?: 0L
                    val premier = premierJoueurManche(index)
                    val estPremierJoueurManche = tour == premier
                    val progressionManche = "${index + 1} / ${sequence.size}"

                    when (manche) {
                        is ManchePlanifiee.Chiffres -> {
                            val roundVm: ChiffresRoundViewModel =
                                viewModel(key = "duo-chiffres-$index-$tour") {
                                    ChiffresRoundViewModel(
                                        manche.niveau,
                                        manche.niveau.dureeSecondesPartieStructuree,
                                        random = Random(seedManche),
                                    )
                                }
                            ChiffresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = null,
                                pseudo = joueurActif?.let { "${it.avatar} ${it.pseudo}" },
                                couleurRang = joueurActif?.let { couleurRangJoueur(it.id, tropheeRepository) },
                                afficherResultat = false,
                                onMancheTerminee = { obtenu, detailChiffres ->
                                    val detail = roundVm.uiState.value.operationsEffectuees
                                        .joinToString("\n").ifBlank { "Aucune opération" }
                                    duoVm.enregistrerResultat(
                                        ResultatDuoManche(
                                            ResultatManche(
                                                ModeJeu.CHIFFRES, manche.niveau.name, obtenu,
                                                cibleChiffres = detailChiffres?.cible,
                                                nombreOperationsChiffres = detailChiffres?.nombreOperations,
                                                maxEtapeIntermediaireChiffres = detailChiffres?.maxEtapeIntermediaire,
                                                dureeSecondesManche = detailChiffres?.dureeSecondesEcoulees,
                                                tempsRestantSecondesValidation = detailChiffres?.tempsRestantSecondes,
                                                ecartCibleChiffres = detailChiffres?.ecartCible,
                                                operateursUtilisesChiffres = detailChiffres?.operateursUtilises,
                                            ),
                                            roundVm.uiState.value.ecartCible,
                                            detail,
                                            solutionPossible = roundVm.uiState.value.solutionSolveur,
                                        ),
                                    )
                                },
                                onRetourEntrainement = onRetourAvecConfirmation,
                                progressionManche = progressionManche,
                                actionsFinManche = {},
                            )
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel =
                                viewModel(key = "duo-lettres-$index-$tour") {
                                    LettresRoundViewModel(
                                        manche.niveau,
                                        dictionnaire,
                                        configurationAlphabet,
                                        manche.niveau.dureeSecondesPartieStructuree,
                                        random = Random(seedManche),
                                    )
                                }
                            if (!estPremierJoueurManche) {
                                // Force le même nombre de voyelles que le premier joueur de
                                // cette manche (retour utilisateur : mêmes lettres pour les
                                // deux) — l'instance du premier joueur est retrouvée via la
                                // même clé, sans relancer son tirage ni son chrono.
                                val roundVmPremier: LettresRoundViewModel =
                                    viewModel(key = "duo-lettres-$index-$premier") {
                                        LettresRoundViewModel(
                                            manche.niveau,
                                            dictionnaire,
                                            configurationAlphabet,
                                            manche.niveau.dureeSecondesPartieStructuree,
                                            random = Random(seedManche),
                                        )
                                    }
                                LaunchedEffect(roundVm) {
                                    val n = roundVmPremier.uiState.value.nombreVoyellesChoisi
                                    if (n != null && !roundVm.uiState.value.tirageTermine) {
                                        roundVm.choisirNombreVoyelles(n)
                                    }
                                }
                            }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = null,
                                pseudo = joueurActif?.let { "${it.avatar} ${it.pseudo}" },
                                couleurRang = joueurActif?.let { couleurRangJoueur(it.id, tropheeRepository) },
                                afficherResultat = false,
                                onMancheTerminee = { obtenu, motValide, _, dixMeilleursMots, longueurMotInvalide, motInvalide ->
                                    duoVm.enregistrerResultat(
                                        ResultatDuoManche(
                                            ResultatManche(
                                            ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide, longueurMotInvalide,
                                            dureeSecondesManche = roundVm.uiState.value.dureeSecondesEcoulees,
                                            motInvalide = motInvalide,
                                        ),
                                            detail = motValide ?: motInvalide?.let { patronMotInvalide.format(it) } ?: texteAucunMot,
                                            dixMeilleursMots = dixMeilleursMots,
                                        ),
                                    )
                                },
                                onRetourEntrainement = onRetourAvecConfirmation,
                                progressionManche = progressionManche,
                                actionsFinManche = {},
                            )
                        }
                    }
                }

                if (demanderConfirmationRetour) {
                    AlertDialog(
                        onDismissRequest = { demanderConfirmationRetour = false },
                        title = { Text(stringResource(R.string.quitter_partie_duo_titre)) },
                        text = { Text(stringResource(R.string.quitter_partie_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                demanderConfirmationRetour = false
                                navController.popBackStack(Routes.CONFIGURATION_PARTIE_DUO, inclusive = false)
                            }) { Text(stringResource(R.string.action_quitter)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                        },
                    )
                }
            }

            composable(Routes.RECAP_PARTIE_DUO) { backStackEntry ->
                val duoVm = partieDuoViewModel(navController, backStackEntry, historiqueRepository, tropheeRepository, profilId)
                val profil2 = profils.find { it.id == duoVm.profil2Id }
                val (resultats1, resultats2) = duoVm.resultatsFinaux()
                val pseudo1 = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "Joueur 1"
                val pseudo2 = profil2?.let { "${it.avatar} ${it.pseudo}" } ?: "Joueur 2"
                RecapPartieDuoScreen(
                    pseudo1 = pseudo1,
                    pseudo2 = pseudo2,
                    resultats1 = resultats1,
                    resultats2 = resultats2,
                    // La partie est déjà enregistrée en base pour les deux joueurs dès que les
                    // deux ont joué la dernière manche (voir PartieDuoViewModel.enregistrerResultat) :
                    // ce bouton ne fait plus que naviguer.
                    onTerminer = { navController.popBackStack(Routes.MENU, inclusive = false) },
                    onRetour = { navController.popBackStack() },
                )
                // Affichés l'un après l'autre (retour utilisateur) : les deux dialogs modaux en
                // même temps se chevaucheraient. Joueur 1 d'abord, joueur 2 une fois le sien fermé.
                val tropheesJoueur1 by duoVm.tropheesDebloquesJoueur1.collectAsState()
                val tropheesJoueur2 by duoVm.tropheesDebloquesJoueur2.collectAsState()
                if (tropheesJoueur1.isNotEmpty()) {
                    TropheesDebloquesDialog(tropheesJoueur1, nomJoueur = pseudo1, onDismiss = { duoVm.effacerTropheesDebloquesJoueur1() })
                } else {
                    TropheesDebloquesDialog(tropheesJoueur2, nomJoueur = pseudo2, onDismiss = { duoVm.effacerTropheesDebloquesJoueur2() })
                }
            }
        }

        navigation(startDestination = Routes.CHOIX_ROLE_RESEAU, route = Routes.RESEAU_GRAPH) {
            composable(Routes.CHOIX_ROLE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                ChoixRoleReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onHeberger = { transport ->
                        reseauVm.choisirHote(transport)
                        navController.navigate(Routes.HOTE_ATTENTE_RESEAU)
                    },
                    onRejoindre = { transport ->
                        reseauVm.choisirInvite(transport)
                        navController.navigate(Routes.INVITE_RECHERCHE_RESEAU)
                    },
                    onRetour = { navController.popBackStack() },
                    contenuRegles = { ReglesModePartieDuo() },
                )
            }

            composable(Routes.HOTE_ATTENTE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by reseauVm.etat.collectAsState()
                LaunchedEffect(etat) {
                    if (etat is EtatPartieReseau.Connecte) {
                        navController.navigate(Routes.RESEAU_CONNEXION) {
                            popUpTo(Routes.CHOIX_ROLE_RESEAU) { inclusive = false }
                        }
                    }
                }
                val etatActuel = etat
                AttenteHoteScreen(
                    nomServiceAffiche = (etatActuel as? EtatPartieReseau.AttenteHote)?.nomServiceAffiche,
                    erreur = (etatActuel as? EtatPartieReseau.Erreur)?.message,
                    onAnnulerErreur = {
                        reseauVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(Routes.CHOIX_ROLE_RESEAU, inclusive = false)
                    },
                    onAnnuler = {
                        reseauVm.annulerEtRevenirAuChoix()
                        navController.popBackStack()
                    },
                )
            }

            composable(Routes.INVITE_RECHERCHE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by reseauVm.etat.collectAsState()
                val parties by reseauVm.partiesTrouvees.collectAsState()
                LaunchedEffect(etat) {
                    if (etat is EtatPartieReseau.Connecte) {
                        navController.navigate(Routes.RESEAU_CONNEXION) {
                            popUpTo(Routes.CHOIX_ROLE_RESEAU) { inclusive = false }
                        }
                    }
                }
                val etatActuel = etat
                RechercheInviteScreen(
                    parties = parties,
                    connexionEnCours = etatActuel is EtatPartieReseau.ConnexionEnCours,
                    erreur = (etatActuel as? EtatPartieReseau.Erreur)?.message,
                    onSelectionner = { reseauVm.rejoindre(it) },
                    onAnnulerErreur = {
                        reseauVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(Routes.CHOIX_ROLE_RESEAU, inclusive = false)
                    },
                    onAnnuler = {
                        reseauVm.annulerEtRevenirAuChoix()
                        navController.popBackStack()
                    },
                )
            }

            composable(Routes.RESEAU_CONNEXION) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by reseauVm.etat.collectAsState()
                val etatConnecte = etat as? EtatPartieReseau.Connecte
                if (etatConnecte != null) {
                    ConfirmationConnexionScreen(
                        profilDistant = etatConnecte.profilDistant,
                        onContinuer = {
                            if (etatConnecte.role == RoleReseau.HOTE) {
                                navController.navigate(Routes.CONFIGURATION_PARTIE_RESEAU)
                            } else {
                                navController.navigate(Routes.JEU_PARTIE_RESEAU)
                            }
                        },
                    )
                }
            }

            composable(Routes.CONFIGURATION_PARTIE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                ConfigurationPartieReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onDemarrer = { niveau, mode ->
                        reseauVm.demarrerCommeHote(niveau, mode)
                        navController.navigate(Routes.JEU_PARTIE_RESEAU)
                    },
                )
            }

            composable(Routes.JEU_PARTIE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etatConnexion by reseauVm.etat.collectAsState()
                val profilDistant = (etatConnexion as? EtatPartieReseau.Connecte)?.profilDistant
                val pseudoAdversaire = profilDistant?.let { "${it.avatar} ${it.pseudo}" } ?: "l'adversaire"
                val pseudoMoi = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "Moi"
                val sequence by reseauVm.sequence.collectAsState()
                val seeds by reseauVm.seeds.collectAsState()
                val index by reseauVm.index.collectAsState()
                val tour by reseauVm.tour.collectAsState()
                val etatManche by reseauVm.etatManche.collectAsState()
                val resultats1 by reseauVm.resultatsJoueur1.collectAsState()
                val resultats2 by reseauVm.resultatsJoueur2.collectAsState()
                val choixVoyellesRecu by reseauVm.choixVoyellesRecu.collectAsState()
                val adversairePretPourManche by reseauVm.adversairePretPourManche.collectAsState()
                val erreurJeu by reseauVm.erreurJeu.collectAsState()
                val manche = sequence.getOrNull(index)
                var demanderConfirmationRetour by remember { mutableStateOf(false) }
                val onRetourAvecConfirmation = { demanderConfirmationRetour = true }
                val jeSuisDeclencheur = tour == reseauVm.monTourDuo
                val texteAucunMot = stringResource(R.string.revelation_aucun_mot)
                val patronMotInvalide = stringResource(R.string.revelation_mot_invalide)
                // Mon résultat de cette manche est déjà envoyé, mais pas encore celui de
                // l'adversaire (sinon etatManche serait déjà Revelation) : j'affiche un indicateur
                // d'attente au lieu du plateau figé/désactivé (retour utilisateur, chrono qui
                // restait affiché à sa valeur de validation sans indiquer d'attente).
                val monResultatEnvoye = if (reseauVm.monTourDuo == TourDuo.JOUEUR1) resultats1[index] != null else resultats2[index] != null

                if (erreurJeu != null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(erreurJeu ?: "", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { navController.popBackStack(Routes.MENU, inclusive = false) }) {
                            Text(stringResource(R.string.retour_au_menu))
                        }
                    }
                } else if (sequence.isEmpty()) {
                    AttenteReseauScreen("En attente de la configuration de la partie…")
                } else if (manche == null) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.RECAP_PARTIE_RESEAU) {
                            popUpTo(Routes.JEU_PARTIE_RESEAU) { inclusive = true }
                        }
                    }
                } else if (etatManche is EtatManche.Revelation) {
                    val r1 = resultats1[index]
                    val r2 = resultats2[index]
                    if (r1 != null && r2 != null) {
                        val vainqueur = when (manche) {
                            is ManchePlanifiee.Chiffres -> vainqueurMancheChiffres(r1.ecartCible, r2.ecartCible)
                            is ManchePlanifiee.Lettres -> vainqueurMancheLettres(r1.resultat.motJoue, r2.resultat.motJoue)
                        }
                        fun scoreAffiche(brut: Int, perdant: Boolean) =
                            if (reseauVm.mode == ModeScoreDuo.CONFRONTATION && perdant) 0 else brut
                        // Ordre toujours "Moi, Adversaire" (retour utilisateur : doit correspondre à
                        // la ligne de score ci-dessous, qui est dans cet ordre fixe) — pas l'ordre
                        // Joueur1/Joueur2 réseau, qui dépend de mon rôle dans la partie.
                        val moiEstJoueur1 = reseauVm.monTourDuo == TourDuo.JOUEUR1
                        val resultatMoi = if (moiEstJoueur1) r1 else r2
                        val resultatAdversaire = if (moiEstJoueur1) r2 else r1
                        val jeGagne = vainqueur == (if (moiEstJoueur1) VainqueurManche.JOUEUR1 else VainqueurManche.JOUEUR2)
                        val adversaireGagne = vainqueur == (if (moiEstJoueur1) VainqueurManche.JOUEUR2 else VainqueurManche.JOUEUR1)
                        val resultatsAffiches = listOf(
                            ResultatAffichage(
                                pseudoMoi,
                                scoreAffiche(resultatMoi.resultat.score, adversaireGagne),
                                resultatMoi.detail,
                                jeGagne,
                            ),
                            ResultatAffichage(
                                pseudoAdversaire,
                                scoreAffiche(resultatAdversaire.resultat.score, jeGagne),
                                resultatAdversaire.detail,
                                adversaireGagne,
                            ),
                        )
                        val (scoreFinal1, scoreFinal2) = reseauVm.resultatsFinaux()
                        val (scoreMoi, scoreAdv) = if (moiEstJoueur1) {
                            scoreFinal1.sumOf { it.score } to scoreFinal2.sumOf { it.score }
                        } else {
                            scoreFinal2.sumOf { it.score } to scoreFinal1.sumOf { it.score }
                        }
                        RevelationMancheReseauScreen(
                            resultats = resultatsAffiches,
                            pseudoMoi = pseudoMoi,
                            pseudoAdversaire = pseudoAdversaire,
                            scoreMoi = scoreMoi,
                            scoreAdversaire = scoreAdv,
                            mode = resultatMoi.resultat.mode,
                            dixMeilleursMots = resultatMoi.dixMeilleursMots,
                            solutionPossible = resultatMoi.solutionPossible,
                            dernierManche = index == sequence.lastIndex,
                            onSuivant = { reseauVm.mancheSuivante() },
                        )
                    }
                } else if (etatManche is EtatManche.AttenteDeclenchement && !jeSuisDeclencheur) {
                    LaunchedEffect(index) { reseauVm.signalerPret() }
                    val quoi = if (manche is ManchePlanifiee.Lettres) "choisisse le nombre de voyelles" else "lance la manche"
                    AttenteReseauScreen("En attente que $pseudoAdversaire $quoi…")
                } else if (etatManche is EtatManche.AttenteDeclenchement && index !in adversairePretPourManche) {
                    // Je suis le déclencheur, mais l'adversaire n'a pas encore confirmé être
                    // arrivé sur l'écran d'attente : éviter de déclencher la manche trop tôt.
                    AttenteReseauScreen("En attente que $pseudoAdversaire soit prêt…")
                } else if (monResultatEnvoye) {
                    // Retour utilisateur : rester sur cet écran en voyant le chrono de la manche
                    // continuer de défiler, plutôt qu'un simple indicateur figé. Le ViewModel de
                    // la manche existe déjà (créé dans le "else" ci-dessous avant ma validation) ;
                    // on le retrouve par sa clé pour lire son temps restant, sans le recréer.
                    val secondesRestantes = when (manche) {
                        is ManchePlanifiee.Chiffres -> {
                            val roundVm: ChiffresRoundViewModel = viewModel(key = "reseau-chiffres-$index")
                            val uiState by roundVm.uiState.collectAsState()
                            uiState.tempsRestantSecondes
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel = viewModel(key = "reseau-lettres-$index")
                            val uiState by roundVm.uiState.collectAsState()
                            uiState.tempsRestantSecondes
                        }
                    }
                    AttenteReseauScreen("En attente du résultat de $pseudoAdversaire…", secondesInitiales = secondesRestantes)
                } else {
                    val seedManche = seeds.getOrNull(index) ?: 0L
                    val progressionManche = "${index + 1} / ${sequence.size}"

                    when (manche) {
                        is ManchePlanifiee.Chiffres -> {
                            if (etatManche is EtatManche.AttenteDeclenchement) {
                                DeclencherMancheChiffresScreen(
                                    progressionManche = progressionManche,
                                    onCommencer = { reseauVm.declencherManche() },
                                )
                            } else {
                                val roundVm: ChiffresRoundViewModel =
                                    viewModel(key = "reseau-chiffres-$index") {
                                        ChiffresRoundViewModel(
                                            manche.niveau,
                                            manche.niveau.dureeSecondesPartieStructuree,
                                            random = Random(seedManche),
                                        )
                                    }
                                ChiffresRoundScreen(
                                    viewModel = roundVm,
                                    scoreCumule = null,
                                    pseudo = null,
                                    afficherResultat = false,
                                    onMancheTerminee = { obtenu, detailChiffres ->
                                        val detail = roundVm.uiState.value.operationsEffectuees
                                            .joinToString("\n").ifBlank { "Aucune opération" }
                                        reseauVm.enregistrerMonResultat(
                                            ResultatDuoManche(
                                                ResultatManche(
                                                    ModeJeu.CHIFFRES, manche.niveau.name, obtenu,
                                                    cibleChiffres = detailChiffres?.cible,
                                                    nombreOperationsChiffres = detailChiffres?.nombreOperations,
                                                    maxEtapeIntermediaireChiffres = detailChiffres?.maxEtapeIntermediaire,
                                                    dureeSecondesManche = detailChiffres?.dureeSecondesEcoulees,
                                                    tempsRestantSecondesValidation = detailChiffres?.tempsRestantSecondes,
                                                    ecartCibleChiffres = detailChiffres?.ecartCible,
                                                    operateursUtilisesChiffres = detailChiffres?.operateursUtilises,
                                                ),
                                                roundVm.uiState.value.ecartCible,
                                                detail,
                                                solutionPossible = roundVm.uiState.value.solutionSolveur,
                                            ),
                                        )
                                    },
                                    onRetourEntrainement = onRetourAvecConfirmation,
                                    progressionManche = progressionManche,
                                    actionsFinManche = {},
                                )
                            }
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel =
                                viewModel(key = "reseau-lettres-$index") {
                                    LettresRoundViewModel(
                                        manche.niveau,
                                        dictionnaire,
                                        configurationAlphabet,
                                        manche.niveau.dureeSecondesPartieStructuree,
                                        random = Random(seedManche),
                                    )
                                }
                            if (jeSuisDeclencheur) {
                                val uiState by roundVm.uiState.collectAsState()
                                LaunchedEffect(uiState.nombreVoyellesChoisi) {
                                    val n = uiState.nombreVoyellesChoisi
                                    if (n != null && etatManche is EtatManche.AttenteDeclenchement) {
                                        reseauVm.envoyerChoixVoyelles(n)
                                    }
                                }
                            } else {
                                // etatManche == EnCours ici (sinon on serait dans la branche d'attente
                                // ci-dessus) : le choix de l'adversaire est donc déjà reçu.
                                LaunchedEffect(roundVm) {
                                    val n = choixVoyellesRecu[index]
                                    if (n != null && !roundVm.uiState.value.tirageTermine) {
                                        roundVm.choisirNombreVoyelles(n)
                                    }
                                }
                            }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = null,
                                pseudo = null,
                                afficherResultat = false,
                                onMancheTerminee = { obtenu, motValide, _, dixMeilleursMots, longueurMotInvalide, motInvalide ->
                                    reseauVm.enregistrerMonResultat(
                                        ResultatDuoManche(
                                            ResultatManche(
                                            ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide, longueurMotInvalide,
                                            dureeSecondesManche = roundVm.uiState.value.dureeSecondesEcoulees,
                                            motInvalide = motInvalide,
                                        ),
                                            detail = motValide ?: motInvalide?.let { patronMotInvalide.format(it) } ?: texteAucunMot,
                                            dixMeilleursMots = dixMeilleursMots,
                                        ),
                                    )
                                },
                                onRetourEntrainement = onRetourAvecConfirmation,
                                progressionManche = progressionManche,
                                actionsFinManche = {},
                            )
                        }
                    }
                }

                if (demanderConfirmationRetour) {
                    AlertDialog(
                        onDismissRequest = { demanderConfirmationRetour = false },
                        title = { Text(stringResource(R.string.quitter_partie_reseau_titre)) },
                        text = { Text(stringResource(R.string.quitter_partie_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                demanderConfirmationRetour = false
                                reseauVm.annulerEtRevenirAuChoix()
                                navController.popBackStack(Routes.MENU, inclusive = false)
                            }) { Text(stringResource(R.string.action_quitter)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                        },
                    )
                }
            }

            composable(Routes.RECAP_PARTIE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etatConnexion by reseauVm.etat.collectAsState()
                val profilDistant = (etatConnexion as? EtatPartieReseau.Connecte)?.profilDistant
                val seedsValue by reseauVm.seeds.collectAsState()
                val seedsAuDemarrage = remember { seedsValue }
                LaunchedEffect(seedsValue) {
                    if (seedsValue.isNotEmpty() && seedsValue != seedsAuDemarrage) {
                        navController.navigate(Routes.JEU_PARTIE_RESEAU) {
                            popUpTo(Routes.RECAP_PARTIE_RESEAU) { inclusive = true }
                        }
                    }
                }
                val (finaux1, finaux2) = reseauVm.resultatsFinaux()
                val (mesResultats, resultatsAdversaire) = if (reseauVm.monTourDuo == TourDuo.JOUEUR1) {
                    finaux1 to finaux2
                } else {
                    finaux2 to finaux1
                }
                RecapPartieDuoScreen(
                    pseudo1 = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "Moi",
                    pseudo2 = profilDistant?.let { "${it.avatar} ${it.pseudo}" } ?: "Adversaire",
                    resultats1 = mesResultats,
                    resultats2 = resultatsAdversaire,
                    // Ma partie est déjà enregistrée en base dès que mon résultat et celui de
                    // l'adversaire sont connus pour toutes les manches (voir
                    // PartieReseauViewModel.enregistrerResultat) : ce bouton ne fait plus que
                    // fermer la connexion et naviguer.
                    onTerminer = {
                        reseauVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(Routes.MENU, inclusive = false)
                    },
                    onRejouer = if (reseauVm.monTourDuo == TourDuo.JOUEUR1) reseauVm::rejouer else null,
                    afficherAttenteRejouer = reseauVm.monTourDuo != TourDuo.JOUEUR1,
                )
                val tropheesDebloques by reseauVm.tropheesDebloques.collectAsState()
                TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { reseauVm.effacerTropheesDebloques() })
            }

            // Duel mots (retour utilisateur, 100 % réseau) : même pattern de connexion que
            // ci-dessus, avec son propre ViewModel (DuelMotsReseauViewModel) mais les mêmes
            // écrans réutilisables (ChoixRoleReseauScreen, AttenteHoteScreen,
            // RechercheInviteScreen, ConfirmationConnexionScreen).
            composable(Routes.CHOIX_ROLE_DUEL_MOTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                duelMotsVm.imposerSousMode(null)
                ChoixRoleReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onHeberger = { transport ->
                        duelMotsVm.choisirHote(transport)
                        navController.navigate(Routes.HOTE_ATTENTE_DUEL_MOTS)
                    },
                    onRejoindre = { transport ->
                        duelMotsVm.choisirInvite(transport)
                        navController.navigate(Routes.INVITE_RECHERCHE_DUEL_MOTS)
                    },
                    onRetour = { navController.popBackStack() },
                    contenuRegles = { ReglesModeDuelMots() },
                )
            }

            // Point d'entrée dédié au bouton "Duel points" du menu (retour utilisateur,
            // 2026-08-29) : mêmes écrans hôte/invité que ci-dessus, seul le sous-mode imposé
            // sur le ViewModel partagé change — voir Routes.CHOIX_ROLE_DUEL_POINTS.
            composable(Routes.CHOIX_ROLE_DUEL_POINTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                duelMotsVm.imposerSousMode(SousModeDuelMots.POINTS)
                ChoixRoleReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    onHeberger = { transport ->
                        duelMotsVm.choisirHote(transport)
                        navController.navigate(Routes.HOTE_ATTENTE_DUEL_MOTS)
                    },
                    onRejoindre = { transport ->
                        duelMotsVm.choisirInvite(transport)
                        navController.navigate(Routes.INVITE_RECHERCHE_DUEL_MOTS)
                    },
                    onRetour = { navController.popBackStack() },
                    contenuRegles = { ReglesModeDuelMots() },
                )
            }

            composable(Routes.HOTE_ATTENTE_DUEL_MOTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by duelMotsVm.etat.collectAsState()
                val routeEntreeDuelMots = if (duelMotsVm.sousModeImpose == SousModeDuelMots.POINTS) {
                    Routes.CHOIX_ROLE_DUEL_POINTS
                } else {
                    Routes.CHOIX_ROLE_DUEL_MOTS
                }
                LaunchedEffect(etat) {
                    if (etat is EtatPartieReseau.Connecte) {
                        navController.navigate(Routes.DUEL_MOTS_CONNEXION) {
                            popUpTo(routeEntreeDuelMots) { inclusive = false }
                        }
                    }
                }
                val etatActuel = etat
                AttenteHoteScreen(
                    nomServiceAffiche = (etatActuel as? EtatPartieReseau.AttenteHote)?.nomServiceAffiche,
                    erreur = (etatActuel as? EtatPartieReseau.Erreur)?.message,
                    onAnnulerErreur = {
                        duelMotsVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(routeEntreeDuelMots, inclusive = false)
                    },
                    onAnnuler = {
                        duelMotsVm.annulerEtRevenirAuChoix()
                        navController.popBackStack()
                    },
                )
            }

            composable(Routes.INVITE_RECHERCHE_DUEL_MOTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by duelMotsVm.etat.collectAsState()
                val parties by duelMotsVm.partiesTrouvees.collectAsState()
                val routeEntreeDuelMots = if (duelMotsVm.sousModeImpose == SousModeDuelMots.POINTS) {
                    Routes.CHOIX_ROLE_DUEL_POINTS
                } else {
                    Routes.CHOIX_ROLE_DUEL_MOTS
                }
                LaunchedEffect(etat) {
                    if (etat is EtatPartieReseau.Connecte) {
                        navController.navigate(Routes.DUEL_MOTS_CONNEXION) {
                            popUpTo(routeEntreeDuelMots) { inclusive = false }
                        }
                    }
                }
                val etatActuel = etat
                RechercheInviteScreen(
                    parties = parties,
                    connexionEnCours = etatActuel is EtatPartieReseau.ConnexionEnCours,
                    erreur = (etatActuel as? EtatPartieReseau.Erreur)?.message,
                    onSelectionner = { duelMotsVm.rejoindre(it) },
                    onAnnulerErreur = {
                        duelMotsVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(routeEntreeDuelMots, inclusive = false)
                    },
                    onAnnuler = {
                        duelMotsVm.annulerEtRevenirAuChoix()
                        navController.popBackStack()
                    },
                )
            }

            composable(Routes.DUEL_MOTS_CONNEXION) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val etat by duelMotsVm.etat.collectAsState()
                val etatConnecte = etat as? EtatPartieReseau.Connecte
                if (etatConnecte != null) {
                    ConfirmationConnexionScreen(
                        profilDistant = etatConnecte.profilDistant,
                        onContinuer = {
                            if (etatConnecte.role == RoleReseau.HOTE) {
                                navController.navigate(Routes.CHOIX_MODE_DUEL_MOTS)
                            } else {
                                navController.navigate(Routes.JEU_DUEL_MOTS)
                            }
                        },
                    )
                }
            }

            composable(Routes.CHOIX_MODE_DUEL_MOTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                ChoixModeDuelMotsScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                    sousModeImpose = duelMotsVm.sousModeImpose,
                    onDemarrer = { sousMode, niveau, objectifMots, atteindreExactement ->
                        duelMotsVm.demarrerCommeHote(sousMode, niveau, objectifMots, atteindreExactement)
                        navController.navigate(Routes.JEU_DUEL_MOTS)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.JEU_DUEL_MOTS) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val tirageTermine by duelMotsVm.tirageTermine.collectAsState()
                val seedActuel by duelMotsVm.seed.collectAsState()
                val onRetourMenu: () -> Unit = {
                    duelMotsVm.annulerEtRevenirAuChoix()
                    navController.popBackStack(Routes.MENU, inclusive = false)
                }
                val seedValue = seedActuel
                if (!tirageTermine || seedValue == null) {
                    AttenteReseauScreen(stringResource(R.string.duel_mots_en_attente_configuration))
                } else if (duelMotsVm.sousMode == SousModeDuelMots.CONFRONTATION || duelMotsVm.sousMode == SousModeDuelMots.POINTS) {
                    val lettresTirees by duelMotsVm.lettresTirees.collectAsState()
                    val indicesUtilises by duelMotsVm.indicesUtilises.collectAsState()
                    val motSaisi by duelMotsVm.motSaisi.collectAsState()
                    val motRejete by duelMotsVm.motRejete.collectAsState()
                    val raisonRejet by duelMotsVm.raisonRejet.collectAsState()
                    val motsTrouvesMoi by duelMotsVm.motsTrouvesMoi.collectAsState()
                    val motsTrouvesAdversaire by duelMotsVm.motsTrouvesAdversaire.collectAsState()
                    val gagnant by duelMotsVm.gagnant.collectAsState()
                    val tempsRestantSecondes by duelMotsVm.tempsRestantSecondes.collectAsState()
                    val motsPossiblesConfrontation by duelMotsVm.motsPossiblesConfrontation.collectAsState()
                    val raisonFinConfrontation by duelMotsVm.raisonFinConfrontation.collectAsState()
                    val etatConnexion by duelMotsVm.etat.collectAsState()
                    val profilDistant = (etatConnexion as? EtatPartieReseau.Connecte)?.profilDistant
                    DuelMotsConfrontationScreen(
                        pseudoMoi = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                        pseudoAdversaire = profilDistant?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                        couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                        sousMode = duelMotsVm.sousMode,
                        bareme = configurationAlphabet.baremeLettres,
                        atteindreExactement = duelMotsVm.atteindreExactement,
                        lettresTirees = lettresTirees,
                        indicesUtilises = indicesUtilises,
                        motSaisi = motSaisi,
                        motRejete = motRejete,
                        raisonRejet = raisonRejet,
                        seuilRequis = seuilLongueurDefiLettres(duelMotsVm.niveau),
                        objectifMots = duelMotsVm.objectifMots,
                        motsTrouvesMoi = motsTrouvesMoi,
                        motsTrouvesAdversaire = motsTrouvesAdversaire,
                        gagnant = gagnant,
                        tempsRestantSecondes = tempsRestantSecondes,
                        motsPossibles = motsPossiblesConfrontation,
                        raisonFin = raisonFinConfrontation,
                        peutRejouer = duelMotsVm.role == RoleReseau.HOTE,
                        onCliquerLettre = duelMotsVm::cliquerLettreConfrontation,
                        onAnnulerLettre = duelMotsVm::annulerLettreConfrontation,
                        onEffacerMot = duelMotsVm::effacerMotConfrontation,
                        onValider = duelMotsVm::validerMotConfrontation,
                        onRetirerMot = duelMotsVm::retirerMotConfrontation,
                        onRetour = onRetourMenu,
                        onRejouer = duelMotsVm::rejouer,
                    )
                } else {
                    val niveauDuo = duelMotsVm.niveau
                    val defiVm: DefiMotsMaxViewModel = viewModel(key = "duelmots-duo-$seedValue") {
                        DefiMotsMaxViewModel(
                            niveauDuo, dictionnaire, configurationAlphabet, defiRepository, tropheeRepository, profilId,
                            random = Random(seedValue), enregistrerResultat = false,
                        )
                    }
                    val etatRound by defiVm.uiState.collectAsState()
                    LaunchedEffect(Unit) {
                        if (!etatRound.tirageTermine) defiVm.choisirNombreVoyelles(NOMBRE_VOYELLES_DUEL_MOTS)
                    }
                    LaunchedEffect(etatRound.termine) {
                        if (etatRound.termine) {
                            duelMotsVm.envoyerResultatDuo(etatRound.motsTrouves)
                            navController.navigate(Routes.RESULTATS_DUEL_MOTS_DUO) {
                                popUpTo(Routes.JEU_DUEL_MOTS) { inclusive = true }
                            }
                        }
                    }
                    DefiMotsMaxScreen(
                        viewModel = defiVm,
                        pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                        couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                        onRetour = onRetourMenu,
                        actionsFin = {},
                    )
                }
            }

            composable(Routes.RESULTATS_DUEL_MOTS_DUO) { backStackEntry ->
                val duelMotsVm = duelMotsReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                    dictionnaire = dictionnaire,
                    configurationAlphabet = configurationAlphabet,
                    historiqueRepository = historiqueRepository,
                    tropheeRepository = tropheeRepository,
                    profilId = profilId,
                )
                val motsTrouvesMoi by duelMotsVm.motsTrouvesMoi.collectAsState()
                val motsTrouvesAdversaire by duelMotsVm.motsTrouvesAdversaire.collectAsState()
                val resultatAdversaireRecu by duelMotsVm.resultatAdversaireDuoRecu.collectAsState()
                val seedValue by duelMotsVm.seed.collectAsState()
                val niveauDuo = duelMotsVm.niveau
                val etatConnexion by duelMotsVm.etat.collectAsState()
                val profilDistant = (etatConnexion as? EtatPartieReseau.Connecte)?.profilDistant
                val seedAuDemarrage = remember { seedValue }
                LaunchedEffect(seedValue) {
                    if (seedValue != null && seedValue != seedAuDemarrage) {
                        navController.navigate(Routes.JEU_DUEL_MOTS) {
                            popUpTo(Routes.RESULTATS_DUEL_MOTS_DUO) { inclusive = true }
                        }
                    }
                }
                val motsPossibles = remember(seedValue, niveauDuo) {
                    val s = seedValue
                    if (s == null) {
                        emptyList()
                    } else {
                        val sac = SacLettres.creer(
                            configurationAlphabet.distributionBase,
                            configurationAlphabet.voyelles,
                            configurationAlphabet.lettresExcluesParNiveau.getValue(niveauDuo),
                        )
                        val tirage = TirageLettres.tirer(sac, NOMBRE_VOYELLES_DUEL_MOTS, TirageLettres.NOMBRE_LETTRES, Random(s))
                        dictionnaire.rechercherAuMoins(tirage, seuilLongueurDefiLettres(niveauDuo))
                            .distinct()
                            .sortedWith(compareByDescending<String> { it.length }.then(DictionnaireIndex.comparateurAlphabetiqueFrancais()))
                    }
                }
                DuelMotsResultatsScreen(
                    pseudoMoi = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: stringResource(R.string.duel_mots_moi),
                    pseudoAdversaire = profilDistant?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    motsTrouvesMoi = motsTrouvesMoi,
                    motsTrouvesAdversaire = motsTrouvesAdversaire,
                    resultatAdversaireRecu = resultatAdversaireRecu,
                    motsPossibles = motsPossibles,
                    peutRejouer = duelMotsVm.role == RoleReseau.HOTE,
                    onRetour = {
                        duelMotsVm.annulerEtRevenirAuChoix()
                        navController.popBackStack(Routes.MENU, inclusive = false)
                    },
                    onRejouer = duelMotsVm::rejouer,
                )
                val tropheesDebloques by duelMotsVm.tropheesDebloques.collectAsState()
                TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { duelMotsVm.effacerTropheesDebloques() })
            }
        }

        // Deux entrées distinctes depuis l'accueil (retour utilisateur : "Défi série" et "Défi
        // chrono" sont deux boutons séparés, pas des onglets d'un même écran).
        composable(Routes.CHOIX_DEFI_SERIE) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_serie),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                afficherDuree = false,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiLettres(niveau)) },
                onRetour = { navController.popBackStack() },
                contenuRegles = { ReglesModeDefiSerie() },
            )
        }

        composable(Routes.CHOIX_DEFI_CHRONO) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_chrono),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                afficherDuree = true,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoLettres(niveau)) },
                onRetour = { navController.popBackStack() },
                contenuRegles = { ReglesModeDefiChrono() },
            )
        }

        // Lettres uniquement (retour utilisateur) : onNiveauChiffresChoisi = null masque la
        // section chiffres de ChoixDefiScreen.
        composable(Routes.CHOIX_DEFI_MOTS_MAX) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_mots_max),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                afficherDuree = false,
                onNiveauChiffresChoisi = null,
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiMotsMax(niveau)) },
                onRetour = { navController.popBackStack() },
                contenuRegles = { ReglesModeDefiMots() },
            )
        }

        composable(
            route = Routes.JEU_DEFI_MOTS_MAX_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
        ) { backStackEntry ->
            val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val defiVm: DefiMotsMaxViewModel = viewModel(backStackEntry) {
                DefiMotsMaxViewModel(niveau, dictionnaire, configurationAlphabet, defiRepository, tropheeRepository, profilId)
            }
            val etat by defiVm.uiState.collectAsState()
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            DefiMotsMaxScreen(
                viewModel = defiVm,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                onRetour = {
                    if (etat.termine) {
                        navController.popBackStack(Routes.CHOIX_DEFI_MOTS_MAX, inclusive = false)
                    } else {
                        demanderConfirmationRetour = true
                    }
                },
                actionsFin = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { defiVm.recommencer() }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.action_recommencer))
                        }
                        OutlinedButton(
                            onClick = { navController.popBackStack(Routes.CHOIX_DEFI_MOTS_MAX, inclusive = false) },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(stringResource(R.string.action_retour)) }
                    }
                },
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(Routes.CHOIX_DEFI_MOTS_MAX, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        // Lettres uniquement (retour utilisateur) : onNiveauChiffresChoisi = null masque la
        // section chiffres de ChoixDefiScreen. afficherDuree = true (retour utilisateur) : la
        // durée du chrono varie par niveau, contrairement au défi mots max.
        composable(Routes.CHOIX_DEFI_POINTS) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_points),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                afficherDuree = true,
                onNiveauChiffresChoisi = null,
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiPoints(niveau)) },
                onRetour = { navController.popBackStack() },
                contenuRegles = { ReglesModeDefiPoints() },
            )
        }

        composable(
            route = Routes.JEU_DEFI_POINTS_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType },
                navArgument(Routes.ARG_JOUR_QUOTIDIEN) { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val jourQuotidien = backStackEntry.arguments!!.getString(Routes.ARG_JOUR_QUOTIDIEN)
            val defiVm: DefiObjectifsPointsViewModel = viewModel(backStackEntry) {
                DefiObjectifsPointsViewModel(
                    niveau, dictionnaire, configurationAlphabet, defiRepository, tropheeRepository, profilId,
                    defiQuotidienRepository = defiQuotidienRepository,
                    jourQuotidien = jourQuotidien,
                    context = context,
                )
            }
            val etat by defiVm.uiState.collectAsState()
            val cibleRetour = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_POINTS
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            DefiObjectifsPointsScreen(
                viewModel = defiVm,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                onRetour = {
                    if (etat.termine) {
                        navController.popBackStack(cibleRetour, inclusive = false)
                    } else {
                        demanderConfirmationRetour = true
                    }
                },
                actionsFin = {
                    if (jourQuotidien != null && etat.raisonFin == RaisonFinDefiObjectifsPoints.TOUS_OBJECTIFS_ATTEINTS) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = { defiVm.recommencer() }, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.action_recommencer))
                            }
                            OutlinedButton(
                                onClick = { navController.popBackStack(cibleRetour, inclusive = false) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text(stringResource(R.string.action_retour)) }
                        }
                    }
                },
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(cibleRetour, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        // Un seul niveau pour les deux modes (retour utilisateur) : cf. doc de ChoixDefiSansFauteScreen.
        composable(Routes.CHOIX_DEFI_SANS_FAUTE) {
            ChoixDefiSansFauteScreen(
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                onNiveauChoisi = { niveau -> navController.navigate(Routes.jeuDefiSansFaute(niveau)) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.JEU_DEFI_SANS_FAUTE_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
        ) { backStackEntry ->
            val niveauCode = backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!
            val niveauChiffres = Niveau.valueOf(niveauCode)
            val niveauLettres = NiveauLettres.valueOf(niveauCode)
            // Mode field non signifiant pour SANS_FAUTE (défi mixte, cf. doc DefiEntity.mode) :
            // toujours ModeJeu.CHIFFRES par convention.
            val defiVm: DefiViewModel = viewModel(backStackEntry) {
                DefiViewModel(defiRepository, tropheeRepository, profilId, ModeJeu.CHIFFRES, niveauCode, TypeDefi.SANS_FAUTE)
            }
            val index by defiVm.index.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val seuilLettres = seuilLongueurDefiLettres(niveauLettres)
            val libelleProgression = stringResource(R.string.defi_sans_faute_libelle_progression)
            val pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" }
            val couleurRang = couleurRangJoueur(profilId, tropheeRepository)
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            val onRetourAvecConfirmation: () -> Unit = {
                if (termine) {
                    navController.popBackStack(Routes.CHOIX_DEFI_SANS_FAUTE, inclusive = false)
                } else {
                    demanderConfirmationRetour = true
                }
            }

            val actionsFinManche: @Composable () -> Unit = {
                if (termine) {
                    ActionsFinDefi(
                        message = stringResource(R.string.defi_sans_faute_recap, index),
                        onRecommencer = { defiVm.recommencer() },
                        onChangerNiveau = { navController.popBackStack(Routes.CHOIX_DEFI_SANS_FAUTE, inclusive = false) },
                    )
                } else {
                    Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.action_continuer))
                    }
                }
            }
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })

            // Alternance stricte (retour utilisateur), chiffres en premier : index pair = chiffres,
            // impair = lettres. Clé sur essaiId (jamais réutilisé, y compris entre les deux modes) :
            // cf. commentaire équivalent sur le défi série mono-mode.
            if (index % 2 == 0) {
                val roundVm: ChiffresRoundViewModel =
                    viewModel(key = "defi-sansfaute-$essaiId") {
                        ChiffresRoundViewModel(niveauChiffres, niveauChiffres.dureeSecondesPartieStructuree, garantieSolution = true)
                    }
                ChiffresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = pseudo,
                    couleurRang = couleurRang,
                    progressionManche = "$index",
                    libelleProgression = libelleProgression,
                    onMancheTerminee = { obtenu, _ -> if (obtenu != 10) defiVm.echec() },
                    onRetourEntrainement = onRetourAvecConfirmation,
                    actionsFinManche = actionsFinManche,
                )
            } else {
                val roundVm: LettresRoundViewModel =
                    viewModel(key = "defi-sansfaute-$essaiId") {
                        LettresRoundViewModel(
                            niveauLettres,
                            dictionnaire,
                            configurationAlphabet,
                            niveauLettres.dureeSecondesPartieStructuree,
                            garantieMotSeuil = niveauLettres == NiveauLettres.MONIQUE || niveauLettres == NiveauLettres.MATHIEU,
                        )
                    }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = pseudo,
                    couleurRang = couleurRang,
                    progressionManche = "$index",
                    libelleProgression = libelleProgression,
                    onMancheTerminee = { _, motValide, meilleurMot, _, _, _ ->
                        val reussi = motValide != null && motEstReussiDefiLettres(niveauLettres, motValide, seuilLettres, meilleurMot)
                        if (!reussi) defiVm.echec()
                    },
                    onRetourEntrainement = onRetourAvecConfirmation,
                    actionsFinManche = actionsFinManche,
                    seuilRequis = seuilLettres,
                )
            }

            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(Routes.CHOIX_DEFI_SANS_FAUTE, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        composable(Routes.CHOIX_DEFI_QUOTIDIEN) {
            val jour = remember { LocalDate.now().toString() }
            val tirage = remember(profilId, jour) { DefiQuotidienTirage.pour(profilId, jour) }
            var niveauDejaReussi by remember(profilId, jour) { mutableStateOf<String?>(null) }
            var niveauxDejaReussis by remember(profilId, jour) { mutableStateOf<Set<String>>(emptySet()) }
            var serieActuelle by remember(profilId) { mutableStateOf(0) }
            LaunchedEffect(profilId, jour) {
                niveauDejaReussi = defiQuotidienRepository.niveauReussiAujourdhui(profilId, jour)
                niveauxDejaReussis = defiQuotidienRepository.niveauxReussisAujourdhui(profilId, jour)
                serieActuelle = defiQuotidienRepository.serieActuelle(profilId)
            }
            DefiQuotidienScreen(
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                tirage = tirage,
                niveauReussiAujourdhui = niveauDejaReussi,
                niveauxReussisAujourdhui = niveauxDejaReussis,
                serieActuelle = serieActuelle,
                onNiveauChiffresChoisi = { niveau ->
                    val route = if (tirage.type == TypeDefi.SERIE) {
                        Routes.jeuDefiChiffres(niveau, tirage.objectif, jour)
                    } else {
                        Routes.jeuDefiChronoChiffres(niveau, tirage.objectif, jour)
                    }
                    navController.navigate(route)
                },
                onNiveauLettresChoisi = { niveau ->
                    val route = when (tirage.type) {
                        TypeDefi.SERIE -> Routes.jeuDefiLettres(niveau, tirage.objectif, jour)
                        TypeDefi.OBJECTIFS_POINTS -> Routes.jeuDefiPoints(niveau, jour)
                        else -> Routes.jeuDefiChronoLettres(niveau, tirage.objectif, jour)
                    }
                    navController.navigate(route)
                },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.JEU_DEFI_CHIFFRES_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType },
                navArgument(Routes.ARG_OBJECTIF_QUOTIDIEN) { type = NavType.IntType; defaultValue = -1 },
                navArgument(Routes.ARG_JOUR_QUOTIDIEN) { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val objectifQuotidien = backStackEntry.arguments!!.getInt(Routes.ARG_OBJECTIF_QUOTIDIEN)
            val jourQuotidien = backStackEntry.arguments!!.getString(Routes.ARG_JOUR_QUOTIDIEN)
            val defiVm: DefiViewModel = viewModel(backStackEntry) {
                DefiViewModel(
                    defiRepository, tropheeRepository, profilId, ModeJeu.CHIFFRES, niveau.name, TypeDefi.SERIE,
                    defiQuotidienRepository = defiQuotidienRepository, jourQuotidien = jourQuotidien, context = context,
                )
            }
            val index by defiVm.index.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            // Résultat de la manche en cours, connu seulement une fois qu'elle se termine
            // (via onMancheTerminee ci-dessous) : remis à null à chaque nouvelle manche.
            // Sans lui, le "+1" ci-dessous restait vrai pendant toute la manche SUIVANT
            // celle qui amenait index à objectifQuotidien - 1 (y compris avant même qu'elle
            // ait commencé), déclenchant une victoire prématurée et un compteur gonflé d'une
            // unité (bug remonté par l'utilisateur).
            var derniereMancheReussie by remember(essaiId) { mutableStateOf<Boolean?>(null) }
            // Compte de réussites confirmées, y compris la manche qui vient de se terminer avec
            // succès mais pas encore validée par "Continuer" (retour utilisateur : le défi
            // quotidien doit s'arrêter DÈS que l'objectif est atteint, pas à la manche suivante).
            val objectifAtteint = jourQuotidien != null &&
                (if (termine) index else if (derniereMancheReussie == true) index + 1 else index) >= objectifQuotidien
            if (jourQuotidien != null) {
                // La réussite du jour et la mise à jour du widget sont enregistrées à l'intérieur
                // de DefiViewModel.objectifQuotidienAtteint() (viewModelScope), pas ici : un
                // retour arrière juste après ne peut donc plus les interrompre.
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) defiVm.objectifQuotidienAtteint()
                }
            }
            val cibleRetour = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            val onRetourAvecConfirmation: () -> Unit = {
                if (termine) navController.popBackStack(cibleRetour, inclusive = false) else demanderConfirmationRetour = true
            }
            // Solution exacte toujours garantie en défi série, même sur Monique/Mathieu
            // (retour utilisateur) : la série ne doit s'arrêter que sur une erreur du
            // joueur. Le chrono reste celui de la partie solo pour ce niveau (retour
            // utilisateur). Clé sur essaiId (jamais réutilisé), pas index (qui revient à 0
            // après "Recommencer" et renverrait sinon l'ancien ViewModel déjà terminé).
            val roundVm: ChiffresRoundViewModel =
                viewModel(key = "defi-chiffres-$essaiId") {
                    ChiffresRoundViewModel(niveau, niveau.dureeSecondesPartieStructuree, garantieSolution = true)
                }
            ChiffresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                progressionManche = "$index",
                libelleProgression = "Série",
                onMancheTerminee = { obtenu, _ ->
                    derniereMancheReussie = obtenu == 10
                    if (obtenu != 10) defiVm.echec()
                },
                onRetourEntrainement = onRetourAvecConfirmation,
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Série terminée : $index réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = { navController.popBackStack(cibleRetour, inclusive = false) },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_continuer)) }
                    }
                },
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(cibleRetour, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        composable(
            route = Routes.JEU_DEFI_LETTRES_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType },
                navArgument(Routes.ARG_OBJECTIF_QUOTIDIEN) { type = NavType.IntType; defaultValue = -1 },
                navArgument(Routes.ARG_JOUR_QUOTIDIEN) { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val objectifQuotidien = backStackEntry.arguments!!.getInt(Routes.ARG_OBJECTIF_QUOTIDIEN)
            val jourQuotidien = backStackEntry.arguments!!.getString(Routes.ARG_JOUR_QUOTIDIEN)
            val defiVm: DefiViewModel = viewModel(backStackEntry) {
                DefiViewModel(
                    defiRepository, tropheeRepository, profilId, ModeJeu.LETTRES, niveau.name, TypeDefi.SERIE,
                    defiQuotidienRepository = defiQuotidienRepository, jourQuotidien = jourQuotidien, context = context,
                )
            }
            val index by defiVm.index.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val seuil = seuilLongueurDefiLettres(niveau)
            // Résultat de la manche en cours, connu seulement une fois qu'elle se termine
            // (via onMancheTerminee ci-dessous) : cf. commentaire équivalent sur le défi chiffres.
            var derniereMancheReussie by remember(essaiId) { mutableStateOf<Boolean?>(null) }
            val objectifAtteint = jourQuotidien != null &&
                (if (termine) index else if (derniereMancheReussie == true) index + 1 else index) >= objectifQuotidien
            if (jourQuotidien != null) {
                // La réussite du jour et la mise à jour du widget sont enregistrées à l'intérieur
                // de DefiViewModel.objectifQuotidienAtteint() (viewModelScope), pas ici.
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) defiVm.objectifQuotidienAtteint()
                }
            }
            val cibleRetour = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            val onRetourAvecConfirmation: () -> Unit = {
                if (termine) navController.popBackStack(cibleRetour, inclusive = false) else demanderConfirmationRetour = true
            }
            // Clé sur essaiId (jamais réutilisé), pas index : cf. commentaire équivalent
            // sur le défi chiffres.
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-lettres-$essaiId") {
                    LettresRoundViewModel(
                        niveau,
                        dictionnaire,
                        configurationAlphabet,
                        niveau.dureeSecondesPartieStructuree,
                        garantieMotSeuil = niveau == NiveauLettres.MONIQUE || niveau == NiveauLettres.MATHIEU,
                    )
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                progressionManche = "$index",
                libelleProgression = "Série",
                onMancheTerminee = { _, motValide, meilleurMot, _, _, _ ->
                    val reussi = motValide != null && motEstReussiDefiLettres(niveau, motValide, seuil, meilleurMot)
                    derniereMancheReussie = reussi
                    if (!reussi) defiVm.echec()
                },
                onRetourEntrainement = onRetourAvecConfirmation,
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Série terminée : $index réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = { navController.popBackStack(cibleRetour, inclusive = false) },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_continuer)) }
                    }
                },
                seuilRequis = seuil,
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(cibleRetour, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        composable(
            route = Routes.JEU_DEFI_CHRONO_CHIFFRES_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType },
                navArgument(Routes.ARG_OBJECTIF_QUOTIDIEN) { type = NavType.IntType; defaultValue = -1 },
                navArgument(Routes.ARG_JOUR_QUOTIDIEN) { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val objectifQuotidien = backStackEntry.arguments!!.getInt(Routes.ARG_OBJECTIF_QUOTIDIEN)
            val jourQuotidien = backStackEntry.arguments!!.getString(Routes.ARG_JOUR_QUOTIDIEN)
            val defiVm: DefiViewModel = viewModel(backStackEntry) {
                DefiViewModel(
                    defiRepository,
                    tropheeRepository,
                    profilId,
                    ModeJeu.CHIFFRES,
                    niveau.name,
                    TypeDefi.CHRONO,
                    budgetSecondesDefiChrono(niveau),
                    defiQuotidienRepository = defiQuotidienRepository,
                    jourQuotidien = jourQuotidien,
                    context = context,
                )
            }
            val reussites by defiVm.reussites.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            // Résultat de la manche en cours, connu seulement une fois qu'elle se termine
            // (via onMancheTerminee ci-dessous) : cf. commentaire équivalent sur le défi série.
            var derniereMancheReussie by remember(essaiId) { mutableStateOf<Boolean?>(null) }
            val objectifAtteint = jourQuotidien != null &&
                (if (termine) reussites else if (derniereMancheReussie == true) reussites + 1 else reussites) >= objectifQuotidien
            if (jourQuotidien != null) {
                // La réussite du jour et la mise à jour du widget sont enregistrées à l'intérieur
                // de DefiViewModel.objectifQuotidienAtteint() (viewModelScope), pas ici.
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) defiVm.objectifQuotidienAtteint()
                }
            }
            val cibleRetour = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            val onRetourAvecConfirmation: () -> Unit = {
                if (termine) navController.popBackStack(cibleRetour, inclusive = false) else demanderConfirmationRetour = true
            }
            // Chaque manche démarre avec le temps restant du budget global (retour
            // utilisateur : le défi chrono continue même après une erreur, seul
            // l'épuisement du temps l'arrête) ; les règles du niveau (cible, opérations,
            // garantie de solution) s'appliquent normalement, sans forçage contrairement au
            // défi série.
            val roundVm: ChiffresRoundViewModel =
                viewModel(key = "defi-chrono-chiffres-$essaiId") {
                    ChiffresRoundViewModel(niveau, defiVm.dureeProchaineManche())
                }
            ChiffresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                progressionManche = "$reussites",
                libelleProgression = "Réussites",
                // Réussite comme échec attendent le "Continuer" du joueur avant d'enchaîner
                // (retour utilisateur) : sur un échec, le panneau de résultat (compte obtenu)
                // est affiché — enchaîner tout de suite sur la manche suivante (ancien
                // comportement) le faisait disparaître aussitôt affiché.
                onMancheTerminee = { obtenu, _ ->
                    derniereMancheReussie = obtenu == 10
                },
                onRetourEntrainement = onRetourAvecConfirmation,
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Défi terminé : $reussites réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = { navController.popBackStack(cibleRetour, inclusive = false) },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheChronoTerminee(derniereMancheReussie == true) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.action_continuer))
                        }
                    }
                },
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(cibleRetour, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }

        composable(
            route = Routes.JEU_DEFI_CHRONO_LETTRES_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType },
                navArgument(Routes.ARG_OBJECTIF_QUOTIDIEN) { type = NavType.IntType; defaultValue = -1 },
                navArgument(Routes.ARG_JOUR_QUOTIDIEN) { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
            val objectifQuotidien = backStackEntry.arguments!!.getInt(Routes.ARG_OBJECTIF_QUOTIDIEN)
            val jourQuotidien = backStackEntry.arguments!!.getString(Routes.ARG_JOUR_QUOTIDIEN)
            val defiVm: DefiViewModel = viewModel(backStackEntry) {
                DefiViewModel(
                    defiRepository,
                    tropheeRepository,
                    profilId,
                    ModeJeu.LETTRES,
                    niveau.name,
                    TypeDefi.CHRONO,
                    budgetSecondesDefiChrono(niveau),
                    defiQuotidienRepository = defiQuotidienRepository,
                    jourQuotidien = jourQuotidien,
                    context = context,
                )
            }
            val reussites by defiVm.reussites.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val seuil = seuilLongueurDefiLettres(niveau)
            // Résultat de la manche en cours, connu seulement une fois qu'elle se termine
            // (via onMancheTerminee ci-dessous) : cf. commentaire équivalent sur le défi série.
            var derniereMancheReussie by remember(essaiId) { mutableStateOf<Boolean?>(null) }
            val objectifAtteint = jourQuotidien != null &&
                (if (termine) reussites else if (derniereMancheReussie == true) reussites + 1 else reussites) >= objectifQuotidien
            if (jourQuotidien != null) {
                // La réussite du jour et la mise à jour du widget sont enregistrées à l'intérieur
                // de DefiViewModel.objectifQuotidienAtteint() (viewModelScope), pas ici.
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) defiVm.objectifQuotidienAtteint()
                }
            }
            val cibleRetour = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
            var demanderConfirmationRetour by remember { mutableStateOf(false) }
            val onRetourAvecConfirmation: () -> Unit = {
                if (termine) navController.popBackStack(cibleRetour, inclusive = false) else demanderConfirmationRetour = true
            }
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-chrono-lettres-$essaiId") {
                    LettresRoundViewModel(niveau, dictionnaire, configurationAlphabet, defiVm.dureeProchaineManche())
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                couleurRang = couleurRangJoueur(profilId, tropheeRepository),
                progressionManche = "$reussites",
                libelleProgression = "Réussites",
                // Réussite comme échec attendent le "Continuer" du joueur avant d'enchaîner
                // (retour utilisateur) : sur un échec, le mot le plus long possible est
                // affiché dans le panneau de résultat — enchaîner tout de suite sur la manche
                // suivante (ancien comportement) le faisait disparaître aussitôt affiché.
                onMancheTerminee = { _, motValide, meilleurMot, _, _, _ ->
                    derniereMancheReussie = motValide != null && motEstReussiDefiLettres(niveau, motValide, seuil, meilleurMot)
                },
                onRetourEntrainement = onRetourAvecConfirmation,
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Défi terminé : $reussites réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = { navController.popBackStack(cibleRetour, inclusive = false) },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheChronoTerminee(derniereMancheReussie == true) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.action_continuer))
                        }
                    }
                },
                seuilRequis = seuil,
            )
            val tropheesDebloques by defiVm.tropheesDebloques.collectAsState()
            TropheesDebloquesDialog(tropheesDebloques, nomJoueur = profilActif?.pseudo, onDismiss = { defiVm.effacerTropheesDebloques() })
            if (demanderConfirmationRetour) {
                AlertDialog(
                    onDismissRequest = { demanderConfirmationRetour = false },
                    title = { Text(stringResource(R.string.quitter_defi_titre)) },
                    text = { Text(stringResource(R.string.quitter_partie_message)) },
                    confirmButton = {
                        TextButton(onClick = {
                            demanderConfirmationRetour = false
                            navController.popBackStack(cibleRetour, inclusive = false)
                        }) { Text(stringResource(R.string.action_quitter)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { demanderConfirmationRetour = false }) { Text(stringResource(R.string.action_annuler)) }
                    },
                )
            }
        }
    }
}

/** Panneau de fin de défi, commun aux quatre types de manches (série/chrono × chiffres/lettres). */
@Composable
private fun ActionsFinDefi(message: String, onRecommencer: () -> Unit, onChangerNiveau: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(message)
        Button(onClick = onRecommencer, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_recommencer)) }
        OutlinedButton(onClick = onChangerNiveau, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_retour)) }
    }
}

/**
 * Panneau affiché dès que l'objectif du défi quotidien est atteint (retour utilisateur : il ne
 * faut pas laisser continuer à jouer après la réussite, contrairement au défi série/chrono
 * classique qui n'a pas d'objectif).
 */
@Composable
private fun DefiQuotidienGagne(onTerminer: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.defi_quotidien_remporte))
        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_terminer)) }
    }
}
