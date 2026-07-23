package fr.pierre.chiffreslettres.letters

import kotlin.random.Random

/**
 * Tirage des lettres (spec §4.1) : le joueur choisit un nombre de voyelles
 * souhaité (entre [VOYELLES_MINIMUM] et [VOYELLES_MAXIMUM]), puis toutes les
 * lettres sont tirées en une fois.
 */
object TirageLettres {
    const val NOMBRE_LETTRES = 10
    const val VOYELLES_MINIMUM = 2
    const val VOYELLES_MAXIMUM = 5

    fun tirer(
        sac: SacLettres,
        nombreVoyelles: Int,
        nombreLettres: Int = NOMBRE_LETTRES,
        random: Random = Random,
    ): List<Char> {
        require(nombreVoyelles in VOYELLES_MINIMUM..VOYELLES_MAXIMUM) {
            "nombreVoyelles doit être compris entre $VOYELLES_MINIMUM et $VOYELLES_MAXIMUM"
        }
        val voyelles = List(nombreVoyelles) { sac.tirerVoyelle(random) }
        val consonnes = List(nombreLettres - nombreVoyelles) { sac.tirerConsonne(random) }
        return (voyelles + consonnes).shuffled(random)
    }
}
