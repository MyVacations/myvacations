package es.myvacations.myvacations.presentation.statistics

import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.utils.Currency
import kotlinx.datetime.daysUntil

data class StatisticsUiState(
    val trips: List<TripUiState> = emptyList(),
    val currency: Currency = Currency.EURO,
    val isLoading: Boolean = true
) {
    private fun filteredTrips(tripStatus: TripStatus): List<TripUiState> =
        when (tripStatus) {
            TripStatus.ALL -> trips
            TripStatus.PLANNED -> trips.filter { it.tripStatus == TripStatus.PLANNED }
            TripStatus.ACTIVE -> trips.filter { it.tripStatus == TripStatus.ACTIVE }
            TripStatus.COMPLETE -> trips.filter { it.tripStatus == TripStatus.COMPLETE }
        }

    fun tripsTaken(tripStatus: TripStatus) = filteredTrips(tripStatus)

    fun tripsTakenNumber(tripStatus: TripStatus) = filteredTrips(tripStatus).size

    fun tripsTotalSpent(tripStatus: TripStatus) = filteredTrips(tripStatus).sumOf { it.mainCost }

    fun tripsDaysDuration(tripStatus: TripStatus) = filteredTrips(tripStatus).sumOf {
        it.endDate?.let { other -> it.startDate?.daysUntil(other) }
            ?: 0
    }

    fun tripsCountriesInt(tripStatus: TripStatus) =
        filteredTrips(tripStatus).distinctBy { it.placeTrip }
            .count()


    fun tripsExpenses(tripStatus: TripStatus) =
        filteredTrips(tripStatus).flatMap { it.optionalExpenses }.groupBy { it.icon }
            .mapValues { (_, expenses) ->
                expenses.sumOf { it.amount }
            }

    fun tripsPriciest(tripStatus: TripStatus) = filteredTrips(tripStatus).maxByOrNull { it.mainCost }
}