package fr.pierre.chiffreslettres.widget

import android.content.Context
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

/**
 * Planifie un rafraîchissement du widget à minuit pile (retour utilisateur : le widget affiche
 * le statut du défi "du jour", qui change à minuit — la seule mise à jour périodique toutes les
 * 30 min n'est pas fiable pour ça, Android la retarde souvent de plusieurs heures en pratique).
 */
fun planifierRafraichissementWidgetMinuit(context: Context) {
    val maintenant = LocalDateTime.now()
    val prochainMinuit = LocalDateTime.of(LocalDate.now().plusDays(1), java.time.LocalTime.MIDNIGHT)
    val delaiInitialMillis = Duration.between(maintenant, prochainMinuit).toMillis()

    val requete = PeriodicWorkRequestBuilder<RafraichissementWidgetMinuitWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(delaiInitialMillis, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(NOM_TRAVAIL_PERIODIQUE, ExistingPeriodicWorkPolicy.KEEP, requete)
}

class RafraichissementWidgetMinuitWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        DefiQuotidienWidgetProvider.demanderMiseAJour(applicationContext)
        return Result.success()
    }
}
