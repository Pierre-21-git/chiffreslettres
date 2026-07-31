package fr.pierre.chiffreslettres.data.dictionary

import android.content.Context
import fr.pierre.chiffreslettres.dictionary.DictionnaireIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val LANGUE_PAR_DEFAUT = "fr"

/**
 * Dictionnaire par langue de profil (retour utilisateur). Seuls le français et l'anglais ont un
 * asset construit pour l'instant (cf. mémoire projet "i18n : traductions en/de/es") ; l'allemand
 * et l'espagnol retombent sur le français en attendant leur propre dictionnaire.
 */
private val ASSETS_PAR_LANGUE = mapOf(
    "fr" to "dictionnaire_fr.txt",
    "en" to "dictionnaire_en.txt",
)

/**
 * Pont entre les assets Android et le module pur `core-dictionary` : charge chaque asset une
 * seule fois par langue et met le résultat en cache pour la durée de vie du process
 * (l'indexation de ~100-150k mots est rapide mais pas gratuite).
 */
object DictionnaireProvider {
    private val mutex = Mutex()
    private val instances = mutableMapOf<String, DictionnaireIndex>()

    suspend fun obtenir(context: Context, langue: String = LANGUE_PAR_DEFAUT): DictionnaireIndex {
        val nomAsset = ASSETS_PAR_LANGUE[langue] ?: ASSETS_PAR_LANGUE.getValue(LANGUE_PAR_DEFAUT)
        instances[nomAsset]?.let { return it }
        return mutex.withLock {
            instances[nomAsset] ?: withContext(Dispatchers.IO) {
                context.assets.open(nomAsset).bufferedReader(Charsets.UTF_8).useLines { lignes ->
                    DictionnaireIndex(lignes.toList().asSequence())
                }
            }.also { instances[nomAsset] = it }
        }
    }
}
