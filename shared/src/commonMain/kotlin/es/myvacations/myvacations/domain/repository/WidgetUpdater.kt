package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.presentation.createedittrip.TripUiState

interface WidgetUpdater {
    suspend fun update(trip: List<TripUiState>? = null)
}