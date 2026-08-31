package es.myvacations.myvacations.presentation.mapper

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.core.extensions.roundTo2Decimals
import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.mapper.calculateStatus
import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.repository.AIRepository
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.dashboard.DashboardStats
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import es.myvacations.myvacations.presentation.settings.SettingsUiState
import es.myvacations.myvacations.presentation.tripdetail.TravelerUiState
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.analytics_attraction
import myvacations.shared.generated.resources.analytics_bar
import myvacations.shared.generated.resources.analytics_cafe
import myvacations.shared.generated.resources.analytics_cinema
import myvacations.shared.generated.resources.analytics_fast_food
import myvacations.shared.generated.resources.analytics_fuel
import myvacations.shared.generated.resources.analytics_gallery
import myvacations.shared.generated.resources.analytics_garden
import myvacations.shared.generated.resources.analytics_hospital
import myvacations.shared.generated.resources.analytics_mall
import myvacations.shared.generated.resources.analytics_museum
import myvacations.shared.generated.resources.analytics_none
import myvacations.shared.generated.resources.analytics_park
import myvacations.shared.generated.resources.analytics_pharmacy
import myvacations.shared.generated.resources.analytics_pub
import myvacations.shared.generated.resources.analytics_restaurant
import myvacations.shared.generated.resources.analytics_supermarket
import myvacations.shared.generated.resources.analytics_theatre
import myvacations.shared.generated.resources.analytics_viewpoint
import myvacations.shared.generated.resources.email_reset_send
import myvacations.shared.generated.resources.error_account_already
import myvacations.shared.generated.resources.error_email_reset_send
import myvacations.shared.generated.resources.error_generic_loging
import myvacations.shared.generated.resources.error_nologing
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

fun TripUiState.toDomainModel() = TripDomain(
    id = id,
    title = titleTrip,
    place = placeTrip,
    startDate = startDate,
    endDate = endDate,
    mainCost = mainCost,
    mainBudget = mainBudget,
    optionalExpenses = optionalExpenses.map { expensesDomain -> expensesDomain.toDomainModel() },
    cover = cover,
    favourite = favourite
)

fun TripDomain.toUiState() = TripUiState(
    id = id,
    titleTrip = title,
    placeTrip = place,
    startDate = startDate,
    endDate = endDate,
    mainCost = mainCost,
    mainBudget = mainBudget,
    cover = cover,
    optionalExpenses = optionalExpenses.map { expensesDomain -> expensesDomain.toUiState() },
    favourite = favourite
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
    welcomeShow = false,
    iaTutorial = false,
    firstLogin = false,
    modelStage = null
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

fun LocalDateTime.toLocalUserString(): String {
    return "$day - ${month.number} - $year, " +
            "${hour.toString().padStart(2, '0')}:" +
            minute.toString().padStart(2, '0')
}

@Composable
fun AIRepository.toNameLabel() = this.load().idToLabel.map { label ->
    when (label) {
        "ATTRACTION" -> stringResource(Res.string.analytics_attraction)
        "BAR" -> stringResource(Res.string.analytics_bar)
        "CAFE" -> stringResource(Res.string.analytics_cafe)
        "CINEMA" -> stringResource(Res.string.analytics_cinema)
        "FAST_FOOD" -> stringResource(Res.string.analytics_fast_food)
        "FUEL" -> stringResource(Res.string.analytics_fuel)
        "GALLERY" -> stringResource(Res.string.analytics_gallery)
        "GARDEN" -> stringResource(Res.string.analytics_garden)
        "HOSPITAL" -> stringResource(Res.string.analytics_hospital)
        "MALL" -> stringResource(Res.string.analytics_mall)
        "MUSEUM" -> stringResource(Res.string.analytics_museum)
        "NONE" -> stringResource(Res.string.analytics_none)
        "PARK" -> stringResource(Res.string.analytics_park)
        "PHARMACY" -> stringResource(Res.string.analytics_pharmacy)
        "PUB" -> stringResource(Res.string.analytics_pub)
        "RESTAURANT" -> stringResource(Res.string.analytics_restaurant)
        "SUPERMARKET" -> stringResource(Res.string.analytics_supermarket)
        "THEATRE" -> stringResource(Res.string.analytics_theatre)
        "VIEWPOINT" -> stringResource(Res.string.analytics_viewpoint)
        else -> stringResource(Res.string.analytics_none)
    }
}

suspend fun String.toUserMessage() = when (this) {
    "The supplied auth credential is incorrect, malformed or has expired." -> getString(Res.string.error_nologing)
    "The email address is already in use by another account." -> getString(Res.string.error_account_already)
    "Enviado" ->  getString(Res.string.email_reset_send)
    "The email address is badly formatted." -> getString(Res.string.error_email_reset_send)
    else -> getString(Res.string.error_generic_loging)
}