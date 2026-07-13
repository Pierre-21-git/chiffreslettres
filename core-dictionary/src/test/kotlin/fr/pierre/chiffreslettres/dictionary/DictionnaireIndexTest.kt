package fr.pierre.chiffreslettres.dictionary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionnaireIndexTest {

    private val motsFixture = listOf(
        "chat", "chatte", "chien", "niche", "rat", "art", "tarte", "carte",
        "écran", "élève", "café", "être", "grand-mère", "aujourd'hui", "a",
    )

    private fun index() = DictionnaireIndex(motsFixture.asSequence())

    @Test
    fun `trouve le mot le plus long jouable avec un tirage`() {
        // "chatte" (6 lettres) doit être jouable avec ces lettres.
        val tirage = "CHATTEXYZ".toList() // 9 lettres, contient bien C H A T T E + 3 lettres inutiles
        val resultats = index().rechercher(tirage)
        assertTrue("chatte" in resultats)
    }

    @Test
    fun `ignore les mots trop courts si un plus long existe`() {
        val tirage = "CHATTEXYZ".toList()
        val resultats = index().rechercher(tirage)
        // "chat" (4 lettres) ne doit pas apparaître puisque "chatte" (6) est trouvé.
        assertFalse("chat" in resultats)
    }

    @Test
    fun `aucun mot jouable si les lettres ne suffisent pas`() {
        val resultats = index().rechercher("BXYQWK".toList())
        assertTrue(resultats.isEmpty())
    }

    @Test
    fun `respecte les quantites de lettres disponibles`() {
        // "carte" nécessite deux lettres A? non, une seule. Testons un mot nécessitant 2 T avec un seul T dispo.
        val tirageUnSeulT = "CHATERS".toList() // un seul T, 7 lettres
        val resultats = index().rechercher(tirageUnSeulT)
        assertFalse("chatte" in resultats) // nécessite 2 T
    }

    @Test
    fun `les accents sont normalises pour la comparaison`() {
        // "écran" doit être jouable avec un tirage en lettres non accentuées ECRANXYZ.
        val resultats = index().rechercher("ECRANXYZ".toList())
        assertTrue("écran" in resultats)
    }

    @Test
    fun `les mots composes ou avec apostrophe sont exclus`() {
        val resultats = index().rechercher("AUJOURDHUIXYZ".toList())
        assertFalse("aujourd'hui" in resultats)
        assertFalse("grand-mère" in resultats)
    }

    @Test
    fun `estJouable verifie un mot precis`() {
        val idx = index()
        assertTrue(idx.estJouable("chat", "TACHEXYZ".toList()))
        assertFalse(idx.estJouable("chatte", "CHATXYZ".toList()))
    }
}
