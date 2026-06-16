package es.myvacations.myvacations.presentation.dashboard

import es.myvacations.myvacations.domain.model.Greetings

data class DashboardUiState(
    val greetings: Greetings = Greetings.MORNING,
    val userName : String = "",
    //val currentTrip:
)