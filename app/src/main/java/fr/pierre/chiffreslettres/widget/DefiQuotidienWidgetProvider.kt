package fr.pierre.chiffreslettres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import fr.pierre.chiffreslettres.MainActivity
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.AppDatabaseProvider
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/** Une ligne par profil (max 6, largement suffisant pour un usage familial). */
private val LIGNES = listOf(
    Triple(R.id.ligne_1, R.id.nom_1, R.id.statut_1),
    Triple(R.id.ligne_2, R.id.nom_2, R.id.statut_2),
    Triple(R.id.ligne_3, R.id.nom_3, R.id.statut_3),
    Triple(R.id.ligne_4, R.id.nom_4, R.id.statut_4),
    Triple(R.id.ligne_5, R.id.nom_5, R.id.statut_5),
    Triple(R.id.ligne_6, R.id.nom_6, R.id.statut_6),
)

/**
 * Widget écran d'accueil listant tous les profils avec leur statut du défi quotidien (retour
 * utilisateur : un seul widget compact pour toute la famille plutôt qu'un widget par profil).
 * Les lignes fixes (jusqu'à 6) évitent la complexité d'un `RemoteViewsService` façon liste
 * dynamique, inutile vu le nombre de profils attendu dans ce jeu.
 */
class DefiQuotidienWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val resultatEnAttente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val vues = construireVues(context)
                for (id in appWidgetIds) appWidgetManager.updateAppWidget(id, vues)
            } finally {
                resultatEnAttente.finish()
            }
        }
    }

    companion object {
        /** Appelé juste après l'enregistrement d'une réussite du défi quotidien. */
        fun demanderMiseAJour(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, DefiQuotidienWidgetProvider::class.java))
            if (ids.isEmpty()) return
            context.sendBroadcast(
                Intent(context, DefiQuotidienWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                },
            )
        }
    }
}

private suspend fun construireVues(context: Context): RemoteViews {
    val db = AppDatabaseProvider.obtenir(context.applicationContext)
    val profilRepository = ProfilRepository(db.profilDao())
    val defiQuotidienRepository = DefiQuotidienRepository(db.defiQuotidienDao())
    val profils = profilRepository.tousLesProfils().first()
    val jour = LocalDate.now().toString()

    val vues = RemoteViews(context.packageName, R.layout.widget_defi_quotidien)
    val intentOuverture = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE,
    )
    vues.setOnClickPendingIntent(R.id.widget_racine, intentOuverture)
    vues.setViewVisibility(R.id.widget_vide, if (profils.isEmpty()) View.VISIBLE else View.GONE)

    for ((index, ids) in LIGNES.withIndex()) {
        val (idLigne, idNom, idStatut) = ids
        val profil = profils.getOrNull(index)
        if (profil == null) {
            vues.setViewVisibility(idLigne, View.GONE)
            continue
        }
        vues.setViewVisibility(idLigne, View.VISIBLE)
        remplirLigne(vues, idNom, idStatut, profil, defiQuotidienRepository, jour)
    }
    return vues
}

private suspend fun remplirLigne(
    vues: RemoteViews,
    idNom: Int,
    idStatut: Int,
    profil: ProfilEntity,
    defiQuotidienRepository: DefiQuotidienRepository,
    jour: String,
) {
    val fait = defiQuotidienRepository.reussiteDuJour(profil.id, jour)
    val statut = if (fait) {
        val serie = defiQuotidienRepository.serieActuelle(profil.id)
        "✅ série $serie"
    } else {
        "⏳"
    }
    vues.setTextViewText(idNom, "${profil.avatar} ${profil.pseudo}")
    vues.setTextViewText(idStatut, statut)
}
