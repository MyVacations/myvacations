package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.createedittrip.TripUiState

actual class AppWidgetUpdaterRepositoryImpl :
    WidgetUpdater {
    actual override suspend fun update(trip: List<TripUiState>?) {
       //Not used
    }

}