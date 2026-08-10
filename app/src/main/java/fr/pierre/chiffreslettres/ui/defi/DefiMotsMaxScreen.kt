package fr.pierre.chiffreslettres.ui.defi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.TirageLettres
import fr.pierre.chiffreslettres.ui.theme.Afficheur
import fr.pierre.chiffreslettres.ui.theme.BoutonSecondaireContour
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.GrilleMotsGroupee
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanneauResultat
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.TextMuted
import fr.pierre.chiffreslettres.ui.theme.TuileJeton
import fr.pierre.chiffreslettres.ui.theme.TuilePrincipale
import fr.pierre.chiffreslettres.ui.theme.fondPlateau

private const val LETTRES_PAR_LIGNE = 5
private const val COLONNES_MOTS = 3
/** 3 colonnes × 4 lignes = 12 emplacements, pour l'objectif de 10 mots des trophées Platine/Diamant (retour utilisateur). */
private const val LIGNES_MOTS_TROUVES = 4

/**
 * Défi mots max (retour utilisateur) : contrairement à `LettresRoundScreen`, un seul écran
 * couvre tout le défi (pas de chaînage manche par manche) — [actionsFin] ne fournit que les
 * boutons "Recommencer"/"Retour" une fois [DefiMotsMaxUiState.termine], le reste (tirage,
 * saisie, décompte des mots) est géré ici en continu sur le même tirage.
 */
@Composable
fun DefiMotsMaxScreen(
    viewModel: DefiMotsMaxViewModel,
    pseudo: String?,
    couleurRang: Color?,
    onRetour: (() -> Unit)?,
    actionsFin: @Composable () -> Unit,
) {
    val etat by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().fondPlateau().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.defi_type_mots_max), onRetour)
        if (pseudo != null) {
            PucePseudo(pseudo, couleurRang = couleurRang)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Afficheur(stringResource(R.string.afficheur_mots_trouves), "${etat.motsTrouves.size}", modifier = Modifier.weight(1f), centre = true)
            Afficheur(
                label = if (!etat.tirageTermine) stringResource(R.string.lettres_tirage_label) else stringResource(R.string.afficheur_temps),
                valeur = if (!etat.tirageTermine) {
                    stringResource(R.string.lettres_tirage_valeur, etat.lettresTirees.size, etat.nombreLettres)
                } else {
                    stringResource(R.string.afficheur_temps_valeur, etat.tempsRestantSecondes)
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

        Afficheur(
            stringResource(R.string.lettres_votre_mot),
            etat.motSaisi.ifEmpty { stringResource(R.string.attente_hote_valeur_provisoire) },
            modifier = Modifier.fillMaxWidth(),
        )

        val motRejete = etat.motRejeteTransitoire
        if (motRejete != null && !etat.termine) {
            val message = when (etat.raisonRejetTransitoire) {
                RaisonRejetMotDefiMotsMax.DEJA_TROUVE ->
                    stringResource(R.string.defi_mots_max_deja_trouve, motRejete)
                RaisonRejetMotDefiMotsMax.INVALIDE ->
                    stringResource(R.string.defi_mots_max_fin_mot_invalide, motRejete)
                RaisonRejetMotDefiMotsMax.TROP_COURT ->
                    stringResource(R.string.defi_mots_max_fin_mot_trop_court, motRejete, seuilLongueurDefiLettres(etat.niveau))
                null -> null
            }
            if (message != null) {
                Text(message, color = TextMuted, fontSize = 13.sp)
            }
        }

        if (!etat.tirageTermine) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.lettres_nombre_voyelles), color = TextMuted, fontSize = 13.sp)
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
                    stringResource(R.string.action_annuler),
                    onClick = { viewModel.annulerLettre() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
                BoutonSecondaireContour(
                    stringResource(R.string.action_effacer),
                    onClick = { viewModel.effacerMot() },
                    enabled = !etat.termine && etat.indicesUtilises.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                )
            }
            TuilePrincipale(stringResource(R.string.action_valider), onClick = { viewModel.valider() }, enabled = !etat.termine)
        }

        if (etat.motsTrouves.isNotEmpty()) {
            PanneauResultat {
                // Emplacements réservés pour 4 lignes (retour utilisateur), pas seulement les
                // mots déjà trouvés : la grille garde une taille stable pendant la recherche,
                // pour l'objectif de 10 mots des trophées Platine/Diamant.
                GrilleMots(mots = etat.motsTrouves, lignesReservees = LIGNES_MOTS_TROUVES) { mot ->
                    Text(mot, color = TextMuted, fontSize = 13.sp)
                }
            }
        }

        if (etat.termine) {
            PanneauResultat {
                Text(
                    stringResource(R.string.defi_mots_max_recap_score, etat.motsTrouves.size),
                    color = BrassBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                val explication = when (etat.raisonFin) {
                    RaisonFinDefiMotsMax.TEMPS_ECOULE ->
                        stringResource(R.string.defi_mots_max_fin_temps_ecoule)
                    RaisonFinDefiMotsMax.TOUS_MOTS_TROUVES ->
                        stringResource(R.string.defi_mots_max_fin_tous_mots_trouves)
                    RaisonFinDefiMotsMax.VOLONTAIRE, null -> null
                }
                if (explication != null) {
                    Text(explication, color = TextMuted, fontSize = 13.sp)
                }
            }
            if (etat.motsPossibles.isNotEmpty()) {
                PanneauResultat {
                    Text(stringResource(R.string.defi_mots_max_mots_possibles_titre), color = TextMuted, fontSize = 11.sp, letterSpacing = 1.sp)
                    GrilleMotsGroupee(mots = etat.motsPossibles) { mot ->
                        val trouve = mot in etat.motsTrouves
                        Text(
                            if (trouve) stringResource(R.string.defi_mots_max_mot_possible_trouve, mot) else mot,
                            color = if (trouve) TextMuted else Ivory,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
            actionsFin()
        }
    }
}

/**
 * Grille de mots sur [COLONNES_MOTS] colonnes alignées (retour utilisateur) : chaque cellule se
 * partage la largeur à parts égales (`Modifier.weight`), donc les colonnes restent alignées d'une
 * ligne à l'autre quelle que soit la longueur des mots. Si [lignesReservees] est fourni, la
 * grille réserve toujours ce nombre de lignes (cellules vides au-delà de [mots]) pour ne pas
 * changer de taille au fil de la recherche ; sinon elle s'arrête au dernier mot.
 */
@Composable
private fun GrilleMots(mots: List<String>, lignesReservees: Int? = null, rendu: @Composable (String) -> Unit) {
    val nombreLignes = lignesReservees ?: ((mots.size + COLONNES_MOTS - 1) / COLONNES_MOTS)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (ligne in 0 until nombreLignes) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (colonne in 0 until COLONNES_MOTS) {
                    // Colonne par colonne (retour utilisateur), pas ligne par ligne : la 1ère
                    // colonne se remplit entièrement avant la 2e, comme GrilleMotsGroupee.
                    val mot = mots.getOrNull(colonne * nombreLignes + ligne)
                    Box(modifier = Modifier.weight(1f)) {
                        if (mot != null) rendu(mot)
                    }
                }
            }
        }
    }
}
