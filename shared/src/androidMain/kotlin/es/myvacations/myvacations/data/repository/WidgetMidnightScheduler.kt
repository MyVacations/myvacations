package es.myvacations.myvacations.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

enum class KindOfWidget {
    TRIPS,
    PLACES
}

object WidgetMidnightScheduler {
    private const val WORK_NAME = "work_name_midnight"
    fun schedule(context: Context, kindOfWidget: KindOfWidget) {
        val request =
            if (kindOfWidget == KindOfWidget.TRIPS) {

                PeriodicWorkRequestBuilder<TripsWorker>(
                    1,
                    TimeUnit.DAYS
                ).build()

            } else {

                PeriodicWorkRequestBuilder<LocationPermissionWorker>(
                    1,
                    TimeUnit.DAYS
                ).build()
            }

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }
}