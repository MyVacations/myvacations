package es.myvacations.myvacations.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import io.github.aakira.napier.Napier
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
    private val getTripsUseCase: GetTripsUseCase
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

        getTotalTrips()
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
        _uiState.update {
            it.copy(
                greetings = getDayPeriod(),
                userName = "Jesus" // El nombre tambien por si se cambia de ajustes
            )
        }
    }

    fun getTotalTrips() {
        viewModelScope.launch {
            val trips = getTripsUseCase.invoke()
            _uiState.update {
                it.copy(
                    upcomingTrips = trips.filter { tripDomain ->
                        tripDomain.tripStatus == TripStatus.PLANNED
                    },
                    pastTrips = trips.filter { tripDomain ->
                        tripDomain.tripStatus == TripStatus.COMPLETE
                    },
                    stats = it.stats.copy(
                        totalTrips = trips.size,
                        totalSpent = trips.sumOf { trip -> trip.totalCost },
                        averageTripCost = trips.map { trip -> trip.totalCost }
                            .average().takeIf { trip -> !trip.isNaN() }
                            ?: 0.0,
                        upcomingTrips = trips.filter { tripDomain ->
                            tripDomain.tripStatus == TripStatus.PLANNED
                        }.size,
                        averageSavesFromBudget = trips.map { trip ->
                            trip.remainingBudget
                        }.average().takeIf { trip -> !trip.isNaN() }
                            ?: 0.0
                    )
                )
            }

            Napier.d(tag = "DashboardViewModel", message = "Total Trips: ${_uiState.value.stats.totalTrips}" +
                    "Total Spent: ${_uiState.value.stats.totalSpent}" +
                    "Average: ${_uiState.value.stats.averageTripCost}" +
                    "Upcoming: ${_uiState.value.stats.upcomingTrips}" +
                    "Average Saves: ${_uiState.value.stats.averageSavesFromBudget}" +
                    "Upcoming Trips: ${_uiState.value.upcomingTrips.size}" +
                    "Past Trips: ${_uiState.value.pastTrips.size}")
        }

    }
}