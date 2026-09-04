package fr.pierre.chiffreslettres.ui.defi

import fr.pierre.chiffreslettres.letters.NiveauLettres
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class ParametresDefiTest {
    @Test
    fun `seuil de longueur des mots croit avec la difficulte du niveau`() {
        assertEquals(4, seuilLongueurDefiLettres(NiveauLettres.EMILE))
        assertEquals(5, seuilLongueurDefiLettres(NiveauLettres.NESTOR))
        assertEquals(6, seuilLongueurDefiLettres(NiveauLettres.MONIQUE))
        assertEquals(7, seuilLongueurDefiLettres(NiveauLettres.MATHIEU))
    }

    @Test
    fun `un mot atteignant le seuil reussit quel que soit le niveau`() {
        assertTrue(motEstReussiDefiLettres(NiveauLettres.MONIQUE, "chaton", seuil = 6, meilleurMot = "chaton"))
    }

    @Test
    fun `sous le seuil, Monique et Mathieu reussissent si c'est le meilleur mot possible du tirage`() {
        // Aucun mot de 6 lettres ou plus n'existe dans ce tirage (meilleurMot = 5 lettres) :
        // le joueur qui trouve ce meilleur mot réussit quand même la manche (retour utilisateur).
        assertTrue(motEstReussiDefiLettres(NiveauLettres.MONIQUE, "table", seuil = 6, meilleurMot = "table"))
        assertTrue(motEstReussiDefiLettres(NiveauLettres.MATHIEU, "table", seuil = 7, meilleurMot = "table"))
    }

    @Test
    fun `sous le seuil, un mot qui n'est pas le meilleur possible echoue meme sur Monique et Mathieu`() {
        assertFalse(motEstReussiDefiLettres(NiveauLettres.MONIQUE, "table", seuil = 6, meilleurMot = "tables"))
    }

    @Test
    fun `la tolerance meilleure approche ne s'applique pas a Emile et Nestor`() {
        assertFalse(motEstReussiDefiLettres(NiveauLettres.EMILE, "col", seuil = 4, meilleurMot = "col"))
        assertFalse(motEstReussiDefiLettres(NiveauLettres.NESTOR, "col", seuil = 5, meilleurMot = "col"))
    }

    @Test
    fun `le nombre d'objectifs du defi Points croit avec la difficulte du niveau, en mode libre`() {
        assertEquals(3, nombreObjectifsDefiPoints(NiveauLettres.EMILE))
        assertEquals(5, nombreObjectifsDefiPoints(NiveauLettres.NESTOR))
        assertEquals(8, nombreObjectifsDefiPoints(NiveauLettres.MONIQUE))
        assertEquals(15, nombreObjectifsDefiPoints(NiveauLettres.MATHIEU))
    }

    @Test
    fun `le defi quotidien reduit le nombre d'objectifs du defi Points de moitie environ`() {
        assertEquals(2, nombreObjectifsDefiPoints(NiveauLettres.EMILE, estDefiQuotidien = true))
        assertEquals(3, nombreObjectifsDefiPoints(NiveauLettres.NESTOR, estDefiQuotidien = true))
        assertEquals(4, nombreObjectifsDefiPoints(NiveauLettres.MONIQUE, estDefiQuotidien = true))
        assertEquals(8, nombreObjectifsDefiPoints(NiveauLettres.MATHIEU, estDefiQuotidien = true))
    }
}
