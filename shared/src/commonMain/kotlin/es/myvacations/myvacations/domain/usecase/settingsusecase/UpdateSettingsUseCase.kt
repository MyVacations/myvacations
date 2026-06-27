package es.myvacations.myvacations.domain.usecase.settingsusecase

import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.repository.SettingsRepository

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: SettingsDomain) = repository.updateSettings(settings)
}