package es.myvacations.myvacations.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class TripDomain(
    val id: String,
    val title: String,
    val place: Country,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val travelers: Int,
    val daysTraveling: Int,
    val mainCost: Double,
    val mainBudget: Double,
    val optionalExpenses: List<TripExpensesDomain>,
    val cover: TripCover
)