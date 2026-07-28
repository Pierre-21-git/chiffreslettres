package fr.pierre.chiffreslettres.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.AProposScreen
import fr.pierre.chiffreslettres.ui.apropos.ReglesDuJeuScreen
import fr.pierre.chiffreslettres.ui.apropos.VersionsScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundViewModel
import fr.pierre.chiffreslettres.ui.defi.ChoixDefiScreen
import fr.pierre.chiffreslettres.ui.defi.DefiQuotidienScreen
import fr.pierre.chiffreslettres.ui.defi.DefiViewModel
import fr.pierre.chiffreslettres.ui.defi.budgetSecondesDefiChrono
import fr.pierre.chiffreslettres.ui.defi.seuilLongueurDefiLettres
import fr.pierre.chiffreslettres.ui.entrainement.ChoixNiveauEntrainementScreen
import fr.pierre.chiffreslettres.ui.entrainement.EntrainementLibreViewModel
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundScreen
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundViewModel
import fr.pierre.chiffreslettres.ui.menu.MenuPrincipalScreen
import fr.pierre.chiffreslettres.ui.partie.ConfigurationPartieScreen
import fr.pierre.chiffreslettres.ui.partie.ManchePlanifiee
import fr.pierre.chiffreslettres.ui.partie.PartieStructureeViewModel
import fr.pierre.chiffreslettres.ui.partie.RecapPartieScreen
import fr.pierre.chiffreslettres.ui.profil.ChangerProfilScreen
import fr.pierre.chiffreslettres.ui.profil.CreerProfilScreen
import fr.pierre.chiffreslettres.ui.statistiques.MesStatistiquesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesGeneralesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesJoueurScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesScreen
import fr.pierre.chiffreslettres.ui.trophees.TropheesScreen
import java.time.LocalDate
import kotlinx.coroutines.launch

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
private fun partieViewModel(navController: NavHostController, backStackEntry: NavBackStackEntry): PartieStructureeViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.PARTIE_GRAPH) }
    return viewModel(parentEntry)
}

