package fr.pierre.chiffreslettres.letters

import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjectifsPointsTest {

    // Mots de "A" (1 point chacun) de longueur 2 à 9 : score == longueur, valeurs 2..9 faciles à
    // prédire, tous jouables sur un tirage de 9 "A" (pas besoin de TirageLettres pour ce test).
    // Le score 2 ("AA") est en dessous du minimum d'objectif (3 points) et donc toujours exclu.
    private val dictionnaire = DictionnaireIndex((2..9).asSequence().map { "A".repeat(it) })
    private val bareme = BaremeLettres.FRANCAIS

    @Test
    fun `genererObjectifs repartit les scores en quantiles du plus faible au plus eleve`() {
        val tirage = List(9) { 'A' }
        val objectifs = genererObjectifs(tirage, dictionnaire, nombreObjectifs = 3, bareme = bareme)
        assertEquals(listOf(3, 6, 9), objectifs.map { it.points })
        assertTrue(objectifs.all { !it.atteint })
    }

    @Test
    fun `genererObjectifs avec un seul objectif prend le score le plus faible`() {
        val tirage = List(9) { 'A' }
        assertEquals(listOf(3), genererObjectifs(tirage, dictionnaire, nombreObjectifs = 1, bareme = bareme).map { it.points })
    }

    @Test
    fun `genererObjectifs renvoie moins d'objectifs que demande si le tirage n'offre pas assez de valeurs distinctes`() {
        // "AA" (2, exclu car sous le minimum) et "AAA" (3) sont jouables sur un tirage de 3 lettres.
        val tirage = List(3) { 'A' }
        val objectifs = genererObjectifs(tirage, dictionnaire, nombreObjectifs = 5, bareme = bareme)
        assertEquals(listOf(3), objectifs.map { it.points })
    }

    @Test
    fun `genererObjectifs renvoie une liste vide si aucun mot n'est jouable`() {
        val tirage = List(9) { 'Z' }
        assertEquals(emptyList<ObjectifPoints>(), genererObjectifs(tirage, dictionnaire, nombreObjectifs = 3, bareme = bareme))
    }

    @Test
    fun `genererObjectifs couvre les mots courts, pas seulement la plus grande longueur du tirage`() {
        // dictionnaire.rechercher() s'arrêterait à "AAAAAAAAA" (9 lettres, seule plus grande
        // longueur jouable) : genererObjectifs doit utiliser rechercherAuMoins pour couvrir aussi
        // les scores faibles ("AAA" = 3 points, le plus petit non filtré), conformément à
        // l'intention de la spec.
        val tirage = List(9) { 'A' }
        assertTrue(3 in genererObjectifs(tirage, dictionnaire, nombreObjectifs = 8, bareme = bareme).map { it.points })
    }

    @Test
    fun `genererObjectifs exclut les scores en dessous du minimum d'objectif`() {
        val tirage = List(9) { 'A' }
        val objectifs = genererObjectifs(tirage, dictionnaire, nombreObjectifs = 8, bareme = bareme)
        assertTrue(objectifs.none { it.points < 3 })
    }
}
