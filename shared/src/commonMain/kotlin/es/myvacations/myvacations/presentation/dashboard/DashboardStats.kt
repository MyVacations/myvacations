package es.myvacations.myvacations.presentation.dashboard

data class DashboardStats(
    val totalTrips: Int = 0,
    val totalSpent: Double = 0.0,
    val averageTripCost: Double = 0.0,
    val upcomingTrips: Int = 0
)