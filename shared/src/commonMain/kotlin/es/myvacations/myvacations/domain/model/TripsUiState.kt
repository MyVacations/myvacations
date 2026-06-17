package es.myvacations.myvacations.domain.model

data class TripsUiState(
    val trips: List<Trip> = emptyList(),
    val filter: TripsFilterStatus = TripsFilterStatus()
) {
    fun getFilteredTrips(): List<Trip> =
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
