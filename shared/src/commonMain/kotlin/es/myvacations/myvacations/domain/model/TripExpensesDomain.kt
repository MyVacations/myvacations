package es.myvacations.myvacations.domain.model

import es.myvacations.myvacations.presentation.utils.TravelIcon

data class TripExpensesDomain(
    val id: String,
    val name: String,
    val icon: TravelIcon,
    val amount: Double
)