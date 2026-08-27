package fr.pierre.chiffreslettres.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

private const val NOM_TRAVAIL_PERIODIQUE = "rafraichissement_widget_minuit"
private const val ACTION_ALARME_MINUIT = "fr.pierre.chiffreslettres.widget.ALARME_RAFRAICHISSEMENT_MINUIT"
private const val REQUEST_CODE_ALARME = 4200

/**
 * Planifie un rafraîchissement du widget à minuit (retour utilisateur : le widget affiche le
 * statut du défi "du jour", qui change à minuit). Deux mécanismes combinés, car aucun des deux
 * seul n'est fiable :
 * - Un [AlarmManager] réveillant l'app même pendant Doze/App Standby via
 *   [AlarmManager.setAndAllowWhileIdle] (pas besoin de permission spéciale, contrairement à
 *   `setExactAndAllowWhileIdle`/`SCHEDULE_EXACT_ALARM` — inexact mais livré dans la prochaine
 *   fenêtre de maintenance, largement suffisant ici et sans demander d'autorisation
 *   supplémentaire à l'utilisateur). Se replanifie lui-même pour le minuit suivant à chaque
 *   déclenchement, cf. [AlarmeRafraichissementWidgetMinuitReceiver].
 * - Le `WorkManager` périodique existant, gardé en filet de sécurité (retour utilisateur) : si
 *   l'alarme est un jour perdue (redémarrage du téléphone avant que l'app ne soit rouverte,
 *   alarmes système désactivées par l'utilisateur...), le widget se rattrape quand même, au plus
 *   tard dans la journée suivante.
 *
 * `UPDATE` (pas `KEEP`) pour le WorkManager, et replanification systématique de l'alarme : réancre
 * le calcul sur le prochain minuit à chaque appel (donc à chaque ouverture de l'app, cf.
 * MainActivity.onCreate) au lieu de rester figé sur le minuit qui suivait le tout premier
 * lancement — sans ça, un décalage pris une fois ne se rattrapait jamais.
 */
fun planifierRafraichissementWidgetMinuit(context: Context) {
    val delaiInitialMillis = delaiJusquauProchainMinuitMillis()

    val requete = PeriodicWorkRequestBuilder<RafraichissementWidgetMinuitWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(delaiInitialMillis, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(NOM_TRAVAIL_PERIODIQUE, ExistingPeriodicWorkPolicy.UPDATE, requete)

    planifierAlarmeProchainMinuit(context)
}

private fun delaiJusquauProchainMinuitMillis(): Long {
    val maintenant = LocalDateTime.now()
    val prochainMinuit = LocalDateTime.of(LocalDate.now().plusDays(1), java.time.LocalTime.MIDNIGHT)
    return Duration.between(maintenant, prochainMinuit).toMillis()
}

private fun planifierAlarmeProchainMinuit(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intentAlarme = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE_ALARME,
        Intent(context, AlarmeRafraichissementWidgetMinuitReceiver::class.java).setAction(ACTION_ALARME_MINUIT),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val declenchementMillis = System.currentTimeMillis() + delaiJusquauProchainMinuitMillis()
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, declenchementMillis, intentAlarme)
}

/**
 * Reçoit l'alarme de minuit : demande la mise à jour du widget puis se replanifie pour le minuit
 * suivant (retour utilisateur : `setAndAllowWhileIdle` est un déclenchement unique, pas répété).
 */
class AlarmeRafraichissementWidgetMinuitReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DefiQuotidienWidgetProvider.demanderMiseAJour(context.applicationContext)
        planifierAlarmeProchainMinuit(context.applicationContext)
    }
}

class RafraichissementWidgetMinuitWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        DefiQuotidienWidgetProvider.demanderMiseAJour(applicationContext)
        return Result.success()
    }
}
