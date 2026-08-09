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
    fun `dixMeilleursMots trie par longueur decroissante puis ordre alphabetique`() {
        val dico = DictionnaireIndex(sequenceOf("art", "rat", "as", "at", "arts"))
        assertEquals(listOf("arts", "art", "rat", "as", "at"), dixMeilleursMots("ARTS".toList(), dico))
    }

    @Test
    fun `dixMeilleursMots respecte la limite demandee`() {
        val dico = DictionnaireIndex(sequenceOf("art", "rat", "as", "at", "arts"))
        assertEquals(listOf("arts", "art", "rat"), dixMeilleursMots("ARTS".toList(), dico, limite = 3))
    }
}
