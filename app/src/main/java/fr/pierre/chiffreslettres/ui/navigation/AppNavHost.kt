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
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesJoueurScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesScreen
import fr.pierre.chiffreslettres.ui.trophees.TropheesScreen
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
                pseudoActif = profilActif?.pseudo ?: "…",
                onEntrainementLibre = { navController.navigate(Routes.ENTRAINEMENT_GRAPH) },
                onPartieStructuree = { navController.navigate(Routes.PARTIE_GRAPH) },
                onDefi = { navController.navigate(Routes.DEFI_GRAPH) },
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
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
                profilRepository = profilRepository,
                tropheeRepository = tropheeRepository,
                onVoirTrophees = { navController.navigate(Routes.tropheesJoueur(profilIdArg)) },
                onRetour = { navController.popBackStack() },
            )
        }

        navigation(startDestination = Routes.CHOIX_NIVEAU_ENTRAINEMENT, route = Routes.ENTRAINEMENT_GRAPH) {
            composable(Routes.CHOIX_NIVEAU_ENTRAINEMENT) { backStackEntry ->
                val entrainementVm = entrainementViewModel(navController, backStackEntry, historiqueRepository, profilId)
                ChoixNiveauEntrainementScreen(
                    pseudoActif = profilActif?.pseudo ?: "…",
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
                    pseudo = profilActif?.pseudo,
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
                    pseudo = profilActif?.pseudo,
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
                    pseudoActif = profilActif?.pseudo ?: "…",
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
                                pseudo = profilActif?.pseudo,
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
                                pseudo = profilActif?.pseudo,
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

        navigation(startDestination = Routes.CHOIX_DEFI, route = Routes.DEFI_GRAPH) {
            composable(Routes.CHOIX_DEFI) {
                ChoixDefiScreen(
                    pseudoActif = profilActif?.pseudo ?: "…",
                    onNiveauChiffresSerieChoisi = { niveau -> navController.navigate(Routes.jeuDefiChiffres(niveau)) },
                    onNiveauLettresSerieChoisi = { niveau -> navController.navigate(Routes.jeuDefiLettres(niveau)) },
                    onNiveauChiffresChronoChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoChiffres(niveau)) },
                    onNiveauLettresChronoChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoLettres(niveau)) },
                    onChangerProfil = { navController.navigate(Routes.CHANGER_PROFIL) },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(
                route = Routes.JEU_DEFI_CHIFFRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val defiVm: DefiViewModel = viewModel(backStackEntry) {
                    DefiViewModel(defiRepository, tropheeRepository, profilId, ModeJeu.CHIFFRES, niveau.name, TypeDefi.SERIE)
                }
                val index by defiVm.index.collectAsState()
                val essaiId by defiVm.essaiId.collectAsState()
                val termine by defiVm.termine.collectAsState()
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
                    pseudo = profilActif?.pseudo,
                    progressionManche = "$index",
                    libelleProgression = "Série",
                    onMancheTerminee = { obtenu -> if (obtenu != 10) defiVm.echec() },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                    actionsFinManche = {
                        if (termine) {
                            ActionsFinDefi(
                                message = "Série terminée : $index réussite(s)",
                                onRecommencer = { defiVm.recommencer() },
                                onChangerNiveau = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                            )
                        } else {
                            Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text("Continuer") }
                        }
                    },
                )
            }

            composable(
                route = Routes.JEU_DEFI_LETTRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
                val defiVm: DefiViewModel = viewModel(backStackEntry) {
                    DefiViewModel(defiRepository, tropheeRepository, profilId, ModeJeu.LETTRES, niveau.name, TypeDefi.SERIE)
                }
                val index by defiVm.index.collectAsState()
                val essaiId by defiVm.essaiId.collectAsState()
                val termine by defiVm.termine.collectAsState()
                val seuil = seuilLongueurDefiLettres(niveau)
                // Clé sur essaiId (jamais réutilisé), pas index : cf. commentaire équivalent
                // sur le défi chiffres.
                val roundVm: LettresRoundViewModel =
                    viewModel(key = "defi-lettres-$essaiId") {
                        LettresRoundViewModel(niveau, dictionnaire, niveau.dureeSecondesPartieStructuree)
                    }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = profilActif?.pseudo,
                    progressionManche = "$index",
                    libelleProgression = "Série",
                    onMancheTerminee = { _, motValide ->
                        val reussi = motValide != null && motValide.length >= seuil
                        if (!reussi) defiVm.echec()
                    },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                    actionsFinManche = {
                        if (termine) {
                            ActionsFinDefi(
                                message = "Série terminée : $index réussite(s)",
                                onRecommencer = { defiVm.recommencer() },
                                onChangerNiveau = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                            )
                        } else {
                            Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text("Continuer") }
                        }
                    },
                )
            }

            composable(
                route = Routes.JEU_DEFI_CHRONO_CHIFFRES_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = Niveau.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
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
                    pseudo = profilActif?.pseudo,
                    progressionManche = "$reussites",
                    libelleProgression = "Réussites",
                    // Comme en défi série : un échec avance seul (pas de confirmation), une
                    // réussite attend le "Continuer" du joueur avant d'enchaîner (retour
                    // utilisateur, cohérence avec le défi série existant).
                    onMancheTerminee = { obtenu -> if (obtenu != 10) defiVm.mancheChronoTerminee(false) },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                    actionsFinManche = {
                        if (termine) {
                            ActionsFinDefi(
                                message = "Défi terminé : $reussites réussite(s)",
                                onRecommencer = { defiVm.recommencer() },
                                onChangerNiveau = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
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
                arguments = listOf(navArgument(Routes.ARG_NIVEAU) { type = NavType.StringType }),
            ) { backStackEntry ->
                val niveau = NiveauLettres.valueOf(backStackEntry.arguments!!.getString(Routes.ARG_NIVEAU)!!)
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
                val roundVm: LettresRoundViewModel =
                    viewModel(key = "defi-chrono-lettres-$essaiId") {
                        LettresRoundViewModel(niveau, dictionnaire, defiVm.dureeProchaineManche())
                    }
                LettresRoundScreen(
                    viewModel = roundVm,
                    scoreCumule = null,
                    pseudo = profilActif?.pseudo,
                    progressionManche = "$reussites",
                    libelleProgression = "Réussites",
                    // Comme en défi série : un échec avance seul (pas de confirmation), une
                    // réussite attend le "Continuer" du joueur avant d'enchaîner.
                    onMancheTerminee = { _, motValide ->
                        val reussi = motValide != null && motValide.length >= seuil
                        if (!reussi) defiVm.mancheChronoTerminee(false)
                    },
                    onRetourEntrainement = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
                    actionsFinManche = {
                        if (termine) {
                            ActionsFinDefi(
                                message = "Défi terminé : $reussites réussite(s)",
                                onRecommencer = { defiVm.recommencer() },
                                onChangerNiveau = { navController.popBackStack(Routes.CHOIX_DEFI, inclusive = false) },
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
