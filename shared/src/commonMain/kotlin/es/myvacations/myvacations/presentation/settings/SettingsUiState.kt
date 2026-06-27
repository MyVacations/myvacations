package es.myvacations.myvacations.presentation.settings

import es.myvacations.myvacations.presentation.utils.Currency

data class SettingsUiState(
    val userName: String = "",
    val currency: Currency = Currency.EURO
)