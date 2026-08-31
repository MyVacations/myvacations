package es.myvacations.myvacations.presentation.dashboard

import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import es.myvacations.myvacations.presentation.settings.SettingsUiState

data class DashboardUiState(
    val userInfo: UserInfo = UserInfo(),
    val greetings: Greetings = Greetings.MORNING,
    val settings: SettingsUiState = SettingsUiState(),
    val currentTrip: TripUiState? = null,
    val stats: DashboardStats = DashboardStats(),
    val upcomingTrips: List<TripUiState> = listOf(),
    val pastTrips: List<TripUiState> = listOf(),
    val isLoading: Boolean = false,
    val notifications: List<AppNotificationUiState> = emptyList()
)

data class UserInfo(
    val userName: String = "",
    val userPhoto: String? = null,
    val userEmail: String? = null,
    val isLoggedIn: Boolean = false
)