package es.myvacations.myvacations.presentation.utils

import kotlin.random.Random

data class TripExpenseUiState(
    val id: String = Random.nextInt().toString(),
    val name: String = "",
    val icon: TravelIcon = TravelIcon.PLACE,
    val amount: Double = 0.0,
    val currency: Currency = Currency.EURO
)