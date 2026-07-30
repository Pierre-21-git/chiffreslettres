package fr.pierre.chiffreslettres.data.alphabet

import android.content.Context
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.letters.NiveauLettres

/**
 * Contenu réel de l'alphabet du mode Lettres, lu depuis strings.xml (retour utilisateur :
 * externalisé pour permettre une déclinaison par langue). Pont entre les ressources Android
 * et le module pur `core-letters`, qui reçoit ces valeurs en paramètre sans connaître leur
 * origine — cf. `DictionnaireProvider` pour le même principe côté dictionnaire.
 */
data class ConfigurationAlphabetLettres(
    val distributionBase: Map<Char, Int>,
    val voyelles: Set<Char>,
    val lettresExcluesParNiveau: Map<NiveauLettres, Set<Char>>,
)

object ConfigurationAlphabetProvider {
    fun charger(context: Context): ConfigurationAlphabetLettres {
        val resources = context.resources
        val lettres = resources.getStringArray(R.array.alphabet_lettres)
        val comptes = resources.getIntArray(R.array.alphabet_comptes)
        require(lettres.size == comptes.size) { "alphabet_lettres et alphabet_comptes doivent avoir la même taille" }
        val distributionBase = lettres.map { it[0] }.zip(comptes.toList()).toMap()
        val voyelles = resources.getString(R.string.alphabet_voyelles).toSet()
        val lettresExcluesParNiveau = mapOf(
            NiveauLettres.EMILE to resources.getString(R.string.lettres_exclues_emile).toSet(),
            NiveauLettres.NESTOR to resources.getString(R.string.lettres_exclues_nestor).toSet(),
            NiveauLettres.MONIQUE to resources.getString(R.string.lettres_exclues_monique).toSet(),
            NiveauLettres.MATHIEU to resources.getString(R.string.lettres_exclues_mathieu).toSet(),
        )
        return ConfigurationAlphabetLettres(distributionBase, voyelles, lettresExcluesParNiveau)
    }
}
