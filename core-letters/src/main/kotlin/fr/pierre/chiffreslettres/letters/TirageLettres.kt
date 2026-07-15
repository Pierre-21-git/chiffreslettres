package fr.pierre.chiffreslettres.letters

import kotlin.random.Random

/**
 * Orchestre le tirage pas-à-pas des lettres (spec §4.1) : le joueur choisit
 * "Consonne" ou "Voyelle" à chaque tirage, mais l'interface doit forcer une
 * voyelle dès que le nombre de tirages restants ne permet plus d'atteindre le
 * minimum de 2 voyelles.
 */
object TirageLettres {
    const val NOMBRE_LETTRES = 10
    const val VOYELLES_MINIMUM = 2

    /** Le bouton "Consonne" doit-il rester actif compte tenu des lettres déjà tirées ? */
    fun consonneAutorisee(dejaTirees: List<Char>, nombreLettres: Int = NOMBRE_LETTRES): Boolean {
        val voyellesTirees = dejaTirees.count { it in SacLettres.VOYELLES }
        val voyellesNecessaires = (VOYELLES_MINIMUM - voyellesTirees).coerceAtLeast(0)
        val lettresRestantes = nombreLettres - dejaTirees.size
        return lettresRestantes > voyellesNecessaires
    }

    /**
     * Tire la prochaine lettre. Si [consonneDemandee] est vrai mais que la règle du
     * minimum 2 voyelles l'interdit, une voyelle est tirée à la place (l'UI est
     * censée avoir désactivé le bouton, ceci est un garde-fou côté logique).
     */
    fun tirerProchaineLettre(
        sac: SacLettres,
        dejaTirees: List<Char>,
        consonneDemandee: Boolean,
        nombreLettres: Int = NOMBRE_LETTRES,
        random: Random = Random,
    ): Char {
        val demandeValide = consonneDemandee && consonneAutorisee(dejaTirees, nombreLettres)
        return if (demandeValide) sac.tirerConsonne(random) else sac.tirerVoyelle(random)
    }
}
