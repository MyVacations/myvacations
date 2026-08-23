package es.myvacations.myvacations.domain.model

import kotlinx.datetime.LocalDate

data class TripDomain(
    val id: String,
    val title: String,
    val place: Country,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val mainCost: Double,
    val mainBudget: Double,
    val optionalExpenses: List<TripExpensesDomain>,
    val cover: TripCover,
    val favourite: Boolean
)