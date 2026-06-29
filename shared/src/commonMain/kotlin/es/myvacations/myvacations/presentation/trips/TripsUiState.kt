package es.myvacations.myvacations.presentation.trips

import es.myvacations.myvacations.presentation.createedittrip.TripUiState

data class TripsUiState(
    val trips: List<TripUiState> = emptyList(),
    val isLoading: Boolean = true
)