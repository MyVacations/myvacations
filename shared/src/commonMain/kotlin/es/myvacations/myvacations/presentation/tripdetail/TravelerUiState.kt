package es.myvacations.myvacations.presentation.tripdetail

data class TravelerUiState(
    val id: String,
    val tripId: String,
    val travelerName: String,
    val isMainTraveler: Boolean
)
