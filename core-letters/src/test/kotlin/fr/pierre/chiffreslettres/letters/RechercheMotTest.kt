package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RechercheMotTest {

    private val dictionnaire = DictionnaireIndex(
        sequenceOf("chat", "chatte", "chien", "rat", "art"),
    )

    @Test
    fun `meilleurMot renvoie le mot le plus long jouable`() {
        assertEquals("chatte", meilleurMot("CHATTEXYZ".toList(), dictionnaire))
    }

    @Test
    fun `meilleurMot renvoie null si rien n'est jouable`() {
        assertNull(meilleurMot("BXYQWK".toList(), dictionnaire))
    }

    @Test
    fun `dixMeilleursMots ne retient que les deux plus grandes longueurs, triees par longueur decroissante puis ordre alphabetique`() {
        val dico = DictionnaireIndex(sequenceOf("art", "rat", "as", "at", "arts"))
        // "as"/"at" (2 lettres) sont une 3e longueur, écartée : seules les longueurs 4 et 3 sont gardées.
        assertEquals(listOf("arts", "art", "rat"), dixMeilleursMots("ARTS".toList(), dico))
    }

    @Test
    fun `dixMeilleursMots n'a plus de plafond fixe, meme au-dela de 10 mots`() {
        val motsLongueur4 = listOf(
            "aaaa", "aaab", "aaba", "aabb", "abaa", "abab", "abba", "abbb", "baaa", "baab", "baba",
        )
        val motsLongueur3 = listOf("aab", "abb")
        val dico = DictionnaireIndex((motsLongueur4 + motsLongueur3).asSequence())
        assertEquals(motsLongueur4 + motsLongueur3, dixMeilleursMots("AAAAAABBBB".toList(), dico))
    }
}
