package es.myvacations.myvacations.presentation.trips

import es.myvacations.myvacations.presentation.createtrip.TripUiState

data class TripsUiState(
    val trips: List<TripUiState> = emptyList(),
)