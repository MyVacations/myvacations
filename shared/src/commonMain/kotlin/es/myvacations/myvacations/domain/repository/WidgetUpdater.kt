package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.presentation.chatbot.WidgetPlace
import es.myvacations.myvacations.presentation.createedittrip.TripUiState

interface WidgetUpdater {
    suspend fun updateTripWidget(trip: List<TripUiState>? = null)
    suspend fun updatePlacesWidget(widgetElement: WidgetPlace? = null)
    suspend fun updateLocationPermission(hasLocationPermission: Boolean)
    suspend fun updateLocationLoading()
    suspend fun updateLocationError()
    suspend fun noMessagesLoad()
    suspend fun noModelInstallOrUpdate()
    suspend fun outOfLimits()
}