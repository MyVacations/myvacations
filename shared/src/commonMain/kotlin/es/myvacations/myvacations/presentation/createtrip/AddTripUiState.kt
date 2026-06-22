package es.myvacations.myvacations.presentation.createtrip

import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

data class AddTripUiState(
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
    val optionalExpenses: List<TripExpensesDomain> = emptyList())
{
    val totalDays: Int
        get() {
            if (startDate == null || endDate == null) {
                return 0
            }

            return startDate.daysUntil(endDate) + 1
        }
    val totalCost: Double
        get() {
            return mainCost + optionalExpenses.sumOf { it.amount }
        }
}
