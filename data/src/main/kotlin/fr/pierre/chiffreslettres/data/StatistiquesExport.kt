package fr.pierre.chiffreslettres.data

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val FORMAT_APPLICATION = "chiffreslettres"
private const val FORMAT_TYPE = "statistiques"
private const val FORMAT_VERSION = 1

/** Contenu exportable/importable des statistiques d'un joueur (historique + défis + trophées). */
data class ExportStatistiques(
    val sessions: List<SessionAvecManches>,
    val defis: List<DefiEntity>,
    val trophees: List<TropheeEntity>,
)

/**
 * Sérialisation JSON des statistiques d'un joueur, pour "Exporter mes statistiques"/"Importer mes
 * statistiques" (fichier auto-suffisant : les id de profil/session d'origine ne sont pas
 * exportés, de nouveaux id sont générés à l'import, cf. `HistoriqueRepository.importerSessions`).
 */
object StatistiquesExport {

    fun versJson(export: ExportStatistiques): String {
        val racine = JSONObject()
        racine.put("application", FORMAT_APPLICATION)
        racine.put("type", FORMAT_TYPE)
        racine.put("version", FORMAT_VERSION)

        val sessions = JSONArray()
        for (entree in export.sessions) {
            val session = JSONObject()
                .put("date", entree.session.date)
                .put("type", entree.session.type.name)
                .put("scoreTotal", entree.session.scoreTotal)
            val manches = JSONArray()
            for (manche in entree.manches) {
                manches.put(
                    JSONObject()
                        .put("ordre", manche.ordre)
                        .put("mode", manche.mode.name)
                        .put("niveauCode", manche.niveauCode)
                        .put("score", manche.score)
                        .put("motJoue", manche.motJoue ?: JSONObject.NULL),
                )
            }
            session.put("manches", manches)
            sessions.put(session)
        }
        racine.put("sessions", sessions)

        val defis = JSONArray()
        for (defi in export.defis) {
            defis.put(
                JSONObject()
                    .put("mode", defi.mode.name)
                    .put("niveauCode", defi.niveauCode)
                    .put("type", defi.type.name)
                    .put("serie", defi.serie)
                    .put("date", defi.date),
            )
        }
        racine.put("defis", defis)

        val trophees = JSONArray()
        for (trophee in export.trophees) {
            trophees.put(
                JSONObject()
                    .put("trophyId", trophee.trophyId)
                    .put("dateDebloque", trophee.dateDebloque),
            )
        }
        racine.put("trophees", trophees)

        return racine.toString(2)
    }

    /** @throws IllegalArgumentException si le contenu n'est pas un export de statistiques valide de cette application. */
    fun depuisJson(json: String): ExportStatistiques {
        val racine = try {
            JSONObject(json)
        } catch (e: JSONException) {
            throw IllegalArgumentException("Fichier illisible : ce n'est pas un JSON valide.", e)
        }
        if (racine.optString("application") != FORMAT_APPLICATION || racine.optString("type") != FORMAT_TYPE) {
            throw IllegalArgumentException("Ce fichier n'est pas un export de statistiques de l'application.")
        }

        try {
            val sessions = mutableListOf<SessionAvecManches>()
            val sessionsJson = racine.optJSONArray("sessions") ?: JSONArray()
            for (i in 0 until sessionsJson.length()) {
                val s = sessionsJson.getJSONObject(i)
                val manches = mutableListOf<MancheEntity>()
                val manchesJson = s.optJSONArray("manches") ?: JSONArray()
                for (j in 0 until manchesJson.length()) {
                    val m = manchesJson.getJSONObject(j)
                    manches += MancheEntity(
                        sessionId = 0,
                        ordre = m.getInt("ordre"),
                        mode = ModeJeu.valueOf(m.getString("mode")),
                        niveauCode = m.getString("niveauCode"),
                        score = m.getInt("score"),
                        motJoue = if (m.isNull("motJoue")) null else m.getString("motJoue"),
                    )
                }
                sessions += SessionAvecManches(
                    session = SessionEntity(
                        profilId = 0,
                        date = s.getLong("date"),
                        type = TypePartie.valueOf(s.getString("type")),
                        scoreTotal = s.getInt("scoreTotal"),
                    ),
                    manches = manches,
                )
            }

            val defis = mutableListOf<DefiEntity>()
            val defisJson = racine.optJSONArray("defis") ?: JSONArray()
            for (i in 0 until defisJson.length()) {
                val d = defisJson.getJSONObject(i)
                defis += DefiEntity(
                    profilId = 0,
                    mode = ModeJeu.valueOf(d.getString("mode")),
                    niveauCode = d.getString("niveauCode"),
                    type = TypeDefi.valueOf(d.getString("type")),
                    serie = d.getInt("serie"),
                    date = d.getLong("date"),
                )
            }

            val trophees = mutableListOf<TropheeEntity>()
            val tropheesJson = racine.optJSONArray("trophees") ?: JSONArray()
            for (i in 0 until tropheesJson.length()) {
                val t = tropheesJson.getJSONObject(i)
                trophees += TropheeEntity(
                    profilId = 0,
                    trophyId = t.getString("trophyId"),
                    dateDebloque = t.getLong("dateDebloque"),
                )
            }

            return ExportStatistiques(sessions, defis, trophees)
        } catch (e: JSONException) {
            throw IllegalArgumentException("Fichier illisible : ce n'est pas un JSON valide.", e)
        } catch (e: IllegalArgumentException) {
            // Enum.valueOf() sur un code inconnu (mode/type/niveau) lève aussi IllegalArgumentException.
            throw IllegalArgumentException("Fichier illisible : contenu inattendu.", e)
        }
    }
}
