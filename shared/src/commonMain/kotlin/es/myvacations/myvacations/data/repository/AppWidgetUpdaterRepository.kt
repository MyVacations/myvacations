package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.createedittrip.TripUiState

expect class AppWidgetUpdaterRepository(): WidgetUpdater {
    override suspend fun update(trip: List<TripUiState>?)
}