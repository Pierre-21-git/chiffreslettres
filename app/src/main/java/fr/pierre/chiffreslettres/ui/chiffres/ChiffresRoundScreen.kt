package fr.pierre.chiffreslettres.ui.chiffres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.numbers.Operation
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonOperateur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuileJeton
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

/** 6 jetons tirés par défaut = 5 combinaisons possibles au maximum pour les réduire à 1. */
private const val MAX_OPERATIONS = 5

@Composable
fun ChiffresRoundScreen(
    viewModel: ChiffresRoundViewModel,
    scoreCumule: Int?,
    onMancheTerminee: (score: Int) -> Unit,
    actionsFinManche: @Composable () -> Unit,
    onRetourEntrainement: (() -> Unit)? = null,
    /** "2 / 4" par exemple, uniquement en partie structurée (retour utilisateur). */
    progressionManche: String? = null,
) {
    val etat by viewModel.uiState.collectAsState()

    LaunchedEffect(etat.termine) {
        if (etat.termine) onMancheTerminee(etat.scoreObtenu ?: 0)
    }

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Chiffres", onRetourEntrainement)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (scoreCumule != null) {
                Afficheur("Score", "$scoreCumule", modifier = Modifier.weight(1f), centre = true)
            }
            if (progressionManche != null) {
                Afficheur("Manche", progressionManche, modifier = Modifier.weight(1f), centre = true)
            }
            etat.tempsRestantSecondes?.let {
                Afficheur("Temps", "${it}s", modifier = Modifier.weight(1f), centre = true)
            }
        }

        Afficheur("Compte à trouver", "${etat.cible}", modifier = Modifier.fillMaxWidth(), grand = true)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (jeton in etat.jetons) {
                val selectionne = jeton.id == etat.premierSelectionne?.id
                TuileJeton(
                    texte = "${jeton.expression.resultat}",
                    selectionne = selectionne,
                    enabled = !etat.termine,
                    onClick = { viewModel.cliquerJeton(jeton) },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        ) {
            for (operation in listOf(Operation.PLUS, Operation.MOINS, Operation.FOIS, Operation.DIVISE)) {
                val autorisee = operation in etat.niveau.operations
                BoutonOperateur(
                    symbole = operation.symbole,
                    selectionne = operation == etat.operateurSelectionne,
                    enabled = autorisee && !etat.termine,
                    onClick = { viewModel.cliquerOperateur(operation) },
                )
            }
        }

        // Cadre affiché dès le début de la manche, hauteur fixe pour 5 opérations (6 jetons
        // tirés = 5 combinaisons possibles au maximum) : sa position ne doit pas bouger au
        // fil des opérations effectuées (retour utilisateur).
        PanneauResultat {
            Text("Vos opérations", color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
            for (index in 0 until MAX_OPERATIONS) {
                Text(
                    etat.operationsEffectuees.getOrNull(index) ?: "",
                    color = Ivory,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BoutonSecondaireContour(
                "Annuler",
                onClick = { viewModel.annulerDerniereOperation() },
                enabled = !etat.termine,
                modifier = Modifier.weight(1f),
            )
            BoutonSecondaireContour(
                "Effacer",
                onClick = { viewModel.effacerCalcul() },
                enabled = !etat.termine,
                modifier = Modifier.weight(1f),
            )
        }
        TuilePrincipale("Valider", onClick = { viewModel.valider() }, enabled = !etat.termine)

        if (etat.termine) {
            PanneauResultat {
                Text("Score obtenu : ${etat.scoreObtenu}", color = BrassBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                val etapesSolution = etat.solutionSolveur?.etapes().orEmpty()
                if (etapesSolution.isEmpty()) {
                    Text(
                        "Une solution possible : ${etat.solutionSolveur?.texte() ?: "aucune"}",
                        color = TextMuted,
                        fontSize = 13.sp,
                    )
                } else {
                    Text("Une solution possible", color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    for (ligne in etapesSolution) {
                        Text(ligne, color = Ivory, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    }
                }
            }
            actionsFinManche()
        }
    }
}
