package es.myvacations.myvacations.domain.usecase.settingsusecase

import es.myvacations.myvacations.domain.repository.SettingsRepository

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke() = repository.getSettings()
}