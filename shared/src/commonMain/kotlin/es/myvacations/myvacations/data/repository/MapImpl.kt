package es.myvacations.myvacations.data.repository

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow

expect class MapImpl() : MapRepository {
    override fun hasLocationPermission(): Boolean
    override fun hasApproximateLocationPermission(): Boolean
    override fun getLocation(): Flow<LocationEventResult>
    override suspend fun getCurrentLocation(): LocationEventResult
}

@Composable
expect fun LocationPermissionHandler(
    onUpdatePermission: (LocationEventResult) -> Unit,
    dialogRequestingLocationPermissions: Boolean
)