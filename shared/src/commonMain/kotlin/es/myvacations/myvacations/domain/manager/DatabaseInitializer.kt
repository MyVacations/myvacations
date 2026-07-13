package es.myvacations.myvacations.domain.manager

import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.usecase.settingsusecase.InitializeDatabaseSettingsUseCase
import es.myvacations.myvacations.presentation.utils.Currency

class DatabaseInitializer(
    private val initializeDatabaseSettingsUseCase: InitializeDatabaseSettingsUseCase
) {
    suspend fun initialize() {
        initializeDatabaseSettingsUseCase(
            SettingsDomain(
                username = "",
                preferredCurrency = Currency.EURO,
                welcomeShown = true
            )
        )
    }
}