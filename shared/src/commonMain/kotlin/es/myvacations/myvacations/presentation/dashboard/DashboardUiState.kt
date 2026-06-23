package es.myvacations.myvacations.presentation.dashboard

import es.myvacations.myvacations.presentation.createtrip.TripUiState

data class DashboardUiState(
    val greetings: Greetings = Greetings.MORNING,
    val userName: String? = null,
    val currentTrip: TripUiState? = null,
    val stats: DashboardStats = DashboardStats(),
    val upcomingTrips: List<TripUiState> = listOf(),
    val pastTrips: List<TripUiState> = listOf()
)