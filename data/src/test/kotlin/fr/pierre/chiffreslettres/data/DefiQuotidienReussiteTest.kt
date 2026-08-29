package fr.pierre.chiffreslettres.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefiQuotidienReussiteTest {

    @Test
    fun `premiere reussite du jour cree l'ensemble avec ce seul niveau`() {
        val resultat = apresReussiteDefiQuotidien(null, "", "EMILE")
        assertEquals("EMILE" to "EMILE", resultat)
    }

    @Test
    fun `rejouer un niveau superieur ajoute au lieu de remplacer, et devient le plus haut`() {
        val resultat = apresReussiteDefiQuotidien("EMILE", "EMILE", "NESTOR")
        assertEquals("NESTOR" to "EMILE,NESTOR", resultat)
    }

    @Test
    fun `rejouer un niveau deja reussi ne change rien`() {
        assertNull(apresReussiteDefiQuotidien("NESTOR", "EMILE,NESTOR", "EMILE"))
        assertNull(apresReussiteDefiQuotidien("NESTOR", "EMILE,NESTOR", "NESTOR"))
    }

    @Test
    fun `reussir un niveau inferieur a celui deja atteint l'ajoute sans redescendre le plus haut`() {
        val resultat = apresReussiteDefiQuotidien("MATHIEU", "MATHIEU", "EMILE")
        assertEquals("MATHIEU" to "MATHIEU,EMILE", resultat)
    }

    @Test
    fun `niveauxDepuisCsv ignore les entrees vides`() {
        assertEquals(emptySet<String>(), niveauxDepuisCsv(""))
        assertEquals(setOf("EMILE", "NESTOR"), niveauxDepuisCsv("EMILE,NESTOR"))
    }
}
