package fr.pierre.chiffreslettres.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatistiquesExportTest {

    @Test
    fun `l'aller-retour JSON conserve tous les champs d'une manche et d'une session`() {
        val manche = MancheEntity(
            sessionId = 0,
            ordre = 0,
            mode = ModeJeu.CHIFFRES,
            niveauCode = "MATHIEU",
            score = 10,
            motJoue = null,
            longueurMotInvalide = null,
            cibleChiffres = 537,
            nombreOperationsChiffres = 1,
            maxEtapeIntermediaireChiffres = 42,
            dureeSecondesManche = 12,
            tempsRestantSecondesValidation = 88,
            ecartCibleChiffres = 250,
            operateursUtilisesChiffres = 0b1111,
        )
        val session = SessionEntity(
            profilId = 0,
            date = 1_700_000_000_000L,
            type = TypePartie.DUO,
            scoreTotal = 10,
            victoireDuel = true,
            egaliteDuel = false,
            ecartDuel = 25,
            objectifExactAtteint = true,
        )
        val defiQuotidien = DefiQuotidienEntity(
            profilId = 0,
            jour = "2026-08-29",
            dateReussite = 1_700_000_000_000L,
            niveau = "NESTOR",
            niveauxReussis = "EMILE,NESTOR",
        )
        val export = ExportStatistiques(
            sessions = listOf(SessionAvecManches(session, listOf(manche))),
            defis = emptyList(),
            defisQuotidiens = listOf(defiQuotidien),
            trophees = emptyList(),
        )

        val relu = StatistiquesExport.depuisJson(StatistiquesExport.versJson(export))

        val sessionRelue = relu.sessions.single()
        assertEquals(true, sessionRelue.session.victoireDuel)
        assertEquals(false, sessionRelue.session.egaliteDuel)
        assertEquals(25, sessionRelue.session.ecartDuel)
        assertEquals(true, sessionRelue.session.objectifExactAtteint)
        val mancheRelue = sessionRelue.manches.single()
        assertEquals(537, mancheRelue.cibleChiffres)
        assertEquals(1, mancheRelue.nombreOperationsChiffres)
        assertEquals(42, mancheRelue.maxEtapeIntermediaireChiffres)
        assertEquals(12, mancheRelue.dureeSecondesManche)
        assertEquals(88, mancheRelue.tempsRestantSecondesValidation)
        assertEquals(250, mancheRelue.ecartCibleChiffres)
        assertEquals(0b1111, mancheRelue.operateursUtilisesChiffres)
        val defiQuotidienRelu = relu.defisQuotidiens.single()
        assertEquals("EMILE,NESTOR", defiQuotidienRelu.niveauxReussis)
    }

    @Test
    fun `l'import reste compatible avec un export sans les champs ajoutes en v1_95`() {
        // Fichier tel qu'il aurait été produit avant l'ajout de ces champs (v1.91) : les clés
        // n'existent pas du tout dans le JSON, pas seulement à null.
        val json = """
            {
              "application": "chiffreslettres",
              "type": "statistiques",
              "version": 1,
              "sessions": [
                {
                  "date": 1700000000000,
                  "type": "STRUCTUREE",
                  "scoreTotal": 20,
                  "manches": [
                    {"ordre": 0, "mode": "LETTRES", "niveauCode": "EMILE", "score": 5, "motJoue": "table"}
                  ]
                }
              ],
              "defis": [],
              "defisQuotidiens": [],
              "trophees": []
            }
        """.trimIndent()

        val relu = StatistiquesExport.depuisJson(json)

        val sessionRelue = relu.sessions.single()
        assertNull(sessionRelue.session.victoireDuel)
        assertNull(sessionRelue.session.egaliteDuel)
        assertNull(sessionRelue.session.ecartDuel)
        assertNull(sessionRelue.session.objectifExactAtteint)
        val mancheRelue = sessionRelue.manches.single()
        assertNull(mancheRelue.cibleChiffres)
        assertNull(mancheRelue.dureeSecondesManche)
        assertNull(mancheRelue.ecartCibleChiffres)
        assertNull(mancheRelue.operateursUtilisesChiffres)
    }

    @Test
    fun `un defi Points s'exporte et se reimporte avec le bon type`() {
        val defi = DefiEntity(profilId = 0, mode = ModeJeu.LETTRES, niveauCode = "MONIQUE", type = TypeDefi.OBJECTIFS_POINTS, serie = 4, date = 1L)
        val export = ExportStatistiques(sessions = emptyList(), defis = listOf(defi), defisQuotidiens = emptyList(), trophees = emptyList())

        val relu = StatistiquesExport.depuisJson(StatistiquesExport.versJson(export))

        assertEquals(TypeDefi.OBJECTIFS_POINTS, relu.defis.single().type)
        assertEquals(4, relu.defis.single().serie)
    }
}
