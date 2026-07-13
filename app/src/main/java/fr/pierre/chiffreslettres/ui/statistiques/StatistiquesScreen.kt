package fr.pierre.chiffreslettres.ui.statistiques

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.data.HistoriqueRepository
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

@Composable
fun StatistiquesScreen(
    profilRepository: ProfilRepository,
    historiqueRepository: HistoriqueRepository,
) {
    var mode by remember { mutableStateOf(ModeJeu.CHIFFRES) }
    var niveauChiffres by remember { mutableStateOf(Niveau.entries.first()) }
    var niveauLettres by remember { mutableStateOf(NiveauLettres.entries.first()) }
    val niveauCode = if (mode == ModeJeu.CHIFFRES) niveauChiffres.name else niveauLettres.name
    val niveauLabel = if (mode == ModeJeu.CHIFFRES) niveauChiffres.label else niveauLettres.label

    val classementFlow = remember(mode, niveauCode) { historiqueRepository.classementParNiveau(mode, niveauCode) }
    val classement by classementFlow.collectAsState(initial = emptyList())

    val profils by profilRepository.tousLesProfils().collectAsState(initial = emptyList())
    var profilSelectionne by remember { mutableStateOf<ProfilEntity?>(null) }
    val profilActif = profilSelectionne ?: profils.firstOrNull()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Statistiques", style = MaterialTheme.typography.headlineSmall)

        Text("Classement par niveau", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (mode == ModeJeu.CHIFFRES) {
                Button(onClick = {}) { Text("Chiffres") }
                OutlinedButton(onClick = { mode = ModeJeu.LETTRES }) { Text("Lettres") }
            } else {
                OutlinedButton(onClick = { mode = ModeJeu.CHIFFRES }) { Text("Chiffres") }
                Button(onClick = {}) { Text("Lettres") }
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (mode == ModeJeu.CHIFFRES) {
                for (niveau in Niveau.entries) {
                    if (niveau == niveauChiffres) {
                        Button(onClick = { niveauChiffres = niveau }) { Text(niveau.label) }
                    } else {
                        OutlinedButton(onClick = { niveauChiffres = niveau }) { Text(niveau.label) }
                    }
                }
            } else {
                for (niveau in NiveauLettres.entries) {
                    if (niveau == niveauLettres) {
                        Button(onClick = { niveauLettres = niveau }) { Text(niveau.label) }
                    } else {
                        OutlinedButton(onClick = { niveauLettres = niveau }) { Text(niveau.label) }
                    }
                }
            }
        }
        if (classement.isEmpty()) {
            Text("Aucun score enregistré pour $niveauLabel.")
        } else {
            for ((position, ligne) in classement.withIndex()) {
                Text("${position + 1}. ${ligne.pseudo} — ${ligne.meilleurScore} points")
            }
        }

        Text("Stats par joueur", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (profil in profils) {
                if (profil.id == profilActif?.id) {
                    Button(onClick = { profilSelectionne = profil }) { Text(profil.pseudo) }
                } else {
                    OutlinedButton(onClick = { profilSelectionne = profil }) { Text(profil.pseudo) }
                }
            }
        }

        if (profilActif != null) {
            val plusLongMotFlow = remember(profilActif.id) { historiqueRepository.plusLongMot(profilActif.id) }
            val plusLongMot by plusLongMotFlow.collectAsState(initial = null)
            val meilleurScoreFlow = remember(profilActif.id) { historiqueRepository.meilleurScorePartieStructuree(profilActif.id) }
            val meilleurScore by meilleurScoreFlow.collectAsState(initial = null)

            Text("Plus long mot trouvé : ${plusLongMot?.motJoue ?: "aucun"}")
            Text("Meilleur score en partie structurée : ${meilleurScore ?: "aucun"}")
        }
    }
}
