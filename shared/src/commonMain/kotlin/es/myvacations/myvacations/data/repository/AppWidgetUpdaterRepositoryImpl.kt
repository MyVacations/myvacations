package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.chatbot.WidgetPlace
import es.myvacations.myvacations.presentation.createedittrip.TripUiState

expect class AppWidgetUpdaterRepositoryImpl(): WidgetUpdater {
    override suspend fun updateTripWidget(trip: List<TripUiState>?)
    override suspend fun updatePlacesWidget(widgetElement: WidgetPlace?)
    override suspend fun updateLocationPermission(hasLocationPermission: Boolean)
    override suspend fun updateLocationLoading()
    override suspend fun updateLocationError()
    override suspend fun noMessagesLoad()
    override suspend fun noModelInstallOrUpdate()
}