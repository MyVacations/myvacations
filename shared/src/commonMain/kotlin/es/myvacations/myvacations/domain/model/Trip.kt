package es.myvacations.myvacations.domain.model

import es.myvacations.myvacations.presentation.trips.TripStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

data class Trip(
    val id: String,
    val title: String,
    val tripStatus: TripStatus,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val travelers: Int,
    val mainCost: Double,
    val mainBudget: Double,
    val optionalExpenses: List<TripExpenses>,
    val notes: String? = null,
    val cover: TripCover
) {
    val totalCost: Double
        get() = mainCost + optionalExpenses.sumOf { it.amount }

    val remainingBudget: Double
        get() = mainBudget - totalCost

    val costPerPerson: Double
        get() = if (travelers > 0) totalCost / travelers else 0.0

    val costPerDay: Double
        get() = if (totalDays > 0) totalCost / totalDays else 0.0

    val costPerDayEachPerson: Double
        get() = if (travelers > 0) costPerDay / travelers else 0.0

    val totalDays: Int
        get() = startDate.daysUntil(endDate)
}