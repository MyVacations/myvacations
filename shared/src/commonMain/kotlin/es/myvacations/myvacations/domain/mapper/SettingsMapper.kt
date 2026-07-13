package es.myvacations.myvacations.domain.mapper

import es.myvacations.myvacations.data.database.SettingsData
import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.presentation.utils.Currency


fun SettingsData.toDomainModel() = SettingsDomain(
    username = name,
    preferredCurrency = Currency.valueOf(currency),
    welcomeShown = welcomeShow
)