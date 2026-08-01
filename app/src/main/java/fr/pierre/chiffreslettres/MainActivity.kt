package fr.pierre.chiffreslettres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fr.pierre.chiffreslettres.data.AppDatabaseProvider
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.DefiRepository
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.TropheeRepository
import fr.pierre.chiffreslettres.data.alphabet.ConfigurationAlphabetProvider
import fr.pierre.chiffreslettres.data.dictionary.DictionnaireProvider
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.rappel.creerCanalNotificationRappel
import fr.pierre.chiffreslettres.rappel.planifierRappelQuotidien
import fr.pierre.chiffreslettres.ui.navigation.AppNavHost
import fr.pierre.chiffreslettres.ui.profil.ChangerProfilScreen
import fr.pierre.chiffreslettres.ui.profil.CreerProfilScreen
import fr.pierre.chiffreslettres.ui.theme.ChiffresLettresTheme
import fr.pierre.chiffreslettres.widget.planifierRafraichissementWidgetMinuit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        creerCanalNotificationRappel(this)
        planifierRappelQuotidien(this)
        planifierRafraichissementWidgetMinuit(this)
        setContent {
            ChiffresLettresTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ContenuApplication(Modifier.fillMaxSize().systemBarsPadding())
                }
            }
        }
    }
}

/** Étapes du "gate" de sélection de profil affiché à chaque lancement de l'app (retour utilisateur, cloisonnement des profils). */
private enum class EtapeGateProfil { SELECTION, CREATION, CONFIRME }

@Composable
private fun ContenuApplication(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var dictionnaire by remember { mutableStateOf<DictionnaireIndex?>(null) }

    // Demande la permission de notification une fois au lancement (retour utilisateur : sans
    // elle, le rappel de défi quotidien planifié ci-dessous ne peut rien afficher).
    val lanceurPermissionNotification =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    LaunchedEffect(Unit) {
        lanceurPermissionNotification.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    val configurationAlphabet = remember { ConfigurationAlphabetProvider.charger(context.applicationContext) }
    val db = remember { AppDatabaseProvider.obtenir(context.applicationContext) }
    val profilRepository = remember { ProfilRepository(db.profilDao()) }
    val historiqueRepository = remember { HistoriqueRepository(db.historiqueDao()) }
    val defiRepository = remember { DefiRepository(db.defiDao()) }
    val tropheeRepository = remember { TropheeRepository(db.tropheeDao(), db.historiqueDao(), db.defiDao(), db.defiQuotidienDao()) }
    val defiQuotidienRepository = remember { DefiQuotidienRepository(db.defiQuotidienDao()) }
    val profilActifStore = remember { ProfilActifStore(context.applicationContext) }

    // null = pas encore chargé (distinct d'une vraie liste vide, cf. plus bas) : un simple
    // `initial = emptyList()` ferait passer par l'écran "premier lancement" à chaque
    // recréation d'Activity (changement de langue de profil, cf. AppNavHost) le temps que ce
    // flow Room réémette la vraie liste — bug remonté par l'utilisateur (flash de l'écran de
    // création de profil, avec son sélecteur d'avatars).
    val profils = profilRepository.tousLesProfils().collectAsState(initial = null).value
    // Dictionnaire selon la langue du profil actif (retour utilisateur) : rechargé si le profil
    // change de langue ou si un autre profil (langue différente) devient actif.
    val profilActifIdStore by profilActifStore.profilActifId.collectAsState(initial = null)
    val profilActifPourLangue = profils?.find { it.id == profilActifIdStore } ?: profils?.firstOrNull()
    LaunchedEffect(profilActifPourLangue?.langue) {
        dictionnaire = DictionnaireProvider.obtenir(context.applicationContext, profilActifPourLangue?.langue ?: "fr")
    }
    val dictionnaireCharge = dictionnaire
    // Non persisté d'un lancement à l'autre : redemande confirmation à chaque lancement de
    // l'app (retour utilisateur, cloisonnement des profils). rememberSaveable (et non simple
    // remember) pour survivre à la recréation d'Activity déclenchée par un changement de
    // langue de profil (LocaleManager.applicationLocales, cf. AppNavHost) — sans quoi ce gate
    // repassait à SELECTION en pleine confirmation et forçait un second clic sur le profil
    // (bug remonté par l'utilisateur).
    var etapeGate by rememberSaveable { mutableStateOf(EtapeGateProfil.SELECTION) }

    when {
        dictionnaireCharge == null || profils == null -> {
            Box(modifier, contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.chargement))
            }
        }
        profils.isEmpty() -> {
            // Premier lancement (§7.1) : la liste des profils se recompose
            // automatiquement dès la création, on bascule alors vers AppNavHost.
            CreerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                premierLancement = true,
                onProfilCree = { etapeGate = EtapeGateProfil.CONFIRME },
                modifier = modifier,
            )
        }
        etapeGate == EtapeGateProfil.CREATION -> {
            CreerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                premierLancement = false,
                onProfilCree = { etapeGate = EtapeGateProfil.CONFIRME },
                onRetour = { etapeGate = EtapeGateProfil.SELECTION },
                modifier = modifier,
            )
        }
        // Un seul profil : rien à choisir, la sélection n'apporte rien (retour utilisateur).
        etapeGate == EtapeGateProfil.SELECTION && profils.size > 1 -> {
            ChangerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                tropheeRepository = tropheeRepository,
                onProfilChoisi = { etapeGate = EtapeGateProfil.CONFIRME },
                onCreerNouveauProfil = { etapeGate = EtapeGateProfil.CREATION },
                modifier = modifier,
            )
        }
        else -> {
            AppNavHost(
                dictionnaire = dictionnaireCharge,
                configurationAlphabet = configurationAlphabet,
                profilRepository = profilRepository,
                historiqueRepository = historiqueRepository,
                defiRepository = defiRepository,
                tropheeRepository = tropheeRepository,
                defiQuotidienRepository = defiQuotidienRepository,
                profilActifStore = profilActifStore,
                modifier = modifier,
            )
        }
    }
}
