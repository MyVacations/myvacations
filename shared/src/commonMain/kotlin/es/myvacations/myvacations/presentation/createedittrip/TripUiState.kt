package es.myvacations.myvacations.presentation.createedittrip

import es.myvacations.myvacations.core.extensions.roundTo2Decimals
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import es.myvacations.myvacations.presentation.utils.calendar.CalendarUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class TripUiState(
    val errorInScreen: Boolean = false,
    val id: String = "",
    val titleTrip: String = "",
    val placeTrip: Country = Country.SPAIN,
    val startDate: LocalDate = CalendarUiState().selectedDate,
    val errorStartDate: Boolean = false,
    val endDate: LocalDate = CalendarUiState().selectedDate,
    val travelers: Int = 1,
    val mainCost: Double = 0.0,
    val mainBudget: Double = 0.0,
    val cover: TripCover = TripCover.BARCELONA,
    val optionalExpensesExpanded: Boolean = true,
    val optionalExpensesErrorAmount: Boolean = false,
    val optionalExpenses: List<TripExpenseUiState> = emptyList(),
    val editMode: Boolean = false,
    val currency: Currency = Currency.EURO,
    val isLoading: Boolean = false,
    val favourite: Boolean = false
) {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val remainingDays: Int
        get() = today.daysUntil(startDate ?: today)

    val daysPassed: Int
        get() = endDate.daysUntil(today) ?: 0

    val totalOptionalExpenses: Double
        get() = optionalExpenses.sumOf { it.amount }

    val remainingBudget: Double
        get() = (mainBudget - totalOptionalExpenses)

    val costPerPerson: Double
        get() = (if (travelers > 0) mainCost / travelers else 0.0).roundTo2Decimals()

    val individualCost: Double
        get() = costPerPerson + totalOptionalExpenses

    val lowBudget: Double
        get() = (100 - ((totalOptionalExpenses * 100)  / mainBudget))

    val totalDays: Int
        get() = startDate.daysUntil(endDate)

    val tripStatus: TripStatus
        get() = when {
            today < (startDate) -> TripStatus.PLANNED
            today > (endDate) -> TripStatus.COMPLETE
            else -> TripStatus.ACTIVE
        }
}