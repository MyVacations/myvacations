package es.myvacations.myvacations.core.navigation

sealed interface ScreenDestination {
    val showNav: Boolean

    data object Dashboard : ScreenDestination {
        override val showNav = true
    }

    data object Trips : ScreenDestination {
        override val showNav = true
    }

    data object Statistics : ScreenDestination {
        override val showNav = true
    }

    data object Settings : ScreenDestination {
        override val showNav = true
    }

    data object AddTrip : ScreenDestination {
        override val showNav = false
    }

    data class TripDetail(val tripId: String) : ScreenDestination {
        override val showNav = false
    }
}