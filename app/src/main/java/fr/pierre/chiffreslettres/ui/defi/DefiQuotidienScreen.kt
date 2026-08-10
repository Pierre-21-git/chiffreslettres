package fr.pierre.chiffreslettres.ui.defi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.TirageDefiQuotidien
import fr.pierre.chiffreslettres.data.TypeDefi
import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau
import fr.pierre.chiffreslettres.ui.apropos.LienReglesDuJeu
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiChrono
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiMots
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiQuotidien
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiSansFaute
import fr.pierre.chiffreslettres.ui.apropos.ReglesModeDefiSerie
import fr.pierre.chiffreslettres.ui.theme.EnTeteEcran
import fr.pierre.chiffreslettres.ui.theme.PucePseudo
import fr.pierre.chiffreslettres.ui.theme.libelle

@Composable
fun DefiQuotidienScreen(
    pseudoActif: String,
    tirage: TirageDefiQuotidien,
    dejaReussiAujourdhui: Boolean,
    serieActuelle: Int,
    onNiveauChiffresChoisi: (Niveau) -> Unit,
    onNiveauLettresChoisi: (NiveauLettres) -> Unit,
    onRetour: (() -> Unit)? = null,
    couleurRang: Color? = null,
) {
    val nomMode = stringResource(if (tirage.mode == ModeJeu.CHIFFRES) R.string.mode_chiffres else R.string.mode_lettres)
    val nomType = stringResource(
        when (tirage.type) {
            TypeDefi.SERIE -> R.string.defi_type_serie
            TypeDefi.CHRONO -> R.string.defi_type_chrono
            TypeDefi.MOTS_MAX -> R.string.defi_type_mots_max
            TypeDefi.SANS_FAUTE -> R.string.defi_type_sans_faute
        },
    )
    val natureObjectif = when {
        tirage.mode == ModeJeu.CHIFFRES && tirage.type == TypeDefi.SERIE -> stringResource(R.string.defi_objectif_comptes_serie, tirage.objectif)
        tirage.mode == ModeJeu.CHIFFRES -> stringResource(R.string.defi_objectif_comptes, tirage.objectif)
        tirage.type == TypeDefi.SERIE -> stringResource(R.string.defi_objectif_mots_serie, tirage.objectif)
        else -> stringResource(R.string.defi_objectif_mots, tirage.objectif)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnTeteEcran(stringResource(R.string.defi_quotidien_titre), onRetour)
        PucePseudo(pseudoActif, couleurRang = couleurRang)
        LienReglesDuJeu {
            ReglesModeDefiQuotidien()
            when (tirage.type) {
                TypeDefi.SERIE -> ReglesModeDefiSerie()
                TypeDefi.CHRONO -> ReglesModeDefiChrono()
                TypeDefi.MOTS_MAX -> ReglesModeDefiMots()
                TypeDefi.SANS_FAUTE -> ReglesModeDefiSansFaute()
            }
        }

        Text(stringResource(R.string.defi_quotidien_aujourdhui, nomType, nomMode), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.defi_objectif, natureObjectif), style = MaterialTheme.typography.bodyMedium)

        Text(
            if (serieActuelle == 0) {
                stringResource(R.string.defi_quotidien_aucune_serie)
            } else {
                stringResource(R.string.defi_quotidien_serie_en_cours, serieActuelle)
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        if (dejaReussiAujourdhui) {
            Text(
                stringResource(R.string.defi_quotidien_deja_reussi),
                style = MaterialTheme.typography.bodyMedium,
            )
        } else if (tirage.mode == ModeJeu.CHIFFRES) {
            Text(stringResource(R.string.defi_quotidien_choisis_niveau), style = MaterialTheme.typography.titleMedium)
            for (niveau in Niveau.entries) {
                Button(onClick = { onNiveauChiffresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.libelle())
                }
            }
        } else {
            Text(stringResource(R.string.defi_quotidien_choisis_niveau), style = MaterialTheme.typography.titleMedium)
            for (niveau in NiveauLettres.entries) {
                Button(onClick = { onNiveauLettresChoisi(niveau) }, modifier = Modifier.fillMaxWidth()) {
                    Text(niveau.libelle())
                }
            }
        }
    }
}
