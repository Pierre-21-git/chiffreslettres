package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val CLE_RAPPEL_ACTIF = booleanPreferencesKey("rappel_defi_actif")

/**
 * Réglages globaux, non liés à un profil (retour mainteneur F-Droid : le rappel quotidien ne
 * doit pas être actif par défaut, la permission de notification associée ne doit être demandée
 * que si l'utilisateur l'active explicitement). Réutilise le même DataStore "reglages" que
 * [ProfilActifStore] (un seul fichier DataStore autorisé par nom).
 */
class ReglagesStore(private val context: Context) {
    val rappelDefiActif: Flow<Boolean> = context.dataStore.data.map { it[CLE_RAPPEL_ACTIF] ?: false }

    suspend fun definirRappelDefiActif(actif: Boolean) {
        context.dataStore.edit { it[CLE_RAPPEL_ACTIF] = actif }
    }
}
