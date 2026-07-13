package fr.pierre.chiffreslettres.ui.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.ReglagesStore
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.AProposScreen
import fr.pierre.chiffreslettres.ui.apropos.ReglesDuJeuScreen
import fr.pierre.chiffreslettres.ui.apropos.VersionsScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundScreen
import fr.pierre.chiffreslettres.ui.chiffres.ChiffresRoundViewModel
import fr.pierre.chiffreslettres.ui.entrainement.ChoixModeScreen
import fr.pierre.chiffreslettres.ui.entrainement.ChoixNiveauChiffresScreen
import fr.pierre.chiffreslettres.ui.entrainement.ChoixNiveauLettresScreen
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
import fr.pierre.chiffreslettres.ui.reglages.ReglagesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesScreen
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
    profilActifStore: ProfilActifStore,
    reglagesStore: ReglagesStore,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val profilActifIdStore by profilActifStore.profilActifId.collectAsState(initial = null)
    val profilActif = profils.find { it.id == profilActifIdStore } ?: profils.firstOrNull()
    val profilId = profilActif?.id ?: -1L
    val dureeChiffres by reglagesStore.dureeChiffresSecondes.collectAsState(initial = 45)
    val dureeLettres by reglagesStore.dureeLettresSecondes.collectAsState(initial = 40)

    NavHost(navController = navController, startDestination = Routes.MENU, modifier = modifier) {
        composable(Routes.MENU) {
            MenuPrincipalScreen(
                pseudoActif = profilActif?.pseudo ?: "…",
                onEntrainementLibre = { navController.navigate(Routes.ENTRAINEMENT_GRAPH) },
                onPartieStructuree = { navController.navigate(Routes.PARTIE_GRAPH) },
                onStatistiques = { navController.navigate(Routes.STATISTIQUES) },
                onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                onReglages = { navController.navigate(Routes.REGLAGES) },
                onAPropos = { navController.navigate(Routes.A_PROPOS) },
            )
        }

        composable(Routes.REGLAGES) {
            ReglagesScreen(profilRepository = profilRepository, reglagesStore = reglagesStore)
        }

        composable(Routes.A_PROPOS) {
            AProposScreen(
                onReglesDuJeu = { navController.navigate(Routes.REGLES_DU_JEU) },
                onVersions = { navController.navigate(Routes.VERSIONS) },
            )
        }

        composable(Routes.REGLES_DU_JEU) {
            ReglesDuJeuScreen()
        }

        composable(Routes.VERSIONS) {
            VersionsScreen()
        }

        composable(Routes.CHANGER_PROFIL) {
            ChangerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
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
            )
        }

        composable(Routes.STATISTIQUES) {
            StatistiquesScreen(profilRepository = profilRepository, historiqueRepository = historiqueRepository)
        }

        navigation(startDestination = Routes.CHOIX_MODE, route = Routes.ENTRAINEMENT_GRAPH) {
            composable(Routes.CHOIX_MODE) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                val score by entrainementVm.scoreCumule.collectAsState()
                ChoixModeScreen(
                    scoreCumule = score,
                    onChoixChiffres = { navController.navigate(Routes.CHOIX_NIVEAU_CHIFFRES) },
                    onChoixLettres = { navController.navigate(Routes.CHOIX_NIVEAU_LETTRES) },
                )
            }

            composable(Routes.CHOIX_NIVEAU_CHIFFRES) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                val score by entrainementVm.scoreCumule.collectAsState()
                ChoixNiveauChiffresScreen(
                    scoreCumule = score,
                    onNiveauChoisi = { niveau -> navController.navigate(Routes.jeuChiffres(niveau)) },
                )
            }

            composable(Routes.CHOIX_NIVEAU_LETTRES) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                val score by entrainementVm.scoreCumule.collectAsState()
                ChoixNiveauLettresScreen(
                    scoreCumule = score,
                    onNiveauChoisi = { niveau -> navController.navigate(Routes.jeuLettres(niveau)) },
                )
            }

            composable(
                route = Routes.JEU_CHIFFRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                val score by entrainementVm.scoreCumule.collectAsState()
                val roundVm: ChiffresRoundViewModel =
                    viewModel(backStackEntry) { ChiffresRoundViewModel(niveau, dureeChiffres) }
                ChiffresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = score,
                    onMancheTerminee = { obtenu -> entrainementVm.enregistrerMancheChiffres(niveau, obtenu) },
                    actionsFinManche = {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(onClick = {
                                navController.navigate(Routes.jeuChiffres(niveau)) {
                                    popUpTo(Routes.JEU_CHIFFRES_PATTERN) { inclusive = true }
                                }
                            }) { Text("Rejouer") }
                            OutlinedButton(onClick = {
                                navController.popBackStack(Routes.CHOIX_NIVEAU_CHIFFRES, inclusive = false)
                            }) { Text("Changer de niveau") }
                            OutlinedButton(onClick = {
                                entrainementVm.terminerEtEnregistrer()
                                navController.popBackStack(Routes.MENU, inclusive = false)
                            }) { Text("Arrêter") }
                        }
                    },
                )
            }

            composable(
                route = Routes.JEU_LETTRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                val score by entrainementVm.scoreCumule.collectAsState()
                val roundVm: LettresRoundViewModel =
                    viewModel(backStackEntry) { LettresRoundViewModel(niveau, dictionnaire, dureeLettres) }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = score,
                    onMancheTerminee = { obtenu, motValide -> entrainementVm.enregistrerMancheLettres(niveau, obtenu, motValide) },
                    actionsFinManche = {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(onClick = {
                                navController.navigate(Routes.jeuLettres(niveau)) {
                                    popUpTo(Routes.JEU_LETTRES_PATTERN) { inclusive = true }
                                }
                            }) { Text("Rejouer") }
                            OutlinedButton(onClick = {
                                navController.popBackStack(Routes.CHOIX_NIVEAU_LETTRES, inclusive = false)
                            }) { Text("Changer de niveau") }
                            OutlinedButton(onClick = {
                                entrainementVm.terminerEtEnregistrer()
                                navController.popBackStack(Routes.MENU, inclusive = false)
                            }) { Text("Arrêter") }
                        }
                    },
                )
            }
        }

        navigation(startDestination = Routes.CONFIGURATION_PARTIE, route = Routes.PARTIE_GRAPH) {
            composable(Routes.CONFIGURATION_PARTIE) { backStackEntry ->
                val partieVm = partieViewModel(navController, backStackEntry)
                ConfigurationPartieScreen(onDemarrer = { sequence ->
                    partieVm.demarrer(sequence)
                    navController.navigate(Routes.JEU_PARTIE)
                })
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
                    val actionsFinManche: @Composable () -> Unit = {
                        Button(onClick = { partieVm.mancheSuivante() }) {
                            Text(if (estDerniere) "Voir le résultat" else "Manche suivante")
                        }
                    }
                    when (manche) {
                        is ManchePlanifiee.Chiffres -> {
                            val roundVm: ChiffresRoundViewModel =
                                viewModel(key = "partie-chiffres-$index") {
                                    ChiffresRoundViewModel(manche.niveau, dureeChiffres)
                                }
                            ChiffresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                onMancheTerminee = { obtenu ->
                                    partieVm.enregistrerResultat(ResultatManche(ModeJeu.CHIFFRES, manche.niveau.name, obtenu))
                                },
                                actionsFinManche = actionsFinManche,
                            )
                        }
                        is ManchePlanifiee.Lettres -> {
                            val roundVm: LettresRoundViewModel =
                                viewModel(key = "partie-lettres-$index") {
                                    LettresRoundViewModel(manche.niveau, dictionnaire, dureeLettres)
                                }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                onMancheTerminee = { obtenu, motValide ->
                                    partieVm.enregistrerResultat(
                                        ResultatManche(ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide),
                                    )
                                },
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
                        }
                        navController.popBackStack(Routes.MENU, inclusive = false)
                    },
                )
            }
        }
    }
}
