package fr.pierre.chiffreslettres.dictionary

import java.text.Normalizer
import java.util.Locale

/**
 * Structure de recherche pour le mode Lettres (spec §4.3) : chaque mot est
 * représenté par un vecteur de comptage des 26 lettres (accents et casse
 * normalisés), regroupé par longueur. La recherche pour un tirage donné
 * parcourt les longueurs décroissantes (9 → 2) et ne garde que les mots dont
 * le vecteur est un sous-ensemble du tirage.
 *
 * Agnostique de la source des mots : un fichier fixture en test, un asset
 * Android une fois chargé en [Sequence] plus tard.
 */
class DictionnaireIndex(mots: Sequence<String>) {

    private data class EntreeMot(val mot: String, val vecteur: IntArray)

    private val motsParLongueur: Map<Int, List<EntreeMot>> = buildMap<Int, MutableList<EntreeMot>> {
        for (motBrut in mots) {
            val normalise = normaliser(motBrut) ?: continue
            if (normalise.length !in 2..9) continue
            getOrPut(normalise.length) { mutableListOf() }.add(EntreeMot(motBrut, vecteurLettres(normalise)))
        }
    }

    /** Renvoie les mots de longueur maximale jouables avec ce tirage, ou une liste vide. */
    fun rechercher(tirage: List<Char>): List<String> {
        val vecteurTirage = vecteurLettres(tirage.joinToString("").uppercase(Locale.FRENCH))
        for (longueur in 9 downTo 2) {
            val candidats = motsParLongueur[longueur].orEmpty()
                .filter { estSousEnsemble(it.vecteur, vecteurTirage) }
            if (candidats.isNotEmpty()) return candidats.map { it.mot }
        }
        return emptyList()
    }

    /** Un mot précis est-il jouable avec ce tirage ? */
    fun estJouable(mot: String, tirage: List<Char>): Boolean {
        val normalise = normaliser(mot) ?: return false
        val vecteurTirage = vecteurLettres(tirage.joinToString("").uppercase(Locale.FRENCH))
        return estSousEnsemble(vecteurLettres(normalise), vecteurTirage)
    }

    companion object {
        /** Majuscule, sans accents ; renvoie null si le mot contient autre chose que des lettres. */
        fun normaliser(mot: String): String? {
            if (mot.isEmpty() || mot.any { !it.isLetter() }) return null
            val decompose = Normalizer.normalize(mot.uppercase(Locale.FRENCH), Normalizer.Form.NFD)
            val lettres = decompose.filter { it in 'A'..'Z' }
            return lettres.ifEmpty { null }
        }

        fun vecteurLettres(mot: String): IntArray {
            val vecteur = IntArray(26)
            for (c in mot) {
                if (c in 'A'..'Z') vecteur[c - 'A']++
            }
            return vecteur
        }

        private fun estSousEnsemble(vecteurMot: IntArray, vecteurTirage: IntArray): Boolean {
            for (i in vecteurMot.indices) {
                if (vecteurMot[i] > vecteurTirage[i]) return false
            }
            return true
        }
    }
}
