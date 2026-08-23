package es.myvacations.myvacations.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshPlacesWidget

class LocationPermissionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val hasPermission =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val preferences =
            applicationContext.getSharedPreferences(
                "places_widget",
                Context.MODE_PRIVATE
            )

        val previousPermission =
            preferences.getBoolean(
                "location_permission",
                false
            )

        if (previousPermission != hasPermission) {

            preferences.edit {
                putBoolean(
                    "location_permission",
                    hasPermission
                )
            }

            refreshPlacesWidget()
        }

        return Result.success()
    }
}