package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Un seul DataStore "reglages" pour toute l'app : DataStore n'autorise qu'une
 * instance par nom de fichier, donc cette propriété doit rester unique et être
 * réutilisée par toutes les classes de préférences (ne pas la redéclarer ailleurs). */
private val Context.dataStore by preferencesDataStore(name = "reglages")

private val CLE_PROFIL_ACTIF = longPreferencesKey("profil_actif_id")

/** Le profil sélectionné reste actif jusqu'à changement explicite (spec §7.1). */
class ProfilActifStore(private val context: Context) {
    val profilActifId: Flow<Long?> = context.dataStore.data.map { it[CLE_PROFIL_ACTIF] }

    suspend fun definirProfilActif(id: Long) {
        context.dataStore.edit { it[CLE_PROFIL_ACTIF] = id }
    }
}

private val CLE_DUREE_CHIFFRES = intPreferencesKey("duree_chiffres_secondes")
private val CLE_DUREE_LETTRES = intPreferencesKey("duree_lettres_secondes")
private const val DUREE_CHIFFRES_DEFAUT = 45
private const val DUREE_LETTRES_DEFAUT = 40

/** Durées de chrono configurables (spec §5), minimum technique de 10s. */
class ReglagesStore(private val context: Context) {
    val dureeChiffresSecondes: Flow<Int> =
        context.dataStore.data.map { it[CLE_DUREE_CHIFFRES] ?: DUREE_CHIFFRES_DEFAUT }
    val dureeLettresSecondes: Flow<Int> =
        context.dataStore.data.map { it[CLE_DUREE_LETTRES] ?: DUREE_LETTRES_DEFAUT }

    suspend fun definirDureeChiffres(secondes: Int) {
        context.dataStore.edit { it[CLE_DUREE_CHIFFRES] = secondes }
    }

    suspend fun definirDureeLettres(secondes: Int) {
        context.dataStore.edit { it[CLE_DUREE_LETTRES] = secondes }
    }
}
