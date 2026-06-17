package es.myvacations.myvacations.core.navigation

sealed interface ScreenDestination {
    data object Dashboard : ScreenDestination
    data object Trips : ScreenDestination
    data object Statistics : ScreenDestination
    data object Settings : ScreenDestination
    data object TripAdd : ScreenDestination
    data class TripDetail(val tripId: String) : ScreenDestination
}