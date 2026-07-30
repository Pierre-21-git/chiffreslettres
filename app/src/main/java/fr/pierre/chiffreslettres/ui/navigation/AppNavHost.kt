package fr.pierre.chiffreslettres.ui.navigation

import android.content.Context
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
import androidx.compose.runtime.rememberCoroutineScope
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
import fr.pierre.chiffreslettres.data.ResultatManche
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.data.TypePartie
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.widget.DefiQuotidienWidgetProvider
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
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetLettres
import fr.pierre.chiffreslettres.ui.defi.motEstReussiDefiLettres
import fr.pierre.chiffreslettres.ui.defi.seuilLongueurDefiLettres
import fr.pierre.chiffreslettres.ui.entrainement.ChoixNiveauEntrainementScreen
import fr.pierre.chiffreslettres.ui.entrainement.EntrainementLibreViewModel
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundScreen
import fr.pierre.chiffreslettres.ui.lettres.LettresRoundViewModel
import fr.pierre.chiffreslettres.network.EtatManche
import fr.pierre.chiffreslettres.network.EtatPartieReseau
import fr.pierre.chiffreslettres.network.PartieReseauViewModel
import fr.pierre.chiffreslettres.network.RoleReseau
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
import fr.pierre.chiffreslettres.ui.partieduo.versTypePartie
import fr.pierre.chiffreslettres.ui.partieduo.versTypePartieReseau
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
import fr.pierre.chiffreslettres.ui.statistiques.MesStatistiquesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesGeneralesScreen
import fr.pierre.chiffreslettres.ui.statistiques.StatistiquesJoueurScreen
import fr.pierre.chiffreslettres.ui.trophees.TropheesScreen
import java.time.LocalDate
import kotlin.random.Random
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
private fun partieDuoViewModel(navController: NavHostController, backStackEntry: NavBackStackEntry): PartieDuoViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.PARTIE_DUO_GRAPH) }
    return viewModel(parentEntry)
}

