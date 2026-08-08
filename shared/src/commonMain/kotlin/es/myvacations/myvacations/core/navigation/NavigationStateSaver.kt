package es.myvacations.myvacations.core.navigation

import androidx.compose.runtime.saveable.Saver
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import kotlinx.serialization.json.Json

fun ScreenDestination.toSavedValue(): String =
    when (this) {
        ScreenDestination.Splash -> "splash"
        ScreenDestination.Onboarding -> "onboarding"
        ScreenDestination.Dashboard -> "dashboard"
        ScreenDestination.Trips -> "trips"
        ScreenDestination.Statistics -> "statistics"
        ScreenDestination.Settings -> "settings"
        ScreenDestination.ChatScreen -> "chatScreen"
        ScreenDestination.ShowPrivacyPolitic -> "showPrivacyPolitic"
        ScreenDestination.ShowHelpAndSupport -> "showHelpAndSupport"
        ScreenDestination.ShowNotifications -> "showNotifications"
        is ScreenDestination.AddEdit ->  "addEditTrip:${tripId}:${selectedExpenseFromWidget}"
        is ScreenDestination.TripDetail -> "tripDetail:$tripId"
    }

fun String.toScreenDestination(): ScreenDestination =
    when {
        this == "splash" -> ScreenDestination.Splash
        this == "onboarding" -> ScreenDestination.Onboarding
        this == "dashboard" -> ScreenDestination.Dashboard
        this == "trips" -> ScreenDestination.Trips
        this == "statistics" -> ScreenDestination.Statistics
        this == "settings" -> ScreenDestination.Settings
        this == "chatScreen" -> ScreenDestination.ChatScreen
        this == "showPrivacyPolitic" -> ScreenDestination.ShowPrivacyPolitic
        this == "showHelpAndSupport" -> ScreenDestination.ShowHelpAndSupport
        this == "showNotifications" -> ScreenDestination.ShowNotifications

        startsWith("addEditTrip:") -> {
            val parts = split(":")

            ScreenDestination.AddEdit(
                tripId = parts.getOrNull(1).orEmpty(),
                selectedExpenseFromWidget = parts
                    .getOrNull(2)
                    ?.toBooleanStrictOrNull()
                    ?: false
            )
        }

        startsWith("tripDetail:") ->
            ScreenDestination.TripDetail(
                removePrefix("tripDetail:")
            )

        else -> ScreenDestination.Splash
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