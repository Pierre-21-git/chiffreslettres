package fr.pierre.chiffreslettres.ui.lettres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuileJeton
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

private const val LETTRES_PAR_LIGNE = 5

@Composable
fun LettresRoundScreen(
    viewModel: LettresRoundViewModel,
    scoreCumule: Int?,
    onMancheTerminee: (score: Int, motValide: String?) -> Unit,
    actionsFinManche: @Composable () -> Unit,
    onRetourEntrainement: (() -> Unit)? = null,
    pseudo: String? = null,
    /** "2 / 4" par exemple, uniquement en partie structurée ou en défi (retour utilisateur). */
    progressionManche: String? = null,
    /** Libellé de la pastille [progressionManche] : "Manche" en partie solo, "Série" en défi. */
    libelleProgression: String = "Manche",
    /** Faux en mode duo (retour utilisateur) : le score et le mot sont révélés sur l'écran de transition, pas ici — pour ne pas donner d'indice au second joueur avant qu'il ne joue le même tirage. */
    afficherResultat: Boolean = true,
) {
    val etat by viewModel.uiState.collectAsState()

    LaunchedEffect(etat.termine) {
        if (etat.termine) {
            val motValide = if (etat.motJoueurValide == true) etat.motSaisi else null
            onMancheTerminee(etat.scoreObtenu ?: 0, motValide)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran("Lettres", onRetourEntrainement)
        if (pseudo != null) {
            PucePseudo(pseudo)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (scoreCumule != null) {
                Afficheur("Score", "$scoreCumule", modifier = Modifier.weight(1f), centre = true)
            }
            if (progressionManche != null) {
                Afficheur(libelleProgression, progressionManche, modifier = Modifier.weight(1f), centre = true)
            }
            // Cadre toujours affiché (retour utilisateur), même vide une fois le tirage
            // terminé sans chrono (entraînement libre) : sa position ne doit pas bouger.
            Afficheur(
                label = if (!etat.tirageTermine) "Tirage" else "Temps",
                valeur = when {
                    !etat.tirageTermine -> "${etat.lettresTirees.size} / ${etat.nombreLettres}"
                    etat.tempsRestantSecondes != null -> "${etat.tempsRestantSecondes}s"
                    else -> ""
                },
                modifier = Modifier.weight(1f),
                centre = true,
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = LETTRES_PAR_LIGNE,
        ) {
            // Emplacements réservés pour les nombreLettres tuiles dès le début de la manche
            // (retour utilisateur) : la grille garde toujours sa taille finale, qu'il y ait 0
            // ou 10 lettres tirées, pour que rien en dessous (mot, boutons) ne se déplace au
            // fil du tirage.
            for (index in 0 until etat.nombreLettres) {
                val lettre = etat.lettresTirees.getOrNull(index)
                if (lettre == null) {
                    Spacer(Modifier.size(56.dp, 60.dp))
                } else {
                    val utilisee = index in etat.indicesUtilises
                    TuileJeton(
                        texte = "$lettre",
                        selectionne = false,
                        enabled = etat.tirageTermine && !etat.termine && !utilisee,
                        monospace = false,
                        grand = true,
                        onClick = { viewModel.cliquerLettre(index) },
                    )
                }
            }
        }

        // Cadre affiché dès le début de la manche (retour utilisateur), pas seulement une
        // fois le tirage terminé : sa position reste fixe, seul son contenu apparaît une fois
        // que le joueur compose son mot.
        Afficheur(
            "Votre mot",
            etat.motSaisi.ifEmpty { "…" },
            modifier = Modifier.fillMaxWidth(),
        )

        if (!etat.tirageTermine) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Nombre de voyelles souhaitées", color = TextMuted, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (n in TirageLettres.VOYELLES_MINIMUM..TirageLettres.VOYELLES_MAXIMUM) {
                        TuilePrincipale(
                            "$n",
                            onClick = { viewModel.choisirNombreVoyelles(n) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BoutonSecondaireContour(
                    "Annuler",
                    onClick = { viewModel.annulerLettre() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
                BoutonSecondaireContour(
                    "Effacer",
                    onClick = { viewModel.effacerMot() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
            }
            TuilePrincipale("Valider", onClick = { viewModel.valider() }, enabled = !etat.termine)
        }

        if (etat.termine) {
            if (afficherResultat) {
                val validite = if (etat.motJoueurValide == true) "valide" else "invalide ou absent du dictionnaire"
                PanneauResultat {
                    Text("Score obtenu : ${etat.scoreObtenu}", color = BrassBright, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Votre mot (\"${etat.motSaisi}\") : $validite", color = TextMuted, fontSize = 13.sp)
                    Text(
                        "Meilleur mot trouvé : ${etat.meilleurMot?.let { "$it (${it.length} lettres)" } ?: "aucun"}",
                        color = TextMuted,
                        fontSize = 13.sp,
                    )
                }
            }
            actionsFinManche()
        }
    }
}
