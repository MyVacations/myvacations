package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.locations.LocationDomain
import kotlinx.coroutines.flow.Flow

sealed interface LocationEventResult {
    data object Idle : LocationEventResult
    data class Success(val locationDomain: LocationDomain) : LocationEventResult
    data object PermissionDenied : LocationEventResult
    data object PermissionOk : LocationEventResult
    data class Error(val exception: Exception) : LocationEventResult
    data object LocationUnavailable : LocationEventResult
}

interface MapRepository {
    fun hasLocationPermission(): Boolean
    fun hasApproximateLocationPermission(): Boolean
    fun getLocation(): Flow<LocationEventResult>
    suspend fun getCurrentLocation(): LocationEventResult
}