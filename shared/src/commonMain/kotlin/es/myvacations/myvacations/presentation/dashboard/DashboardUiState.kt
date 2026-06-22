package es.myvacations.myvacations.presentation.dashboard

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.presentation.utils.DefaultTrip
import kotlinx.datetime.daysUntil

data class DashboardUiState(
    val greetings: Greetings = Greetings.MORNING,
    val userName: String = "",
    val currentTrip: TripDomain? = DefaultTrip.tripActual,
    val stats: DashboardStats = DashboardStats(),
    val upcomingTrips: List<TripDomain> = listOf(),
    val pastTrips: List<TripDomain> = listOf()
)