@Composable
fun AppNavHost(
    dictionnaire: DictionnaireIndex,
    profilRepository: ProfilRepository,
    historiqueRepository: HistoriqueRepository,
    defiRepository: DefiRepository,
    tropheeRepository: TropheeRepository,
    defiQuotidienRepository: DefiQuotidienRepository,
    profilActifStore: ProfilActifStore,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val profilActifIdStore by profilActifStore.profilActifId.collectAsState(initial = null)
    val profilActif = profils.find { it.id == profilActifIdStore } ?: profils.firstOrNull()
    val profilId = profilActif?.id ?: -1L

    NavHost(navController = navController, startDestination = Routes.MENU, modifier = modifier) {
        composable(Routes.MENU) {
            MenuPrincipalScreen(
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                onEntrainementLibre = { navController.navigate(Routes.ENTRAINEMENT_GRAPH) },
                onPartieStructuree = { navController.navigate(Routes.PARTIE_GRAPH) },
                onDefiSerie = { navController.navigate(Routes.CHOIX_DEFI_SERIE) },
                onDefiChrono = { navController.navigate(Routes.CHOIX_DEFI_CHRONO) },
                onDefiQuotidien = { navController.navigate(Routes.CHOIX_DEFI_QUOTIDIEN) },
                onStatistiques = { navController.navigate(Routes.STATISTIQUES) },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                onAPropos = { navController.navigate(Routes.A_PROPOS) },
            )
        }

        composable(Routes.A_PROPOS) {
            AProposScreen(
                onReglesDuJeu = { navController.navigate(Routes.REGLES_DU_JEU) },
                onVersions = { navController.navigate(Routes.VERSIONS) },
                onTrophees = { navController.navigate(Routes.TROPHEES_CATALOGUE) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.REGLES_DU_JEU) {
            ReglesDuJeuScreen(onRetour = { navController.popBackStack() })
        }

        composable(Routes.VERSIONS) {
            VersionsScreen(onRetour = { navController.popBackStack() })
        }

        composable(Routes.TROPHEES_CATALOGUE) {
            TropheesScreen(
                titre = "Trophées",
                tropheesDebloques = null,
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.TROPHEES_JOUEUR_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            LaunchedEffect(profilIdArg) { tropheeRepository.reevaluer(profilIdArg) }
            val debloques by tropheeRepository.tropheesDebloques(profilIdArg).collectAsState(initial = null)
            val tropheesDebloques = debloques?.associate { it.trophyId to it.dateDebloque } ?: emptyMap()
            TropheesScreen(
                titre = "Mes trophées",
                tropheesDebloques = tropheesDebloques,
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.CHANGER_PROFIL) {
            ChangerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                onProfilChoisi = { navController.popBackStack() },
                onCreerNouveauProfil = { navController.navigate(Routes.CREER_PROFIL) },
                onRetour = { navController.popBackStack() },
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

        composable(Routes.STATISTIQUES) {
            StatistiquesScreen(
                profilRepository = profilRepository,
                onProfilChoisi = { profilIdChoisi -> navController.navigate(Routes.statistiquesJoueur(profilIdChoisi)) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.STATISTIQUES_JOUEUR_PATTERN,
            arguments = listOf(navArgument(Routes.ARG_PROFIL_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val profilIdArg = backStackEntry.arguments!!.getLong(Routes.ARG_PROFIL_ID)
            StatistiquesJoueurScreen(
                profilId = profilIdArg,
                profilActifId = profilId,
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
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
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.STATISTIQUES_GENERALES) {
            StatistiquesGeneralesScreen(
                historiqueRepository = historiqueRepository,
                onRetour = { navController.popBackStack() },
            )
        }

        navigation(startDestination = Routes.CHOIX_NIVEAU_ENTRAINEMENT, route = Routes.ENTRAINEMENT_GRAPH) {
            composable(Routes.CHOIX_NIVEAU_ENTRAINEMENT) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                ChoixNiveauEntrainementScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuChiffres(niveau)) },
                    onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuLettres(niveau)) },
                    onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
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
                    onMancheTerminee = { obtenu -> entrainementVm.enregistrerMancheChiffres(niveau, obtenu) },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_NIVEAU_ENTRAINEMENT, inclusive = false) },
                    actionsFinManche = {
                        Button(
                            onClick = {
                                navController.navigate(Routes.jeuChiffres(niveau)) {
                                    popUpTo(Routes.JEU_CHIFFRES_PATTERN) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Rejouer") }
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
                    viewModel(backStackEntry) { LettresRoundViewModel(niveau, dictionnaire) }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                    onMancheTerminee = { obtenu, motValide -> entrainementVm.enregistrerMancheLettres(niveau, obtenu, motValide) },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_NIVEAU_ENTRAINEMENT, inclusive = false) },
                    actionsFinManche = {
                        Button(
                            onClick = {
                                navController.navigate(Routes.jeuLettres(niveau)) {
                                    popUpTo(Routes.JEU_LETTRES_PATTERN) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Rejouer") }
                    },
                )
            }
        }

        navigation(startDestination = Routes.CONFIGURATION_PARTIE, route = Routes.PARTIE_GRAPH) {
            composable(Routes.CONFIGURATION_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry)
                ConfigurationPartieScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    onDemarrer = { sequence ->
                        partieVm.demarrer(sequence)
                        navController.navigate(Routes.JEU_PARTIE)
                    },
                    onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.JEU_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry)
                val sequence by partieVm.sequence.collectAsState()
                val index by partieVm.index.collectAsState()
                val resultats by partieVm.resultats.collectAsState()
                val manche = sequence.getOrNull(index)

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
                            Text(if (estDerniere) "Voir le résultat" else "Manche suivante")
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
                                onMancheTerminee = { obtenu ->
                                    partieVm.enregistrerResultat(ResultatManche(ModeJeu.CHIFFRES, manche.niveau.name, obtenu))
                                },
                                onRetourEntrainement = {
                                    navController.popBackStack(Routes.CONFIGURATION_PARTIE, inclusive = false)
                                },
                                progressionManche = progressionManche,
                                actionsFinManche = actionsFinManche,
                            )
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel =
                                viewModel(key = "partie-lettres-$index") {
                                    LettresRoundViewModel(manche.niveau, dictionnaire, manche.niveau.dureeSecondesPartieStructuree)
                                }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                                onMancheTerminee = { obtenu, motValide ->
                                    partieVm.enregistrerResultat(
                                        ResultatManche(ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide),
                                    )
                                },
                                onRetourEntrainement = {
                                    navController.popBackStack(Routes.CONFIGURATION_PARTIE, inclusive = false)
                                },
                                progressionManche = progressionManche,
                                actionsFinManche = actionsFinManche,
                            )
                        }
                    }
                }
            }

            composable(Routes.RECAP_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry)
                val resultats by partieVm.resultats.collectAsState()
                val scope = rememberCoroutineScope()
                RecapPartieScreen(
                    resultats = resultats,
                    onTerminer = {
                        scope.launch {
                            historiqueRepository.enregistrerSession(profilId, TypePartie.STRUCTUREE, resultats)
                            tropheeRepository.reevaluer(profilId)
                        }
                        navController.popBackStack(Routes.MENU, inclusive = false)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }
        }

        // Deux entrées distinctes depuis l'accueil (retour utilisateur : "Défi série" et "Défi
        // chrono" sont deux boutons séparés, pas des onglets d'un même écran).
        composable(Routes.CHOIX_DEFI_SERIE) {
            ChoixDefiScreen(
                titre = "Défi série",
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                afficherDuree = false,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiLettres(niveau)) },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.CHOIX_DEFI_CHRONO) {
            ChoixDefiScreen(
                titre = "Défi chrono",
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                afficherDuree = true,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoLettres(niveau)) },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.CHOIX_DEFI_QUOTIDIEN) {
            val jour = remember { LocalDate.now().toString() }
            val tirage = remember(profilId, jour) { DefiQuotidienTirage.pour(profilId, jour) }
            var dejaReussi by remember(profilId, jour) { mutableStateOf(false) }
            var serieActuelle by remember(profilId) { mutableStateOf(0) }
            LaunchedEffect(profilId, jour) {
                dejaReussi = defiQuotidienRepository.reussiteDuJour(profilId, jour)
                serieActuelle = defiQuotidienRepository.serieActuelle(profilId)
            }
            DefiQuotidienScreen(
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                tirage = tirage,
                dejaReussiAujourdhui = dejaReussi,
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
                    val route = if (tirage.type == TypeDefi.SERIE) {
                        Routes.jeuDefiLettres(niveau, tirage.objectif, jour)
                    } else {
                        Routes.jeuDefiChronoLettres(niveau, tirage.objectif, jour)
                    }
                    navController.navigate(route)
                },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
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
                DefiViewModel(defiRepository, tropheeRepository, profilId, ModeJeu.CHIFFRES, niveau.name, TypeDefi.SERIE)
            }
            val index by defiVm.index.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            // Compte de réussites confirmées, y compris la manche qui vient de se terminer avec
            // succès mais pas encore validée par "Continuer" (retour utilisateur : le défi
            // quotidien doit s'arrêter DÈS que l'objectif est atteint, pas à la manche suivante).
            val objectifAtteint = jourQuotidien != null && (if (termine) index else index + 1) >= objectifQuotidien
            if (jourQuotidien != null) {
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) {
                        defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien)
                        tropheeRepository.reevaluer(profilId)
                    }
                }
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
                progressionManche = "$index",
                libelleProgression = "Série",
                onMancheTerminee = { obtenu -> if (obtenu != 10) defiVm.echec() },
                onRetourEntrainement = {
                    val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
                    navController.popBackStack(cible, inclusive = false)
                },
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Série terminée : $index réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = {
                                val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
                                navController.popBackStack(cible, inclusive = false)
                            },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text("Continuer") }
                    }
                },
            )
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
                DefiViewModel(defiRepository, tropheeRepository, profilId, ModeJeu.LETTRES, niveau.name, TypeDefi.SERIE)
            }
            val index by defiVm.index.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val seuil = seuilLongueurDefiLettres(niveau)
            val objectifAtteint = jourQuotidien != null && (if (termine) index else index + 1) >= objectifQuotidien
            if (jourQuotidien != null) {
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) {
                        defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien)
                        tropheeRepository.reevaluer(profilId)
                    }
                }
            }
            // Clé sur essaiId (jamais réutilisé), pas index : cf. commentaire équivalent
            // sur le défi chiffres.
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-lettres-$essaiId") {
                    LettresRoundViewModel(niveau, dictionnaire, niveau.dureeSecondesPartieStructuree)
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                progressionManche = "$index",
                libelleProgression = "Série",
                onMancheTerminee = { _, motValide ->
                    val reussi = motValide != null && motValide.length >= seuil
                    if (!reussi) defiVm.echec()
                },
                onRetourEntrainement = {
                    val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
                    navController.popBackStack(cible, inclusive = false)
                },
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Série terminée : $index réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = {
                                val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_SERIE
                                navController.popBackStack(cible, inclusive = false)
                            },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text("Continuer") }
                    }
                },
            )
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
                )
            }
            val reussites by defiVm.reussites.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val objectifAtteint = jourQuotidien != null && (if (termine) reussites else reussites + 1) >= objectifQuotidien
            if (jourQuotidien != null) {
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) {
                        defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien)
                        tropheeRepository.reevaluer(profilId)
                    }
                }
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
                progressionManche = "$reussites",
                libelleProgression = "Réussites",
                // Comme en défi série : un échec avance seul (pas de confirmation), une
                // réussite attend le "Continuer" du joueur avant d'enchaîner (retour
                // utilisateur, cohérence avec le défi série existant).
                onMancheTerminee = { obtenu -> if (obtenu != 10) defiVm.mancheChronoTerminee(false) },
                onRetourEntrainement = {
                    val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
                    navController.popBackStack(cible, inclusive = false)
                },
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Défi terminé : $reussites réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = {
                                val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
                                navController.popBackStack(cible, inclusive = false)
                            },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheChronoTerminee(true) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Continuer")
                        }
                    }
                },
            )
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
                )
            }
            val reussites by defiVm.reussites.collectAsState()
            val essaiId by defiVm.essaiId.collectAsState()
            val termine by defiVm.termine.collectAsState()
            val seuil = seuilLongueurDefiLettres(niveau)
            val objectifAtteint = jourQuotidien != null && (if (termine) reussites else reussites + 1) >= objectifQuotidien
            if (jourQuotidien != null) {
                LaunchedEffect(objectifAtteint) {
                    if (objectifAtteint) {
                        defiQuotidienRepository.enregistrerReussite(profilId, jourQuotidien)
                        tropheeRepository.reevaluer(profilId)
                    }
                }
            }
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-chrono-lettres-$essaiId") {
                    LettresRoundViewModel(niveau, dictionnaire, defiVm.dureeProchaineManche())
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                progressionManche = "$reussites",
                libelleProgression = "Réussites",
                // Comme en défi série : un échec avance seul (pas de confirmation), une
                // réussite attend le "Continuer" du joueur avant d'enchaîner.
                onMancheTerminee = { _, motValide ->
                    val reussi = motValide != null && motValide.length >= seuil
                    if (!reussi) defiVm.mancheChronoTerminee(false)
                },
                onRetourEntrainement = {
                    val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
                    navController.popBackStack(cible, inclusive = false)
                },
                actionsFinManche = {
                    if (objectifAtteint) {
                        DefiQuotidienGagne(onTerminer = { navController.popBackStack(Routes.CHOIX_DEFI_QUOTIDIEN, inclusive = false) })
                    } else if (termine) {
                        ActionsFinDefi(
                            message = "Défi terminé : $reussites réussite(s)",
                            onRecommencer = { defiVm.recommencer() },
                            onChangerNiveau = {
                                val cible = if (jourQuotidien != null) Routes.CHOIX_DEFI_QUOTIDIEN else Routes.CHOIX_DEFI_CHRONO
                                navController.popBackStack(cible, inclusive = false)
                            },
                        )
                    } else {
                        Button(onClick = { defiVm.mancheChronoTerminee(true) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Continuer")
                        }
                    }
                },
            )
        }
    }
}

/** Panneau de fin de défi, commun aux quatre types de manches (série/chrono × chiffres/lettres). */
@Composable
private fun ActionsFinDefi(message: String, onRecommencer: () -> Unit, onChangerNiveau: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(message)
        Button(onClick = onRecommencer, modifier = Modifier.fillMaxWidth()) { Text("Recommencer") }
        OutlinedButton(onClick = onChangerNiveau, modifier = Modifier.fillMaxWidth()) { Text("Changer de niveau") }
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
        Text("Défi quotidien remporté !")
        Button(onClick = onTerminer, modifier = Modifier.fillMaxWidth()) { Text("Terminer") }
    }
}
