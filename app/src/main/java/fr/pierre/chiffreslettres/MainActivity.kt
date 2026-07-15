package fr.pierre.chiffreslettres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.pierre.chiffreslettres.data.AppDatabaseProvider
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ProfilActifStore
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.dictionary.DictionnaireProvider
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import fr.pierre.chiffreslettres.ui.navigation.AppNavHost
import fr.pierre.chiffreslettres.ui.profil.CreerProfilScreen
import fr.pierre.chiffreslettres.ui.theme.ChiffresLettresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChiffresLettresTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ContenuApplication(Modifier.fillMaxSize().systemBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun ContenuApplication(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var dictionnaire by remember { mutableStateOf<DictionnaireIndex?>(null) }

    LaunchedEffect(Unit) {
        dictionnaire = DictionnaireProvider.obtenir(context.applicationContext)
    }

    val db = remember { AppDatabaseProvider.obtenir(context.applicationContext) }
    val profilRepository = remember { ProfilRepository(db.profilDao()) }
    val historiqueRepository = remember { HistoriqueRepository(db.historiqueDao()) }
    val profilActifStore = remember { ProfilActifStore(context.applicationContext) }

    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    val dictionnaireCharge = dictionnaire

    when {
        dictionnaireCharge == null -> {
            Box(modifier, contentAlignment = Alignment.Center) {
                Text("Chargement...")
            }
        }
        profils.isEmpty() -> {
            // Premier lancement (§7.1) : la liste des profils se recompose
            // automatiquement dès la création, on bascule alors vers AppNavHost.
            CreerProfilScreen(
                profilRepository = profilRepository,
                profilActifStore = profilActifStore,
                premierLancement = true,
                onProfilCree = {},
                modifier = modifier,
            )
        }
        else -> {
            AppNavHost(
                dictionnaire = dictionnaireCharge,
                profilRepository = profilRepository,
                historiqueRepository = historiqueRepository,
                profilActifStore = profilActifStore,
                modifier = modifier,
            )
        }
    }
}
