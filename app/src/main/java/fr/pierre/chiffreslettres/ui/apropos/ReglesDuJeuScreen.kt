package fr.pierre.chiffreslettres.ui.apropos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.ui.theme.BrassBright
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.Ivory
import fr.pierre.chiffreslettres.ui.theme.PanelDeep
import fr.pierre.chiffreslettres.ui.theme.TextMuted

@Composable
fun ReglesDuJeuScreen(onRetour: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        EnTeteEcran(stringResource(R.string.regles_titre), onRetour)
        ReglesModeChiffres()
        ReglesModeLettres()
        ReglesModeEntrainement()
        ReglesModePartieSolo()
        ReglesModePartieDuo()
        ReglesModeDefiSerie()
        ReglesModeDefiChrono()
        ReglesModeDefiMots()
        ReglesModeDefiSansFaute()
        ReglesModeDefiQuotidien()
    }
}

/**
 * Sections des règles extraites en composables individuels (retour utilisateur : réutilisées à
 * la fois dans [ReglesDuJeuScreen] et dans le dialogue "Règles du jeu" de chaque écran de choix
 * de niveau, via `ReglesDialog`/`LienReglesDuJeu`).
 */
@Composable
fun ReglesModeChiffres() {
    SectionRegle(stringResource(R.string.regles_mode_chiffres_titre)) {
        Text(stringResource(R.string.regles_mode_chiffres_texte), style = MaterialTheme.typography.bodyMedium)
        SousTitreRegle(stringResource(R.string.regles_comptage_points_titre))
        ListeAPuces(
            stringResource(R.string.regles_chiffres_points_emile_nestor),
            stringResource(R.string.regles_chiffres_points_monique_mathieu),
        )
    }
}

@Composable
fun ReglesModeLettres() {
    SectionRegle(stringResource(R.string.regles_mode_lettres_titre)) {
        Text(stringResource(R.string.regles_mode_lettres_texte), style = MaterialTheme.typography.bodyMedium)
        SousTitreRegle(stringResource(R.string.regles_comptage_points_titre))
        ListeAPuces(
            stringResource(R.string.regles_lettres_points_emile_nestor),
            stringResource(R.string.regles_lettres_points_monique_mathieu),
        )
        SousTitreRegle(stringResource(R.string.regles_mots_acceptes_titre))
        Text(stringResource(R.string.regles_mots_acceptes_texte), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeEntrainement() {
    SectionRegle(stringResource(R.string.regles_mode_entrainement_titre)) {
        Text(stringResource(R.string.regles_mode_entrainement_texte), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModePartieSolo() {
    SectionRegle(stringResource(R.string.regles_mode_partie_solo_titre)) {
        Text(stringResource(R.string.regles_partie_solo_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_partie_solo_emile),
            stringResource(R.string.regles_partie_solo_nestor),
            stringResource(R.string.regles_partie_solo_monique),
            stringResource(R.string.regles_partie_solo_mathieu),
        )
    }
}

@Composable
fun ReglesModePartieDuo() {
    SectionRegle(stringResource(R.string.regles_mode_partie_duo_titre)) {
        Text(stringResource(R.string.regles_partie_duo_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_partie_duo_mode_duo),
            stringResource(R.string.regles_partie_duo_mode_confrontation),
        )
        Text(stringResource(R.string.regles_partie_duo_reseau), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeDefiSerie() {
    SectionRegle(stringResource(R.string.regles_mode_defi_serie_titre)) {
        Text(stringResource(R.string.regles_defi_serie_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_seuil_emile),
            stringResource(R.string.regles_seuil_nestor),
            stringResource(R.string.regles_seuil_monique),
            stringResource(R.string.regles_seuil_mathieu),
        )
        Text(stringResource(R.string.regles_defi_serie_chiffres_note), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeDefiChrono() {
    SectionRegle(stringResource(R.string.regles_mode_defi_chrono_titre)) {
        Text(stringResource(R.string.regles_defi_chrono_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_defi_chrono_budget_emile),
            stringResource(R.string.regles_defi_chrono_budget_nestor),
            stringResource(R.string.regles_defi_chrono_budget_monique),
            stringResource(R.string.regles_defi_chrono_budget_mathieu),
        )
        Text(stringResource(R.string.regles_defi_chrono_seuil_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_seuil_emile),
            stringResource(R.string.regles_seuil_nestor),
            stringResource(R.string.regles_seuil_monique),
            stringResource(R.string.regles_seuil_mathieu),
        )
        Text(stringResource(R.string.regles_defi_chrono_detail), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeDefiMots() {
    SectionRegle(stringResource(R.string.regles_mode_defi_mots_titre)) {
        Text(stringResource(R.string.regles_defi_mots_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_seuil_emile),
            stringResource(R.string.regles_seuil_nestor),
            stringResource(R.string.regles_seuil_monique),
            stringResource(R.string.regles_seuil_mathieu),
        )
        Text(stringResource(R.string.regles_defi_mots_detail), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeDefiSansFaute() {
    SectionRegle(stringResource(R.string.regles_mode_defi_sans_faute_titre)) {
        Text(stringResource(R.string.regles_defi_sans_faute_intro), style = MaterialTheme.typography.bodyMedium)
        ListeAPuces(
            stringResource(R.string.regles_seuil_emile),
            stringResource(R.string.regles_seuil_nestor),
            stringResource(R.string.regles_seuil_monique),
            stringResource(R.string.regles_seuil_mathieu),
        )
        Text(stringResource(R.string.regles_defi_sans_faute_detail), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReglesModeDefiQuotidien() {
    SectionRegle(stringResource(R.string.regles_mode_defi_quotidien_titre)) {
        Text(stringResource(R.string.regles_defi_quotidien_texte1), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.regles_defi_quotidien_texte2), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SousTitreRegle(texte: String) {
    Text(texte, style = MaterialTheme.typography.labelLarge, color = TextMuted)
}

/** Vraie liste à puces (retour utilisateur) : une pastille alignée en tête de chaque ligne, au lieu d'un simple "• " collé au texte. */
@Composable
private fun ListeAPuces(vararg items: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (item in items) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BrassBright),
                )
                Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}

/** Regroupe les règles d'un mode dans un panneau distinct (lisibilité, retour utilisateur). */
@Composable
private fun SectionRegle(titre: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PanelDeep)
            .border(1.dp, Ivory.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(titre, style = MaterialTheme.typography.titleMedium, color = Ivory)
        content()
    }
}
