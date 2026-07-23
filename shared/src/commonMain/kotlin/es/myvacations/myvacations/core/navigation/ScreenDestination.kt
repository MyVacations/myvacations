package es.myvacations.myvacations.core.navigation


sealed interface ScreenDestination {
    val showBottomBarUi: Boolean
    val showFloatingButton: Boolean

    data object Splash : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }

    data object Dashboard : ScreenDestination {
        override val showBottomBarUi: Boolean = true
        override val showFloatingButton = true
    }

    data object Trips : ScreenDestination {
        override val showBottomBarUi: Boolean = true
        override val showFloatingButton = true
    }

    data object Statistics : ScreenDestination {
        override val showBottomBarUi: Boolean = true
        override val showFloatingButton = true
    }

    data object Settings : ScreenDestination {
        override val showBottomBarUi: Boolean = true
        override val showFloatingButton = false
    }

    data object ShowPrivacyPolitic : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }

    data object ShowHelpAndSupport : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }
    data object ShowNotifications : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }

    data class AddEdit(val tripId: String = "") : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }

    data class TripDetail(val tripId: String) : ScreenDestination {
        override val showBottomBarUi: Boolean = false
        override val showFloatingButton = false
    }
}