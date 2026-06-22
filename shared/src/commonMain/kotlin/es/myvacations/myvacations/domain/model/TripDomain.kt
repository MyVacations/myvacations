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
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val remainingDays: Int
        get() = today.daysUntil(startDate)

    val daysPassed: Int
        get() = endDate.daysUntil(today)

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

    val tripStatus: TripStatus
        get() = when {
            today < startDate -> TripStatus.PLANNED
            today > endDate -> TripStatus.COMPLETE
            else -> TripStatus.ACTIVE
        }
}