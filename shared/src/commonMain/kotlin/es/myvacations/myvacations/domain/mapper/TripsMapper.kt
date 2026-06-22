package es.myvacations.myvacations.domain.mapper

import es.myvacations.myvacations.data.database.TripData
import es.myvacations.myvacations.data.database.TripExpenseData
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.presentation.utils.TravelIcon
import kotlinx.datetime.LocalDate


fun TripData.toDomainModel() = TripDomain(
    id = id,
    title = title,
    place = Country.valueOf(place),
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    travelers = travelers.toInt(),
    daysTraveling = daysTraveling.toInt(),
    mainCost = mainCost,
    mainBudget = mainBudget,
    optionalExpenses = emptyList(),
    cover = TripCover.valueOf(cover)
)

fun TripExpenseData.toDomainModel() = TripExpensesDomain(
    id = id,
    name = nameExpense,
    icon = TravelIcon.valueOf(icon),
    amount = amount
)
