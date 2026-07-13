package fr.pierre.chiffreslettres.ui.partie

import fr.pierre.chiffreslettres.letters.NiveauLettres
import fr.pierre.chiffreslettres.numbers.Niveau

/** Une manche prévue dans la séquence d'une partie structurée (spec §6.2). */
sealed class ManchePlanifiee {
    data class Chiffres(val niveau: Niveau) : ManchePlanifiee()
    data class Lettres(val niveau: NiveauLettres) : ManchePlanifiee()
}
