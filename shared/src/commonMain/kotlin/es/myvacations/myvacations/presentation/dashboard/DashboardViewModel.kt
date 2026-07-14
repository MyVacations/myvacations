package es.myvacations.myvacations.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.extensions.transformInInitials
import es.myvacations.myvacations.domain.events.messageFor
import es.myvacations.myvacations.domain.events.titleFor
import es.myvacations.myvacations.domain.mapper.toUiModel
import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.SelectAllNotificationsUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.presentation.mapper.toUiCurrentTripState
import es.myvacations.myvacations.presentation.mapper.toUiPastTripState
import es.myvacations.myvacations.presentation.mapper.toUiSettingsState
import es.myvacations.myvacations.presentation.mapper.toUiStatsState
import es.myvacations.myvacations.presentation.mapper.toUiUpcomingTripState
import es.myvacations.myvacations.presentation.settings.SettingsUiState
import es.myvacations.myvacations.presentation.utils.Currency
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
class DashboardViewModel(
    private val selectAllNotificationsUseCase: SelectAllNotificationsUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val getDayPeriod: GetDayPeriodUseCase,
    private val getTripsUseCase: GetTripsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            while (isActive) {
                waitUntilNextPeriodChange()
                refreshGreetings()
            }
        }

        observeTrips()
        observeSettings()
        observeNotifications()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            selectAllNotificationsUseCase.invoke().collect { notificationsDomain ->
                _uiState.update {
                    it.copy(notifications = notificationsDomain.map { notificationDomain ->
                        notificationDomain.toUiModel()
                    })
                }
            }
        }
    }

    private suspend fun waitUntilNextPeriodChange() {
        val zone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
            .toLocalDateTime(zone)

        val nextChange = when {
            now.hour <= 4 ->
                now.date.atTime(5, 0)

            now.hour <= 11 ->
                now.date.atTime(12, 0)

            now.hour <= 17 ->
                now.date.atTime(18, 0)

            now.hour <= 21 ->
                now.date.atTime(22, 0)

            else ->
                now.date
                    .plus(DatePeriod(days = 1))
                    .atTime(5, 0)
        }

        val millis = nextChange
            .toInstant(zone)
            .minus(now.toInstant(zone))
            .inWholeMilliseconds

        delay(millis.milliseconds)
    }

    fun refreshGreetings() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settingsDomain ->
                _uiState.update {
                    it.copy(
                        greetings = getDayPeriod(),
                        settings = settingsDomain?.toUiSettingsState() ?: SettingsUiState()
                    )
                }
            }
        }
    }

    fun initials(userName: String) = userName.transformInInitials()

    fun observeTrips() {
        viewModelScope.launch {
            getTripsUseCase.invoke().collect { tripsDomain ->
                _uiState.update {
                    it.copy(
                        currentTrip = tripsDomain.toUiCurrentTripState(),
                        upcomingTrips = tripsDomain.toUiUpcomingTripState(),
                        pastTrips = tripsDomain.toUiPastTripState(),
                        stats = tripsDomain.toUiStatsState()
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            val startTime = Clock.System.now().toEpochMilliseconds()

            getSettingsUseCase.invoke().collect { settingsDomain ->
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime

                val remaining = 1000 - elapsed

                if (remaining > 0) {
                    delay(remaining.milliseconds)
                }

                _uiState.update {
                    it.copy(
                        settings = settingsDomain?.toUiSettingsState() ?: SettingsUiState(),
                        upcomingTrips = _uiState.value.upcomingTrips.map { trip ->
                            trip.copy(
                                currency = settingsDomain?.preferredCurrency ?: Currency.EURO
                            )
                        },
                        pastTrips = _uiState.value.pastTrips.map { trip ->
                            trip.copy(
                                currency = settingsDomain?.preferredCurrency ?: Currency.EURO
                            )
                        },
                        isLoading = false
                    )
                }
            }
        }
    }
}