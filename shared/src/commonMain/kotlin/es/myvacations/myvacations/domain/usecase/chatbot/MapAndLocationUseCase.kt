package es.myvacations.myvacations.domain.usecase.chatbot

import es.myvacations.myvacations.domain.repository.MapRepository

class MapAndLocationUseCase(
    val locationRepository: MapRepository
) {
    fun hasLocationPermissions() = locationRepository.hasLocationPermission()
    fun hasApproximateLocationPermission() =
        locationRepository.hasApproximateLocationPermission()

    operator fun invoke() = locationRepository.getLocation()
    suspend fun getCurrentLocation() = locationRepository.getCurrentLocation()
}