@Composable
private fun partieReseauViewModel(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    context: Context,
    pseudo: String,
    avatar: String,
): PartieReseauViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.RESEAU_GRAPH) }
    return viewModel(parentEntry) { PartieReseauViewModel(context, pseudo, avatar) }
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
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val profilActifIdStore by profilActifStore.profilActifId.collectAsState(initial = null)
    val profilActif = profils.find { it.id == profilActifIdStore } ?: profils.firstOrNull()
    val profilId = profilActif?.id ?: -1L

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
                onDefiSerie = { navController.navigate(Routes.CHOIX_DEFI_SERIE) },
                onDefiChrono = { navController.navigate(Routes.CHOIX_DEFI_CHRONO) },
                onDefiQuotidien = { navController.navigate(Routes.CHOIX_DEFI_QUOTIDIEN) },
                onStatistiques = { navController.navigate(Routes.statistiquesJoueur(profilId)) },
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
                titre = stringResource(R.string.apropos_bouton_trophees),
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
                titre = stringResource(R.string.mes_trophees_titre),
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
            StatistiquesJoueurScreen(
                profilId = profilIdArg,
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
                    onMancheTerminee = { obtenu, motValide, _ -> entrainementVm.enregistrerMancheLettres(niveau, obtenu, motValide) },
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
                val partieVm = partieViewModel(navController, backStackEntry)
                ConfigurationPartieScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    onDemarrer = { sequence ->
                        partieVm.demarrer(sequence)
                        navController.navigate(Routes.JEU_PARTIE)
                    },
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
                                    LettresRoundViewModel(manche.niveau, dictionnaire, configurationAlphabet, manche.niveau.dureeSecondesPartieStructuree)
                                }
                            LettresRoundScreen(
                                viewModel = roundVm,
                                scoreCumule = scoreCumule,
                                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                                onMancheTerminee = { obtenu, motValide, _ ->
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
                        // Navigation à l'intérieur de la coroutine, après l'enregistrement (et non
                        // juste après son lancement) : un popBackStack immédiat dispose la
                        // composition et annule ce rememberCoroutineScope avant que l'écriture en
                        // base n'ait eu lieu, perdant silencieusement la partie et ses trophées.
                        scope.launch {
                            historiqueRepository.enregistrerSession(profilId, TypePartie.STRUCTUREE, resultats)
                            tropheeRepository.reevaluer(profilId)
                            navController.popBackStack(Routes.MENU, inclusive = false)
                        }
                    },
                    onRetour = { navController.popBackStack() },
                )
            }
        }

        navigation(startDestination = Routes.CONFIGURATION_PARTIE_DUO, route = Routes.PARTIE_DUO_GRAPH) {
            composable(Routes.CONFIGURATION_PARTIE_DUO) { backStackEntry ->
                val duoVm = partieDuoViewModel(navController, backStackEntry)
                ConfigurationPartieDuoScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    autresProfils = profils.filter { it.id != profilId },
                    onDemarrer = { profil2Id, sequence, mode ->
                        duoVm.demarrer(profil2Id, sequence, mode)
                        navController.navigate(Routes.JEU_PARTIE_DUO)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.JEU_PARTIE_DUO) { backStackEntry ->
                val duoVm = partieDuoViewModel(navController, backStackEntry)
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
                                afficherResultat = false,
                                onMancheTerminee = { obtenu ->
                                    val detail = roundVm.uiState.value.operationsEffectuees
                                        .joinToString("\n").ifBlank { "Aucune opération" }
                                    duoVm.enregistrerResultat(
                                        ResultatDuoManche(
                                            ResultatManche(ModeJeu.CHIFFRES, manche.niveau.name, obtenu),
                                            roundVm.uiState.value.ecartCible,
                                            detail,
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
                                afficherResultat = false,
                                onMancheTerminee = { obtenu, motValide, _ ->
                                    duoVm.enregistrerResultat(
                                        ResultatDuoManche(
                                            ResultatManche(ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide),
                                            detail = motValide ?: "(aucun mot)",
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
                val duoVm = partieDuoViewModel(navController, backStackEntry)
                val profil2 = profils.find { it.id == duoVm.profil2Id }
                val scope = rememberCoroutineScope()
                val (resultats1, resultats2) = duoVm.resultatsFinaux()
                RecapPartieDuoScreen(
                    pseudo1 = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "Joueur 1",
                    pseudo2 = profil2?.let { "${it.avatar} ${it.pseudo}" } ?: "Joueur 2",
                    resultats1 = resultats1,
                    resultats2 = resultats2,
                    onTerminer = {
                        val total1 = resultats1.sumOf { it.score }
                        val total2 = resultats2.sumOf { it.score }
                        val type = duoVm.mode.versTypePartie()
                        // Une égalité compte comme gagnée pour les deux joueurs, pas comme une
                        // défaite mutuelle (retour utilisateur).
                        scope.launch {
                            historiqueRepository.enregistrerSession(profilId, type, resultats1, total1 >= total2)
                            if (profil2 != null) {
                                historiqueRepository.enregistrerSession(profil2.id, type, resultats2, total2 >= total1)
                            }
                            tropheeRepository.reevaluer(profilId)
                            if (profil2 != null) tropheeRepository.reevaluer(profil2.id)
                            navController.popBackStack(Routes.MENU, inclusive = false)
                        }
                    },
                    onRetour = { navController.popBackStack() },
                )
            }
        }

        navigation(startDestination = Routes.CHOIX_ROLE_RESEAU, route = Routes.RESEAU_GRAPH) {
            composable(Routes.CHOIX_ROLE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
                )
                ChoixRoleReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                    onHeberger = { transport ->
                        reseauVm.choisirHote(transport)
                        navController.navigate(Routes.HOTE_ATTENTE_RESEAU)
                    },
                    onRejoindre = { transport ->
                        reseauVm.choisirInvite(transport)
                        navController.navigate(Routes.INVITE_RECHERCHE_RESEAU)
                    },
                    onRetour = { navController.popBackStack() },
                )
            }

            composable(Routes.HOTE_ATTENTE_RESEAU) { backStackEntry ->
                val reseauVm = partieReseauViewModel(
                    navController, backStackEntry, context,
                    pseudo = profilActif?.pseudo ?: "",
                    avatar = profilActif?.avatar ?: "",
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
                )
                ConfigurationPartieReseauScreen(
                    pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
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
                        val (pseudoJ1, pseudoJ2) = if (reseauVm.monTourDuo == TourDuo.JOUEUR1) {
                            pseudoMoi to pseudoAdversaire
                        } else {
                            pseudoAdversaire to pseudoMoi
                        }
                        val resultatsAffiches = listOf(
                            ResultatAffichage(
                                pseudoJ1,
                                scoreAffiche(r1.resultat.score, vainqueur == VainqueurManche.JOUEUR2),
                                r1.detail,
                                vainqueur == VainqueurManche.JOUEUR1,
                            ),
                            ResultatAffichage(
                                pseudoJ2,
                                scoreAffiche(r2.resultat.score, vainqueur == VainqueurManche.JOUEUR1),
                                r2.detail,
                                vainqueur == VainqueurManche.JOUEUR2,
                            ),
                        )
                        val (scoreFinal1, scoreFinal2) = reseauVm.resultatsFinaux()
                        val (scoreMoi, scoreAdv) = if (reseauVm.monTourDuo == TourDuo.JOUEUR1) {
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
                                    onMancheTerminee = { obtenu ->
                                        val detail = roundVm.uiState.value.operationsEffectuees
                                            .joinToString("\n").ifBlank { "Aucune opération" }
                                        reseauVm.enregistrerMonResultat(
                                            ResultatDuoManche(
                                                ResultatManche(ModeJeu.CHIFFRES, manche.niveau.name, obtenu),
                                                roundVm.uiState.value.ecartCible,
                                                detail,
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
                                onMancheTerminee = { obtenu, motValide, _ ->
                                    reseauVm.enregistrerMonResultat(
                                        ResultatDuoManche(
                                            ResultatManche(ModeJeu.LETTRES, manche.niveau.name, obtenu, motValide),
                                            detail = motValide ?: "(aucun mot)",
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
                )
                val etatConnexion by reseauVm.etat.collectAsState()
                val profilDistant = (etatConnexion as? EtatPartieReseau.Connecte)?.profilDistant
                val scope = rememberCoroutineScope()
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
                    onTerminer = {
                        val monTotal = mesResultats.sumOf { it.score }
                        val sonTotal = resultatsAdversaire.sumOf { it.score }
                        val type = reseauVm.mode.versTypePartieReseau()
                        // Une égalité compte comme gagnée (retour utilisateur, même règle que le duo local).
                        scope.launch {
                            historiqueRepository.enregistrerSession(profilId, type, mesResultats, monTotal >= sonTotal)
                            tropheeRepository.reevaluer(profilId)
                            reseauVm.annulerEtRevenirAuChoix()
                            navController.popBackStack(Routes.MENU, inclusive = false)
                        }
                    },
                )
            }
        }

        // Deux entrées distinctes depuis l'accueil (retour utilisateur : "Défi série" et "Défi
        // chrono" sont deux boutons séparés, pas des onglets d'un même écran).
        composable(Routes.CHOIX_DEFI_SERIE) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_serie),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                afficherDuree = false,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiLettres(niveau)) },
                onRetour = { navController.popBackStack() },
            )
        }

        composable(Routes.CHOIX_DEFI_CHRONO) {
            ChoixDefiScreen(
                titre = stringResource(R.string.defi_type_chrono),
                pseudoActif = profilActif?.let { "${it.avatar} ${it.pseudo}" } ?: "…",
                afficherDuree = true,
                onNiveauChiffresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoChiffres(niveau)) },
                onNiveauLettresChoisi = { niveau -> navController.navigate(Routes.jeuDefiChronoLettres(niveau)) },
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
                        DefiQuotidienWidgetProvider.demanderMiseAJour(context)
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
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_continuer)) }
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
                        DefiQuotidienWidgetProvider.demanderMiseAJour(context)
                    }
                }
            }
            // Clé sur essaiId (jamais réutilisé), pas index : cf. commentaire équivalent
            // sur le défi chiffres.
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-lettres-$essaiId") {
                    LettresRoundViewModel(niveau, dictionnaire, configurationAlphabet, niveau.dureeSecondesPartieStructuree)
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                progressionManche = "$index",
                libelleProgression = "Série",
                onMancheTerminee = { _, motValide, meilleurMot ->
                    val reussi = motValide != null && motEstReussiDefiLettres(niveau, motValide, seuil, meilleurMot)
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
                        Button(onClick = { defiVm.mancheSuivante() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.action_continuer)) }
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
                        DefiQuotidienWidgetProvider.demanderMiseAJour(context)
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
                            Text(stringResource(R.string.action_continuer))
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
                        DefiQuotidienWidgetProvider.demanderMiseAJour(context)
                    }
                }
            }
            val roundVm: LettresRoundViewModel =
                viewModel(key = "defi-chrono-lettres-$essaiId") {
                    LettresRoundViewModel(niveau, dictionnaire, configurationAlphabet, defiVm.dureeProchaineManche())
                }
            LettresRoundScreen(
                viewModel = roundVm,
                scoreCumule = null,
                pseudo = profilActif?.let { "${it.avatar} ${it.pseudo}" },
                progressionManche = "$reussites",
                libelleProgression = "Réussites",
                // Comme en défi série : un échec avance seul (pas de confirmation), une
                // réussite attend le "Continuer" du joueur avant d'enchaîner.
                onMancheTerminee = { _, motValide, meilleurMot ->
                    val reussi = motValide != null && motEstReussiDefiLettres(niveau, motValide, seuil, meilleurMot)
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
                            Text(stringResource(R.string.action_continuer))
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
