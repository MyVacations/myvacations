package es.myvacations.myvacations.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import es.myvacations.myvacations.presentation.utils.WidgetUtils
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshPlacesWidget

class LocationPermissionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val hasFineLocation =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val hasBackgroundLocation =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        if (!hasBackgroundLocation || !hasFineLocation) {
            return Result.success()
        }

        return if (refreshPlacesWidget()) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}