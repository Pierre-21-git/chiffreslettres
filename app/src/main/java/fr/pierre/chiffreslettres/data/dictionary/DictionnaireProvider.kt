package fr.pierre.chiffreslettres.data.dictionary

import android.content.Context
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val ASSET_DICTIONNAIRE = "dictionnaire_fr.txt"

/**
 * Pont entre les assets Android et le module pur `core-dictionary` : charge
 * l'asset une seule fois et met le résultat en cache pour la durée de vie du
 * process (l'indexation de ~150k mots est rapide mais pas gratuite).
 */
object DictionnaireProvider {
    private val mutex = Mutex()
    private var instance: DictionnaireIndex? = null

    suspend fun obtenir(context: Context): DictionnaireIndex {
        instance?.let { return it }
        return mutex.withLock {
            instance ?: withContext(Dispatchers.IO) {
                context.assets.open(ASSET_DICTIONNAIRE).bufferedReader(Charsets.UTF_8).useLines { lignes ->
                    DictionnaireIndex(lignes.toList().asSequence())
                }
            }.also { instance = it }
        }
    }
}
