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
    fun `dixMeilleursMots complete avec les longueurs inferieures tant que le total est sous 10`() {
        val dico = DictionnaireIndex(sequenceOf("art", "rat", "as", "at", "arts"))
        // Seuls 5 mots au total (toutes longueurs confondues) : la 3e longueur (2 lettres) est
        // donc incluse elle aussi, alors que l'ancienne règle "2 plus grandes longueurs" l'aurait
        // écartée (retour utilisateur : certains tirages n'affichaient que 5 à 7 mots).
        assertEquals(listOf("arts", "art", "rat", "as", "at"), dixMeilleursMots("ARTS".toList(), dico))
    }

    @Test
    fun `dixMeilleursMots s'arrete des qu'une tranche de longueur atteint 10 mots, sans la tronquer`() {
        val motsLongueur4 = listOf(
            "aaaa", "aaab", "aaba", "aabb", "abaa", "abab", "abba", "abbb", "baaa", "baab", "baba",
        )
        val motsLongueur3 = listOf("aab", "abb")
        val dico = DictionnaireIndex((motsLongueur4 + motsLongueur3).asSequence())
        // La tranche de 4 lettres compte déjà 11 mots (> 10) : elle n'est pas tronquée, et la
        // tranche de 3 lettres suivante n'est pas ajoutée (le seuil est déjà dépassé).
        assertEquals(motsLongueur4, dixMeilleursMots("AAAAAABBBB".toList(), dico))
    }

    @Test
    fun `dixMeilleursMots cumule plusieurs tranches successives jusqu'a depasser 10 mots`() {
        val motsLongueur5 = listOf("aaaaa", "aaaab", "aaaba")
        val motsLongueur4 = listOf("aaaa", "aaab", "aaba", "aabb")
        val motsLongueur3 = listOf("aaa", "aab", "aba", "abb", "baa")
        val dico = DictionnaireIndex((motsLongueur5 + motsLongueur4 + motsLongueur3).asSequence())
        // 3 mots de 5 lettres + 4 de 4 lettres = 7, encore sous 10 : la 3e tranche (3 lettres,
        // 5 mots) est donc ajoutée en entier, portant le total à 12.
        assertEquals(motsLongueur5 + motsLongueur4 + motsLongueur3, dixMeilleursMots("AAAAAABBBB".toList(), dico))
    }
}
