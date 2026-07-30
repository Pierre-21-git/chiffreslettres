package fr.pierre.chiffreslettres.network

import org.json.JSONArray
import org.json.JSONObject

const val VERSION_PROTOCOLE = 1

data class ProfilReseau(val pseudo: String, val avatar: String)

private const val CLE_TYPE = "type"

/**
 * Message échangé sur le socket TCP, un objet JSON par ligne (cf. ConnexionSocket). org.json du
 * SDK Android : aucune nouvelle dépendance Gradle nécessaire.
 */
sealed interface MessageReseau {
    fun versJson(): JSONObject

    /**
     * Premier message envoyé spontanément par chaque pair dès la connexion établie (aucun des
     * deux n'attend que l'autre parle en premier, pour éviter tout blocage mutuel).
     */
    data class Bonjour(val profil: ProfilReseau, val version: Int = VERSION_PROTOCOLE) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("pseudo", profil.pseudo)
            put("avatar", profil.avatar)
            put("version", version)
        }
        companion object {
            const val TYPE = "bonjour"
        }
    }

    /** Fermeture volontaire annoncée avant de couper la socket (distingue d'une coupure réseau). */
    data object AuRevoir : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().put(CLE_TYPE, TYPE)
        const val TYPE = "auRevoir"
    }

    /**
     * Envoyée une fois par l'hôte, juste après la configuration de la partie : les graines
     * garantissent que les 2 téléphones jouent exactement la même séquence de manches.
     */
    data class Configuration(val niveauCode: String, val modeCode: String, val seeds: List<Long>) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("niveauCode", niveauCode)
            put("modeCode", modeCode)
            put("seeds", JSONArray(seeds))
        }
        companion object {
            const val TYPE = "configuration"
        }
    }

    /** Résultat d'une manche envoyé par chaque téléphone à la fin de sa propre manche. */
    data class ResultatDeManche(
        val index: Int,
        val modeJeu: String,
        val niveauCode: String,
        val score: Int,
        val motJoue: String?,
        val ecartCible: Int?,
        val detail: String,
    ) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("index", index)
            put("modeJeu", modeJeu)
            put("niveauCode", niveauCode)
            put("score", score)
            put("motJoue", motJoue)
            put("ecartCible", ecartCible)
            put("detail", detail)
        }
        companion object {
            const val TYPE = "resultatDeManche"
        }
    }

    /**
     * Manches lettres uniquement : envoyé par le joueur désigné "déclencheur" de la manche
     * (cf. `premierJoueurManche`) dès qu'il choisit son nombre de voyelles — sert à la fois de
     * valeur (le tirage en dépend, pas seulement de la graine) et de signal de départ simultané
     * pour l'autre joueur, qui ne choisit jamais lui-même.
     */
    data class ChoixVoyelles(val index: Int, val nombre: Int) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("index", index)
            put("nombre", nombre)
        }
        companion object {
            const val TYPE = "choixVoyelles"
        }
    }

    /**
     * Manches chiffres uniquement : envoyé par le déclencheur de la manche au clic sur
     * "Commencer la manche", pour que les 2 téléphones basculent sur l'écran de jeu au même
     * moment (pas de choix préalable en chiffres, contrairement aux lettres).
     */
    data class DemarrerManche(val index: Int) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("index", index)
        }
        companion object {
            const val TYPE = "demarrerManche"
        }
    }

    /**
     * Envoyé par le non-déclencheur dès qu'il atteint l'écran d'attente d'une manche : le
     * déclencheur n'affiche son bouton "Commencer"/l'écran de choix des voyelles qu'une fois ce
     * signal reçu, pour éviter de déclencher une manche avant que l'autre soit vraiment là.
     */
    data class PretPourManche(val index: Int) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("index", index)
        }
        companion object {
            const val TYPE = "pretPourManche"
        }
    }

    companion object {
        fun depuisJson(ligne: String): MessageReseau? = runCatching {
            val json = JSONObject(ligne)
            when (json.optString(CLE_TYPE)) {
                Bonjour.TYPE -> Bonjour(
                    profil = ProfilReseau(json.getString("pseudo"), json.getString("avatar")),
                    version = json.optInt("version", 1),
                )
                AuRevoir.TYPE -> AuRevoir
                Configuration.TYPE -> {
                    val seedsJson = json.getJSONArray("seeds")
                    Configuration(
                        niveauCode = json.getString("niveauCode"),
                        modeCode = json.getString("modeCode"),
                        seeds = List(seedsJson.length()) { seedsJson.getLong(it) },
                    )
                }
                ResultatDeManche.TYPE -> ResultatDeManche(
                    index = json.getInt("index"),
                    modeJeu = json.getString("modeJeu"),
                    niveauCode = json.getString("niveauCode"),
                    score = json.getInt("score"),
                    motJoue = if (json.isNull("motJoue")) null else json.getString("motJoue"),
                    ecartCible = if (json.isNull("ecartCible")) null else json.getInt("ecartCible"),
                    detail = json.getString("detail"),
                )
                ChoixVoyelles.TYPE -> ChoixVoyelles(index = json.getInt("index"), nombre = json.getInt("nombre"))
                DemarrerManche.TYPE -> DemarrerManche(index = json.getInt("index"))
                PretPourManche.TYPE -> PretPourManche(index = json.getInt("index"))
                else -> null
            }
        }.getOrNull()
    }
}
