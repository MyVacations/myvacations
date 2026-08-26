package es.myvacations.myvacations.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WidgetPlacesScheduler {
    private const val WORK_NAME = "places_widget_permission_check"
    fun schedule(context: Context) {
        val request =
            PeriodicWorkRequestBuilder<LocationPermissionWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
    }

    fun refreshNow(context: Context) {
        val request =
            OneTimeWorkRequestBuilder<LocationPermissionWorker>()
                .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        schedule(context)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }

}