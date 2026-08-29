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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.numbers.Operation
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonOperateur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
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
    onMancheTerminee: (score: Int, detail: DetailChiffresManche?) -> Unit,
    actionsFinManche: @Composable () -> Unit,
    onRetourEntrainement: (() -> Unit)? = null,
    pseudo: String? = null,
    /** Couleur du rang joueur (bronze/argent/or...) pour le cadre de [pseudo], cf. `PucePseudo`. */
    couleurRang: Color? = null,
    /** "2 / 4" par exemple, uniquement en partie structurée ou en défi (retour utilisateur). */
    progressionManche: String? = null,
    /** Libellé de la pastille [progressionManche] : "Manche" en partie solo, "Série" en défi. */
    libelleProgression: String = stringResource(R.string.libelle_manche),
    /** Faux en mode duo (retour utilisateur) : le score et la solution sont révélés sur l'écran de transition, pas ici — pour ne pas donner d'indice au second joueur avant qu'il ne joue le même tirage. */
    afficherResultat: Boolean = true,
) {
    val etat by viewModel.uiState.collectAsState()

    LaunchedEffect(etat.termine) {
        if (etat.termine) onMancheTerminee(etat.scoreObtenu ?: 0, etat.detailFinal)
    }

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.mode_chiffres), onRetourEntrainement)
        if (pseudo != null) {
            PucePseudo(pseudo, couleurRang = couleurRang)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (scoreCumule != null) {
                Afficheur(stringResource(R.string.afficheur_score), "$scoreCumule", modifier = Modifier.weight(1f), centre = true)
            }
            if (progressionManche != null) {
                Afficheur(libelleProgression, progressionManche, modifier = Modifier.weight(1f), centre = true)
            }
            etat.tempsRestantSecondes?.let {
                Afficheur(stringResource(R.string.afficheur_temps), stringResource(R.string.afficheur_temps_valeur, it), modifier = Modifier.weight(1f), centre = true)
            }
        }

        Afficheur(stringResource(R.string.chiffres_compte_a_trouver), "${etat.cible}", modifier = Modifier.fillMaxWidth(), grand = true)

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
                    grand = true,
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
            Text(stringResource(R.string.chiffres_vos_operations), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
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
                stringResource(R.string.action_annuler),
                onClick = { viewModel.annulerDerniereOperation() },
                enabled = !etat.termine,
                modifier = Modifier.weight(1f),
            )
            BoutonSecondaireContour(
                stringResource(R.string.action_effacer),
                onClick = { viewModel.effacerCalcul() },
                enabled = !etat.termine,
                modifier = Modifier.weight(1f),
            )
        }
        TuilePrincipale(stringResource(R.string.action_valider), onClick = { viewModel.valider() }, enabled = !etat.termine)

        if (etat.termine) {
            if (afficherResultat) {
                PanneauResultat {
                    Text(stringResource(R.string.score_obtenu, etat.scoreObtenu ?: 0), color = BrassBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    val etapesSolution = etat.solutionSolveur?.etapes().orEmpty()
                    if (etapesSolution.isEmpty()) {
                        Text(
                            stringResource(
                                R.string.chiffres_solution_possible_ligne,
                                etat.solutionSolveur?.texte() ?: stringResource(R.string.chiffres_solution_aucune),
                            ),
                            color = TextMuted,
                            fontSize = 13.sp,
                        )
                    } else {
                        Text(stringResource(R.string.chiffres_solution_possible_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                        for (ligne in etapesSolution) {
                            Text(ligne, color = Ivory, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                        }
                    }
                }
            }
            actionsFinManche()
        }
    }
}
