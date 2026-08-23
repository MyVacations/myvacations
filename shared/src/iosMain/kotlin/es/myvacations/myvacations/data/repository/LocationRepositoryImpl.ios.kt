package es.myvacations.myvacations.data.repository

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.data.datasource.PlaceType
import es.myvacations.myvacations.domain.model.locations.PlaceDomain
import es.myvacations.myvacations.domain.repository.LocationRepository

actual class LocationRepositoryImpl actual constructor() :
    LocationRepository {
    actual override suspend fun getPlacesFromLocation(place: PlaceType): List<PlaceDomain> {
        //Not used
        return listOf()
    }

}

@Composable
actual fun LocationPermissionHandler(
    onUpdatePermission: (LocationEventResult) -> Unit,
    dialogRequestingLocalPermissions: Boolean
) {
    //Not used
}