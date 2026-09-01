package es.myvacations.myvacations.data.repository

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.model.locations.LocationDomain
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.MapRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class MapImpl actual constructor() : MapRepository {
    val context = AndroidContextHolder.context
    actual override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PERMISSION_GRANTED
    }

    actual override fun hasApproximateLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    actual override fun getLocation(): Flow<LocationEventResult> = callbackFlow {

        if (!hasLocationPermission()) {
            trySend(LocationEventResult.PermissionDenied)
            close()
            return@callbackFlow
        }

        val client =
            LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L
        )
            .setMinUpdateIntervalMillis(5_000L)
            .build()

        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                result: LocationResult
            ) {
                val location = result.lastLocation

                if (location != null) {
                    trySend(
                        LocationEventResult.Success(
                            LocationDomain(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                radiusMeters = 5000
                            )
                        )
                    )
                } else {
                    trySend(
                        LocationEventResult.LocationUnavailable
                    )
                }
            }
        }

        try {
            client.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            trySend(
                LocationEventResult.Error(e)
            )
        }

        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    actual override suspend fun getCurrentLocation(): LocationEventResult {
        if (!hasLocationPermission()) {
            return LocationEventResult.PermissionDenied
        }

        val client =
            LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { continuation ->

            val cancellationTokenSource = CancellationTokenSource()

            client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { location ->

                    if (!continuation.isActive) return@addOnSuccessListener

                    if (location != null) {
                        continuation.resume(
                            LocationEventResult.Success(
                                LocationDomain(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    radiusMeters = 5000
                                )
                            )
                        )
                    } else {
                        continuation.resume(
                            LocationEventResult.LocationUnavailable
                        )
                    }
                }
                .addOnFailureListener { error ->

                    if (!continuation.isActive) return@addOnFailureListener

                    continuation.resume(
                        LocationEventResult.Error(error)
                    )
                }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}

@Composable
actual fun LocationPermissionHandler(
    onUpdatePermission: (LocationEventResult) -> Unit,
    dialogRequestingLocationPermissions: Boolean
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onUpdatePermission(if (fineGranted || coarseGranted) LocationEventResult.PermissionOk else LocationEventResult.PermissionDenied)
    }

    LaunchedEffect(dialogRequestingLocationPermissions) {
        if (dialogRequestingLocationPermissions) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}