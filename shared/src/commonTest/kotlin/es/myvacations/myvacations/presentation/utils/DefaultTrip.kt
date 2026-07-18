package es.myvacations.myvacations.presentation.utils

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import kotlinx.datetime.LocalDate

object DefaultTrip {
    val tripActual = TripDomain(
        id = "1",
        title = "My Vacation",
        place = Country.SPAIN,
        startDate = LocalDate(2026, 7, 18),
        endDate = LocalDate(2026, 7, 25),
        mainCost = 100.0,
        mainBudget = 1000.0,
        optionalExpenses = emptyList(),
        cover = TripCover.BARCELONA,
        favourite = false
    )
}
