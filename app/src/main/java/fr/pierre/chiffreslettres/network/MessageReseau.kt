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
        /** Longueur du mot soumis quand il était invalide (mode Lettres uniquement), pour le bonus de score de l'adversaire (retour utilisateur, cf. [fr.pierre.chiffreslettres.ui.partieduo.appliquerBonusMotInvalide]). */
        val longueurMotInvalide: Int? = null,
        /** Le mot lui-même quand il était invalide (mode Lettres uniquement), pour l'afficher sur l'écran de révélation côté adversaire (retour utilisateur : seule la longueur était transmise, le mot restait invisible). */
        val motInvalide: String? = null,
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
            put("longueurMotInvalide", longueurMotInvalide)
            put("motInvalide", motInvalide)
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

    /**
     * Envoyée une fois par l'hôte pour démarrer un "duel mots" (retour utilisateur, jeu 100 %
     * réseau) : une seule graine (un seul tirage partagé, pas une liste de manches comme
     * [Configuration]). [objectifMots] n'est utilisé qu'en sous-mode Confrontation (course au
     * premier à N mots), null en sous-mode Duo (chacun ses 5 minutes, comparés à la fin).
     */
    data class ConfigurationDuelMots(
        val sousMode: String,
        val niveauCode: String,
        val seed: Long,
        val objectifMots: Int?,
    ) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("sousMode", sousMode)
            put("niveauCode", niveauCode)
            put("seed", seed)
            put("objectifMots", objectifMots)
        }
        companion object {
            const val TYPE = "configurationDuelMots"
        }
    }

    /**
     * Duel mots, sous-mode Confrontation uniquement : diffusé par chaque téléphone dès qu'il
     * valide un nouveau mot, pour que l'adversaire le retire de ses mots encore disponibles et
     * mette à jour sa colonne en direct.
     */
    data class MotTrouve(val mot: String, val longueur: Int) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("mot", mot)
            put("longueur", longueur)
        }
        companion object {
            const val TYPE = "motTrouve"
        }
    }

    /**
     * Duel mots, sous-mode Duo uniquement : envoyé une fois par chaque téléphone à la fin de ses
     * 5 minutes, avec la liste complète de ses mots trouvés — l'adversaire ne les découvre qu'à
     * ce moment-là (retour utilisateur : à l'aveugle pendant la partie, comparaison à la fin).
     */
    data class ResultatDuelMotsDuo(val motsTrouves: List<String>) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("motsTrouves", JSONArray(motsTrouves))
        }
        companion object {
            const val TYPE = "resultatDuelMotsDuo"
        }
    }

    /**
     * Duel mots, sous-mode Confrontation uniquement : envoyé par le téléphone qui détecte le
     * premier la fin de la partie (objectif atteint, temps écoulé ou tous les mots possibles
     * trouvés), pour que les deux côtés terminent sur le même vainqueur (retour utilisateur :
     * simple "premier message reçu gagne", suffisant pour un jeu familial, pas de résolution de
     * quasi-simultanéité plus fine). [raison] est le nom d'une valeur de `RaisonFinConfrontation`.
     */
    data class FinDuelMots(val gagnantEstExpediteur: Boolean, val raison: String) : MessageReseau {
        override fun versJson(): JSONObject = JSONObject().apply {
            put(CLE_TYPE, TYPE)
            put("gagnantEstExpediteur", gagnantEstExpediteur)
            put("raison", raison)
        }
        companion object {
            const val TYPE = "finDuelMots"
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
                    longueurMotInvalide = if (json.isNull("longueurMotInvalide")) null else json.getInt("longueurMotInvalide"),
                    motInvalide = if (json.isNull("motInvalide")) null else json.getString("motInvalide"),
                )
                ChoixVoyelles.TYPE -> ChoixVoyelles(index = json.getInt("index"), nombre = json.getInt("nombre"))
                DemarrerManche.TYPE -> DemarrerManche(index = json.getInt("index"))
                PretPourManche.TYPE -> PretPourManche(index = json.getInt("index"))
                ConfigurationDuelMots.TYPE -> ConfigurationDuelMots(
                    sousMode = json.getString("sousMode"),
                    niveauCode = json.getString("niveauCode"),
                    seed = json.getLong("seed"),
                    objectifMots = if (json.isNull("objectifMots")) null else json.getInt("objectifMots"),
                )
                MotTrouve.TYPE -> MotTrouve(mot = json.getString("mot"), longueur = json.getInt("longueur"))
                ResultatDuelMotsDuo.TYPE -> {
                    val motsJson = json.getJSONArray("motsTrouves")
                    ResultatDuelMotsDuo(motsTrouves = List(motsJson.length()) { motsJson.getString(it) })
                }
                FinDuelMots.TYPE -> FinDuelMots(
                    gagnantEstExpediteur = json.getBoolean("gagnantEstExpediteur"),
                    raison = json.optString("raison", "OBJECTIF_ATTEINT"),
                )
                else -> null
            }
        }.getOrNull()
    }
}
