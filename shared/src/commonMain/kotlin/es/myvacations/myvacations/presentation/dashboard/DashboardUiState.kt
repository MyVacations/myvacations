package es.myvacations.myvacations.presentation.dashboard

import es.myvacations.myvacations.domain.model.Greetings
import es.myvacations.myvacations.domain.model.Trip
import es.myvacations.myvacations.presentation.utils.DefaultTrip

data class DashboardUiState(
    val greetings: Greetings = Greetings.MORNING,
    val userName: String = "",
    val currentTrip: Trip? = DefaultTrip.tripActual,
    val stats: DashboardStats = DashboardStats(),
    val upcomingTrips: List<Trip> = listOf(DefaultTrip.tripUpcoming),
    val pastTrips: List<Trip> = listOf(DefaultTrip.tripPast)
)