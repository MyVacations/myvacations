package es.myvacations.myvacations.domain.mapper

import es.myvacations.myvacations.data.database.TripData
import es.myvacations.myvacations.data.database.TripExpenseData
import es.myvacations.myvacations.data.database.TripTravelersData
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.utils.TravelIcon
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


fun TripData.toDomainModel() = TripDomain(
    id = id,
    title = title,
    place = Country.valueOf(place),
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    mainCost = mainCost,
    mainBudget = mainBudget,
    optionalExpenses = emptyList(),
    cover = TripCover.valueOf(cover),
    favourite = favourite
)

fun TripExpenseData.toDomainModel() = TripExpensesDomain(
    id = id,
    name = nameExpense,
    icon = TravelIcon.valueOf(icon),
    amount = amount
)

fun TripDomain.calculateStatus(): TripStatus {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return when {
        today < startDate -> TripStatus.PLANNED
        today > endDate -> TripStatus.COMPLETE
        else -> TripStatus.ACTIVE
    }
}

fun TripTravelersData.toDomainModel() = TravelersDomain(
    id = id,
    tripId = tripId,
    travelerName = travelerName,
    isMainTraveler = mainUser
)