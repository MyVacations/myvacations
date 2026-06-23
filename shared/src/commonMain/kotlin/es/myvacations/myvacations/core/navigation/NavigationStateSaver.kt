package es.myvacations.myvacations.core.navigation

import androidx.compose.runtime.saveable.Saver

fun ScreenDestination.toSavedValue(): String =
    when (this) {
        ScreenDestination.Dashboard -> "dashboard"
        ScreenDestination.Trips -> "trips"
        ScreenDestination.Statistics -> "statistics"
        ScreenDestination.Settings -> "settings"
        is ScreenDestination.AddEdit -> "addEditTrip:$tripId"
        is ScreenDestination.TripDetail -> "tripDetail:$tripId"
    }

fun String.toScreenDestination(): ScreenDestination =
    when {
        this == "dashboard" -> ScreenDestination.Dashboard
        this == "trips" -> ScreenDestination.Trips
        this == "statistics" -> ScreenDestination.Statistics
        this == "settings" -> ScreenDestination.Settings
        startsWith("addEditTrip:") -> ScreenDestination.AddEdit(
            removePrefix("addEditTrip:")
        )

        startsWith("tripDetail:") ->
            ScreenDestination.TripDetail(
                removePrefix("tripDetail:")
            )

        else -> ScreenDestination.Dashboard
    }

val NavigationStateSaver = Saver<NavigationState, List<String>>(
    save = { navigationState ->
        buildList {
            add(
                navigationState.currentScreen.toSavedValue()
            )

            addAll(
                navigationState.getBackStack()
                    .map { it.toSavedValue() }
            )
        }
    },
    restore = { saved ->
        NavigationState(
            initialScreen = saved.first().toScreenDestination(),
            initialBackStack = saved.drop(1)
                .map { it.toScreenDestination() }
        )
    }
)