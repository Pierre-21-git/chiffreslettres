package fr.pierre.chiffreslettres.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Compteurs de visite d'écran par profil (refonte 2026-08, easter eggs "Curieux"/"Data-lover") :
 * clés dynamiques par profilId dans le même DataStore "reglages" que ProfilActifStore/
 * ReglagesStore (un seul fichier DataStore autorisé par nom).
 */
class VisitesEcranStore(private val context: Context) {
    suspend fun marquerReglesVues(profilId: Long) {
        context.dataStore.edit { it[booleanPreferencesKey("regles_vues_$profilId")] = true }
    }

    suspend fun reglesDejaVues(profilId: Long): Boolean =
        context.dataStore.data.first()[booleanPreferencesKey("regles_vues_$profilId")] ?: false

    suspend fun incrementerVisitesStats(profilId: Long) {
        val cle = intPreferencesKey("visites_stats_$profilId")
        context.dataStore.edit { it[cle] = (it[cle] ?: 0) + 1 }
    }

    suspend fun nombreVisitesStats(profilId: Long): Int =
        context.dataStore.data.first()[intPreferencesKey("visites_stats_$profilId")] ?: 0

    /**
     * État déplié (défaut)/replié d'une section de l'écran Statistiques, par profil (retour
     * utilisateur : conserver l'affichage choisi même après avoir quitté l'app). [cle] identifie
     * la section (ex. "parties_MONIQUE", "defis_MATHIEU").
     */
    fun sectionDeplieeFlow(profilId: Long, cle: String): Flow<Boolean> =
        context.dataStore.data.map { it[booleanPreferencesKey("section_deplie_${cle}_$profilId")] ?: true }

    suspend fun definirSectionDepliee(profilId: Long, cle: String, depliee: Boolean) {
        context.dataStore.edit { it[booleanPreferencesKey("section_deplie_${cle}_$profilId")] = depliee }
    }
}
