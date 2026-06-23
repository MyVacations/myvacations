package es.myvacations.myvacations.presentation.mapper

import es.myvacations.myvacations.core.extensions.roundTo2Decimals
import es.myvacations.myvacations.domain.mapper.calculateStatus
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.createtrip.TripUiState
import es.myvacations.myvacations.presentation.dashboard.DashboardStats
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun TripUiState.toDomainModel() = TripDomain(
    id = id,
    title = titleTrip,
    place = placeTrip,
    startDate = (startDate ?: Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())) as LocalDate,
    endDate = (endDate ?: Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())) as LocalDate,
    travelers = travelers,
    daysTraveling = daysTraveling,
    mainCost = mainCost,
    mainBudget = mainBudget,
    optionalExpenses = optionalExpenses,
    cover = cover
)

fun TripDomain.toUiState() = TripUiState(
    id = id,
    titleTrip = title,
    placeTrip = place,
    startDate = startDate,
    endDate = endDate,
    daysTraveling = daysTraveling,
    travelers = travelers,
    mainCost = mainCost,
    mainBudget = mainBudget,
    cover = cover,
    optionalExpenses = optionalExpenses,
)

fun List<TripDomain>.toUiStatsState() = DashboardStats(
    totalTrips = size,
    totalSpent = sumOf { trip -> trip.toUiState().totalCost },
    averageTripCost = map { trip -> trip.toUiState().totalCost }
        .average().takeIf { trip -> !trip.isNaN() }
        ?.roundTo2Decimals()
        ?: 0.0,
    upcomingTrips = filter { tripDomain ->
        tripDomain.calculateStatus() == TripStatus.PLANNED
    }.size,
    averageSavesFromBudget = filter { tripDomain -> tripDomain.toUiState().remainingBudget > 0 }
        .map { tripDomain -> tripDomain.toUiState().remainingBudget }
        .average()
        .takeIf { tripDomain -> !tripDomain.isNaN() }
        ?.roundTo2Decimals()
        ?: 0.0

)

fun List<TripDomain>.toUiCurrentTripState() = minByOrNull { trip ->
    when (trip.calculateStatus()) {
        TripStatus.ACTIVE -> 0
        TripStatus.PLANNED -> 1
        TripStatus.COMPLETE -> 2
    }
}?.toUiState()

fun List<TripDomain>.toUiUpcomingTripState() = filter { tripDomain ->
    tripDomain.calculateStatus() == TripStatus.PLANNED
}.map { tripDomain -> tripDomain.toUiState() }

fun List<TripDomain>.toUiPastTripState() = filter { tripDomain ->
    tripDomain.calculateStatus() == TripStatus.COMPLETE
}.map { tripDomain -> tripDomain.toUiState() }