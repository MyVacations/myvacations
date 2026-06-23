package es.myvacations.myvacations.presentation.createtrip

import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.model.TripStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class TripUiState(
    val id : String = "",
    val titleTrip: String = "",
    val placeTrip: Country = Country.SPAIN,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val daysTraveling: Int = 1,
    val travelers: Int = 1,
    val mainCost: Double = 0.0,
    val mainBudget: Double = 0.0,
    val cover: TripCover = TripCover.BARCELONA,
    val optionalExpensesExpanded: Boolean = false,
    val optionalExpenses: List<TripExpensesDomain> = emptyList(),
    val editMode: Boolean = false
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val remainingDays: Int
        get() = today.daysUntil(startDate ?: today)

    val daysPassed: Int
        get() = endDate?.daysUntil(today) ?: 0

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
        get() = startDate?.daysUntil(endDate ?: today) ?: 0

    val tripStatus: TripStatus
        get() = when {
            today < (startDate ?: today) -> TripStatus.PLANNED
            today > (endDate ?: today) -> TripStatus.COMPLETE
            else -> TripStatus.ACTIVE
        }
}
