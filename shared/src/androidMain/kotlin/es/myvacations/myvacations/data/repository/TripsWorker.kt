package es.myvacations.myvacations.data.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshTripsWidget

class TripsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return if (refreshTripsWidget()) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}