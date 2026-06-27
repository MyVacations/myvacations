package es.myvacations.myvacations.domain.model

data class TravelersDomain(
    val id: String,
    val tripId: String,
    val travelerName: String,
    val isMainTraveler: Boolean
)