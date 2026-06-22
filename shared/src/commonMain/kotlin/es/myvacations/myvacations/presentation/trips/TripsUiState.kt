package es.myvacations.myvacations.presentation.trips

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripStatus

data class TripsUiState(
    val trips: List<TripDomain> = emptyList(),
    val filter: TripsFilterStatus = TripsFilterStatus()
) {
    fun getFilteredTrips(): List<TripDomain> =
        when (filter.status) {

            TripStatus.PLANNED ->
                trips.filter {
                    it.tripStatus == TripStatus.PLANNED
                }

            TripStatus.ACTIVE ->
                trips.filter {
                    it.tripStatus == TripStatus.ACTIVE
                }

            TripStatus.COMPLETE ->
                trips.filter {
                    it.tripStatus == TripStatus.COMPLETE
                }

            else -> trips
        }
}