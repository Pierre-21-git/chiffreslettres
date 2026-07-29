package fr.pierre.chiffreslettres.network

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

    companion object {
        fun depuisJson(ligne: String): MessageReseau? = runCatching {
            val json = JSONObject(ligne)
            when (json.optString(CLE_TYPE)) {
                Bonjour.TYPE -> Bonjour(
                    profil = ProfilReseau(json.getString("pseudo"), json.getString("avatar")),
                    version = json.optInt("version", 1),
                )
                AuRevoir.TYPE -> AuRevoir
                else -> null
            }
        }.getOrNull()
    }
}
