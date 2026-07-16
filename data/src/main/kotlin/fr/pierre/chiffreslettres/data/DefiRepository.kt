package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

class DefiRepository(private val dao: DefiDao) {

    suspend fun enregistrer(profilId: Long, mode: ModeJeu, niveauCode: String, serie: Int) {
        dao.enregistrer(
            DefiEntity(
                profilId = profilId,
                mode = mode,
                niveauCode = niveauCode,
                serie = serie,
                date = System.currentTimeMillis(),
            ),
        )
    }

    fun meilleursDefisParNiveau(profilId: Long, mode: ModeJeu, niveauCode: String): Flow<List<MeilleurDefi>> =
        dao.meilleursDefisParNiveau(profilId, mode, niveauCode)

    suspend fun reinitialiserJoueur(profilId: Long) = dao.reinitialiserJoueur(profilId)
}
