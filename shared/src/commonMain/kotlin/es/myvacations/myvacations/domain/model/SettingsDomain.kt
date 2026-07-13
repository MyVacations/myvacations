package es.myvacations.myvacations.domain.model

import es.myvacations.myvacations.presentation.utils.Currency

data class SettingsDomain(
    val username: String,
    val preferredCurrency: Currency,
    val welcomeShown: Boolean
)