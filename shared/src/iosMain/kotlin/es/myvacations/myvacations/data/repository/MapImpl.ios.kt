package es.myvacations.myvacations.data.repository

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow

actual class MapImpl actual constructor() :
    MapRepository {
    actual override fun hasLocationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun hasApproximateLocationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun getLocation(): Flow<LocationEventResult> {
        TODO("Not yet implemented")
    }

    actual override suspend fun getCurrentLocation(): LocationEventResult {
        TODO("Not yet implemented")
    }
}

@Composable
actual fun LocationPermissionHandler(
    onUpdatePermission: (LocationEventResult) -> Unit,
    dialogRequestingLocationPermissions: Boolean
) {

}