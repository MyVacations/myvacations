package es.myvacations.myvacations.domain.usecase.settingsusecase

import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.repository.SettingsRepository

class InitializeDatabaseSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(user: SettingsDomain) = repository.insertDefaultSettings(user)
}