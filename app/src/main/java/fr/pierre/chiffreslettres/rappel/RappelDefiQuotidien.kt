package fr.pierre.chiffreslettres.rappel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import fr.pierre.chiffreslettres.MainActivity
import fr.pierre.chiffreslettres.R
import fr.pierre.chiffreslettres.data.AppDatabaseProvider
import fr.pierre.chiffreslettres.data.DefiQuotidienRepository
import fr.pierre.chiffreslettres.data.ProfilRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

private const val CANAL_RAPPEL_ID = "rappel_defi_quotidien"
private const val NOTIFICATION_ID = 1
private const val NOM_TRAVAIL_PERIODIQUE = "rappel_defi_quotidien"

/** Heure du rappel quotidien (fixe, pas de réglage utilisateur pour l'instant). */
private val HEURE_RAPPEL: LocalTime = LocalTime.of(18, 0)

fun creerCanalNotificationRappel(context: Context) {
    val canal = NotificationChannel(
        CANAL_RAPPEL_ID,
        "Rappel du défi quotidien",
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = "Rappelle chaque jour les profils qui n'ont pas encore fait le défi quotidien."
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
}

/**
 * Planifie le rappel quotidien une bonne fois pour toutes (retour utilisateur : un rappel
 * groupé pour tous les profils du foyer, pas un par profil). [ExistingPeriodicWorkPolicy.KEEP]
 * pour ne pas repousser l'heure du prochain rappel à chaque lancement de l'app.
 */
fun planifierRappelQuotidien(context: Context) {
    val maintenant = LocalDateTime.now()
    var prochainDeclenchement = LocalDateTime.of(maintenant.toLocalDate(), HEURE_RAPPEL)
    if (!prochainDeclenchement.isAfter(maintenant)) prochainDeclenchement = prochainDeclenchement.plusDays(1)
    val delaiInitialMillis = Duration.between(maintenant, prochainDeclenchement).toMillis()

    val requete = PeriodicWorkRequestBuilder<RappelDefiQuotidienWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(delaiInitialMillis, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(NOM_TRAVAIL_PERIODIQUE, ExistingPeriodicWorkPolicy.KEEP, requete)
}

/**
 * Vérifie chaque jour, pour tous les profils, si le défi quotidien a déjà été joué, et
 * affiche une notification groupée listant ceux qui ne l'ont pas encore fait (retour
 * utilisateur : usage familial à plusieurs profils, pas de notif par profil).
 */
class RappelDefiQuotidienWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db = AppDatabaseProvider.obtenir(applicationContext)
        val profilRepository = ProfilRepository(db.profilDao())
        val defiQuotidienRepository = DefiQuotidienRepository(db.defiQuotidienDao())

        val profils = profilRepository.tousLesProfils().first()
        if (profils.isEmpty()) return Result.success()

        val jour = LocalDate.now().toString()
        val pseudosRestants = profils
            .filterNot { defiQuotidienRepository.reussiteDuJour(it.id, jour) }
            .map { it.pseudo }
        if (pseudosRestants.isEmpty()) return Result.success()

        afficherNotification(applicationContext, pseudosRestants)
        return Result.success()
    }

    private fun afficherNotification(context: Context, pseudosRestants: List<String>) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val intentOuverture = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val texte = "Défi pas encore joué aujourd'hui : ${pseudosRestants.joinToString(", ")}"
        val notification = NotificationCompat.Builder(context, CANAL_RAPPEL_ID)
            .setSmallIcon(R.drawable.ic_notification_rappel)
            .setContentTitle("Défi quotidien")
            .setContentText(texte)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texte))
            .setContentIntent(intentOuverture)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
