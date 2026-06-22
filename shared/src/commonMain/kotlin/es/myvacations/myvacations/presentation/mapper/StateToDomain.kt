package es.myvacations.myvacations.presentation.mapper

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.presentation.createtrip.AddTripUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Clock

fun AddTripUiState.toDomainModel() = TripDomain(
    id = Random.nextInt().toString(),
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
    optionalExpenses = emptyList(),
    cover = cover
)