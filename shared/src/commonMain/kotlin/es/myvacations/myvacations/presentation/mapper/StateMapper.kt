package es.myvacations.myvacations.presentation.mapper

import es.myvacations.myvacations.core.extensions.roundTo2Decimals
import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.mapper.calculateStatus
import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.dashboard.DashboardStats
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import es.myvacations.myvacations.presentation.settings.SettingsUiState
import es.myvacations.myvacations.presentation.tripdetail.TravelerUiState
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun TripUiState.toDomainModel() = TripDomain(
    id = id,
    title = titleTrip,
    place = placeTrip,
    startDate = startDate
        ?: Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date,

    endDate = endDate
        ?: Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date,
    travelers = travelers,
    daysTraveling = daysTraveling,
    mainCost = mainCost,
    mainBudget = mainBudget,
    optionalExpenses = optionalExpenses.map { expensesDomain -> expensesDomain.toDomainModel() },
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
    optionalExpenses = optionalExpenses.map { expensesDomain -> expensesDomain.toUiState() },
)

fun List<TripDomain>.toUiStatsState() = DashboardStats(
    totalTrips = size,
    totalSpent = sumOf { trip -> trip.toUiState().mainCost },
    averageTripCost = map { trip -> trip.toUiState().mainCost }
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
        else -> 3
    }
}?.toUiState()

fun List<TripDomain>.toUiUpcomingTripState() = filter { tripDomain ->
    tripDomain.calculateStatus() == TripStatus.PLANNED
}.map { tripDomain -> tripDomain.toUiState() }

fun List<TripDomain>.toUiPastTripState() = filter { tripDomain ->
    tripDomain.calculateStatus() == TripStatus.COMPLETE
}.map { tripDomain -> tripDomain.toUiState() }

fun SettingsDomain.toUiSettingsState() = SettingsUiState(
    userName = username,
    currency = preferredCurrency
)

fun SettingsUiState.toDomainSettingsState() = SettingsDomain(
    username = userName,
    preferredCurrency = currency,
    welcomeShown = false
)

fun TripExpensesDomain.toUiState() = TripExpenseUiState(
    id = id,
    name = name,
    icon = icon,
    amount = amount
)

fun TripExpenseUiState.toDomainModel() = TripExpensesDomain(
    id = id,
    name = name,
    icon = icon,
    amount = amount
)

fun TravelersDomain.toUiState() = TravelerUiState(
    id = id,
    tripId = tripId,
    travelerName = travelerName,
    isMainTraveler = isMainTraveler
)

fun TravelerUiState.toDomainModel() = TravelersDomain(
    id = id,
    tripId = tripId,
    travelerName = travelerName,
    isMainTraveler = isMainTraveler
)

fun AppNotificationUiState.toDomainModel() = AppNotificationDomain(
    id = id,
    tripId = tripId,
    title = title,
    message = message,
    type = type,
    read = read,
    createdAt = createdAt
)

fun AppNotificationDomain.toUiState() = AppNotificationUiState(
    id = id,
    tripId = tripId,
    title = title,
    message = message,
    type = type,
    read = read,
    createdAt = createdAt
)