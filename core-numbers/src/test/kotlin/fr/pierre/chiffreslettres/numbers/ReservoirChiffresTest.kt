package fr.pierre.chiffreslettres.numbers

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservoirChiffresTest {

    @Test
    fun `le reservoir contient 24 plaques`() {
        assertEquals(24, ReservoirChiffres.plaquesInitiales().size)
    }

    @Test
    fun `les petits nombres 1 a 10 sont en double exemplaire`() {
        val plaques = ReservoirChiffres.plaquesInitiales()
        for (v in 1..10) {
            assertEquals(2, plaques.count { it.valeur == v })
        }
    }

    @Test
    fun `les grands nombres sont en simple exemplaire`() {
        val plaques = ReservoirChiffres.plaquesInitiales()
        for (v in listOf(25, 50, 75, 100)) {
            assertEquals(1, plaques.count { it.valeur == v })
        }
    }

    @Test
    fun `tirage retourne 6 nombres avec au plus 2 grands nombres`() {
        val random = Random(42)
        repeat(500) {
            val tirage = ReservoirChiffres.tirerNombres(random)
            assertEquals(6, tirage.size)
            val nbGrands = tirage.count { it in ReservoirChiffres.GRANDS_NOMBRES }
            assertTrue("nbGrands=$nbGrands devrait être entre 0 et 2", nbGrands in 0..2)
        }
    }

    @Test
    fun `tirage sans remise ne depasse jamais le stock disponible`() {
        val random = Random(7)
        repeat(500) {
            val tirage = ReservoirChiffres.tirerNombres(random)
            for (v in 1..10) {
                assertTrue(tirage.count { it == v } <= 2)
            }
            for (v in listOf(25, 50, 75, 100)) {
                assertTrue(tirage.count { it == v } <= 1)
            }
        }
    }

    @Test
    fun `les deux bornes 0 et 2 grands nombres sont atteignables`() {
        val random = Random(1)
        val comptes = (0..2).associateWith { 0 }.toMutableMap()
        repeat(1000) {
            val tirage = ReservoirChiffres.tirerNombres(random)
            val nbGrands = tirage.count { it in ReservoirChiffres.GRANDS_NOMBRES }
            comptes[nbGrands] = (comptes[nbGrands] ?: 0) + 1
        }
        assertTrue(comptes.getValue(0) > 0)
        assertTrue(comptes.getValue(1) > 0)
        assertTrue(comptes.getValue(2) > 0)
    }
}
