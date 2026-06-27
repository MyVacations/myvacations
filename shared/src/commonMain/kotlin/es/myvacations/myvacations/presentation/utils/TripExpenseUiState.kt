package es.myvacations.myvacations.presentation.utils

data class TripExpenseUiState(
    val id: String = "",
    val name: String = "",
    val icon: TravelIcon = TravelIcon.RESTAURANT,
    val amount: Double = 0.0
)