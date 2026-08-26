package fr.pierre.chiffreslettres.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.Palier
import fr.pierre.chiffreslettres.data.TropheeRepository

/** Couleur associée à un palier de trophée/rang joueur. */
fun couleurPalier(palier: Palier): Color = when (palier) {
    Palier.BRONZE -> PalierBronze
    Palier.ARGENT -> PalierArgent
    Palier.OR -> BrassBright
    Palier.PLATINE -> PalierPlatine
    Palier.EMERAUDE -> PalierEmeraude
    Palier.SAPHIR -> PalierSaphir
    Palier.RUBIS -> PalierRubis
    Palier.DIAMANT -> PalierDiamant
}

/** Couleur de la tuile d'un easter egg (retour utilisateur, `Trophee.palier == null`) : hors échelle Bronze→Diamant. */
val PalierEasterEgg: Color get() = BrassBright

/**
 * Couleur du rang joueur d'un profil (retour utilisateur : cadre du profil de la couleur du
 * rang), null si même le premier palier (bronze) n'est pas complet.
 */
@Composable
fun couleurRangJoueur(profilId: Long, tropheeRepository: TropheeRepository): Color? {
    val debloques by tropheeRepository.tropheesDebloques(profilId).collectAsState(initial = emptyList())
    val rang = remember(debloques) { CatalogueTrophees.rangJoueur(debloques.map { it.trophyId }.toSet()) }
    return rang?.let { couleurPalier(it) }
}
