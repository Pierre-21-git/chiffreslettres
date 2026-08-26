package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Un seul DataStore "reglages" pour toute l'app : DataStore n'autorise qu'une
 * instance par nom de fichier, donc cette propriété doit rester unique et être
 * réutilisée par toutes les classes de préférences (ne pas la redéclarer ailleurs).
 * `internal` (et non `private`) pour être réutilisable depuis les autres fichiers du
 * module data, ex. [ReglagesStore]. */
internal val Context.dataStore by preferencesDataStore(name = "reglages")

private val CLE_PROFIL_ACTIF = longPreferencesKey("profil_actif_id")

/** Le profil sélectionné reste actif jusqu'à changement explicite (spec §7.1). */
class ProfilActifStore(private val context: Context) {
    val profilActifId: Flow<Long?> = context.dataStore.data.map { it[CLE_PROFIL_ACTIF] }

    suspend fun definirProfilActif(id: Long) {
        context.dataStore.edit { it[CLE_PROFIL_ACTIF] = id }
    }
}
