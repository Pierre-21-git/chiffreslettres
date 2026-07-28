package fr.pierre.chiffreslettres.ui.partieduo

import org.junit.Assert.assertEquals
import org.junit.Test

class ComparaisonDuoTest {
    @Test
    fun `chiffres - l'ecart le plus petit gagne`() {
        assertEquals(VainqueurManche.JOUEUR1, vainqueurMancheChiffres(3, 12))
        assertEquals(VainqueurManche.JOUEUR2, vainqueurMancheChiffres(12, 3))
    }

    @Test
    fun `chiffres - ecarts egaux donnent une egalite`() {
        assertEquals(VainqueurManche.EGALITE, vainqueurMancheChiffres(5, 5))
        assertEquals(VainqueurManche.EGALITE, vainqueurMancheChiffres(null, null))
    }

    @Test
    fun `chiffres - aucune proposition perd toujours face a une proposition`() {
        assertEquals(VainqueurManche.JOUEUR2, vainqueurMancheChiffres(null, 50))
        assertEquals(VainqueurManche.JOUEUR1, vainqueurMancheChiffres(50, null))
    }

    @Test
    fun `lettres - le mot le plus long gagne`() {
        assertEquals(VainqueurManche.JOUEUR1, vainqueurMancheLettres("MAISON", "TOIT"))
        assertEquals(VainqueurManche.JOUEUR2, vainqueurMancheLettres("TOIT", "MAISON"))
    }

    @Test
    fun `lettres - meme longueur donne une egalite`() {
        assertEquals(VainqueurManche.EGALITE, vainqueurMancheLettres("CHAT", "MOTS"))
    }

    @Test
    fun `lettres - mot absent vaut une longueur de 0`() {
        assertEquals(VainqueurManche.JOUEUR1, vainqueurMancheLettres("CHAT", null))
        assertEquals(VainqueurManche.EGALITE, vainqueurMancheLettres(null, null))
    }
}
