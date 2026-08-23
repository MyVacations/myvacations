package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.chatbot.WidgetPlace
import es.myvacations.myvacations.presentation.createedittrip.TripUiState

actual class AppWidgetUpdaterRepositoryImpl :
    WidgetUpdater {
    actual override suspend fun updateTripWidget(trip: List<TripUiState>?) {
       //Not used
    }

    actual override suspend fun updatePlacesWidget(widgetElement: WidgetPlace?) {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateLocationPermission(hasLocationPermission: Boolean) {
        TODO("Not yet implemented")
    }


    actual override suspend fun updateLocationLoading() {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateLocationError() {
        TODO("Not yet implemented")
    }

    actual override suspend fun noMessagesLoad() {
        TODO("Not yet implemented")
    }

}