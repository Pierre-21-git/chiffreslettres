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
import fr.pierre.chiffreslettres.data.CatalogueTrophees
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.Palier
import fr.pierre.chiffreslettres.data.ProfilEntity
import fr.pierre.chiffreslettres.data.ProfilRepository
import fr.pierre.chiffreslettres.data.TropheeRepository
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/** Une case par profil (max 8, grille 2x4, largement suffisant pour un usage familial). */
private val CASES = listOf(
    Triple(R.id.cadre_1, R.id.nom_1, R.id.statut_1),
    Triple(R.id.cadre_2, R.id.nom_2, R.id.statut_2),
    Triple(R.id.cadre_3, R.id.nom_3, R.id.statut_3),
    Triple(R.id.cadre_4, R.id.nom_4, R.id.statut_4),
    Triple(R.id.cadre_5, R.id.nom_5, R.id.statut_5),
    Triple(R.id.cadre_6, R.id.nom_6, R.id.statut_6),
    Triple(R.id.cadre_7, R.id.nom_7, R.id.statut_7),
    Triple(R.id.cadre_8, R.id.nom_8, R.id.statut_8),
)

/** Drawable de cadre associé au palier de rang du joueur (voir ui/theme/CouleurPalier.kt pour l'équivalent Compose). */
private fun drawableCadre(palier: Palier?): Int = when (palier) {
    Palier.BRONZE -> R.drawable.widget_cadre_bronze
    Palier.ARGENT -> R.drawable.widget_cadre_argent
    Palier.OR -> R.drawable.widget_cadre_or
    Palier.PLATINE -> R.drawable.widget_cadre_platine
    Palier.DIAMANT -> R.drawable.widget_cadre_diamant
    null -> R.drawable.widget_cadre_neutre
}

/**
 * Widget écran d'accueil listant tous les profils avec leur statut du défi quotidien (retour
 * utilisateur : un seul widget compact pour toute la famille plutôt qu'un widget par profil).
 * Grille de cases fixes (jusqu'à 8, 4 par ligne) : évite la complexité d'un `RemoteViewsService`
 * façon liste dynamique, inutile vu le nombre de profils attendu dans ce jeu.
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
    val tropheeRepository = TropheeRepository(db.tropheeDao(), db.historiqueDao(), db.defiDao(), db.defiQuotidienDao())
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

    for ((index, ids) in CASES.withIndex()) {
        val (idCadre, idNom, idStatut) = ids
        val profil = profils.getOrNull(index)
        if (profil == null) {
            vues.setViewVisibility(idCadre, View.GONE)
            continue
        }
        vues.setViewVisibility(idCadre, View.VISIBLE)
        remplirCase(vues, idCadre, idNom, idStatut, profil, defiQuotidienRepository, tropheeRepository, jour)
    }
    return vues
}

private suspend fun remplirCase(
    vues: RemoteViews,
    idCadre: Int,
    idNom: Int,
    idStatut: Int,
    profil: ProfilEntity,
    defiQuotidienRepository: DefiQuotidienRepository,
    tropheeRepository: TropheeRepository,
    jour: String,
) {
    val fait = defiQuotidienRepository.reussiteDuJour(profil.id, jour)
    val statut = if (fait) {
        val serie = defiQuotidienRepository.serieActuelle(profil.id)
        "✅ $serie"
    } else {
        "⏳"
    }
    val debloques = tropheeRepository.tropheesDebloques(profil.id).first()
    val palier = CatalogueTrophees.rangJoueur(debloques.map { it.trophyId }.toSet())

    vues.setInt(idCadre, "setBackgroundResource", drawableCadre(palier))
    vues.setTextViewText(idNom, "${profil.avatar} ${profil.pseudo}")
    vues.setTextViewText(idStatut, statut)
}
