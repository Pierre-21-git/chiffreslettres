package fr.pierre.chiffreslettres.ui.partieduo

import fr.pierre.chiffreslettres.data.ModeJeu
import fr.pierre.chiffreslettres.data.ResultatManche
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

    @Test
    fun `bonus mot invalide - remplace le score de l'adversaire, pas un ajout`() {
        // A invalide 8 lettres, B valide 7 lettres : B marque 8 points (pas 7, pas 15).
        val a = ResultatManche(ModeJeu.LETTRES, "MONIQUE", score = 0, motJoue = null, longueurMotInvalide = 8)
        val b = ResultatManche(ModeJeu.LETTRES, "MONIQUE", score = 7, motJoue = "chapeau")
        val (resultatA, resultatB) = appliquerBonusMotInvalide(a, b)
        assertEquals(0, resultatA.score)
        assertEquals(8, resultatB.score)
    }

    @Test
    fun `bonus mot invalide - ne s'applique pas si le mot invalide est plus court`() {
        val a = ResultatManche(ModeJeu.LETTRES, "MONIQUE", score = 0, motJoue = null, longueurMotInvalide = 5)
        val b = ResultatManche(ModeJeu.LETTRES, "MONIQUE", score = 7, motJoue = "chapeau")
        val (resultatA, resultatB) = appliquerBonusMotInvalide(a, b)
        assertEquals(0, resultatA.score)
        assertEquals(7, resultatB.score)
    }

    @Test
    fun `bonus mot invalide - sans effet en chiffres`() {
        val a = ResultatManche(ModeJeu.CHIFFRES, "MONIQUE", score = 0, longueurMotInvalide = 8)
        val b = ResultatManche(ModeJeu.CHIFFRES, "MONIQUE", score = 7)
        val (resultatA, resultatB) = appliquerBonusMotInvalide(a, b)
        assertEquals(0, resultatA.score)
        assertEquals(7, resultatB.score)
    }
}
