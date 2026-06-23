package es.myvacations.myvacations.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.extensions.roundTo2Decimals
import es.myvacations.myvacations.core.extensions.transformInInitials
import es.myvacations.myvacations.domain.mapper.calculateStatus
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.domain.usecase.userusecase.GetUserUseCase
import es.myvacations.myvacations.presentation.mapper.toUiCurrentTripState
import es.myvacations.myvacations.presentation.mapper.toUiPastTripState
import es.myvacations.myvacations.presentation.mapper.toUiState
import es.myvacations.myvacations.presentation.mapper.toUiStatsState
import es.myvacations.myvacations.presentation.mapper.toUiUpcomingTripState
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
    private val getDayPeriod: GetDayPeriodUseCase,
    private val getTripsUseCase: GetTripsUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                waitUntilNextPeriodChange()
                refreshGreetings()
            }
        }

        observeTrips()
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
            getUserUseCase().collect { userDomain ->
                _uiState.update {
                    it.copy(
                        greetings = getDayPeriod(),
                        userName = userDomain?.name
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
}