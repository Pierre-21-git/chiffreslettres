package fr.pierre.chiffreslettres.data

import kotlinx.coroutines.flow.Flow

class ProfilRepository(private val dao: ProfilDao) {
    fun tousLesProfils(): Flow<List<ProfilEntity>> = dao.tous()

    suspend fun creerProfil(pseudo: String): Long =
        dao.inserer(ProfilEntity(pseudo = pseudo, dateCreation = System.currentTimeMillis()))

    suspend fun supprimerProfil(profil: ProfilEntity) = dao.supprimer(profil)

    suspend fun parId(id: Long): ProfilEntity? = dao.parId(id)

    suspend fun renommerProfil(id: Long, pseudo: String) = dao.renommer(id, pseudo)
}
