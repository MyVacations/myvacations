package es.myvacations.myvacations.domain.usecase.settingsusecase

import es.myvacations.myvacations.domain.repository.SettingsRepository

class UpdateWelcomeShowUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.updateWelcomeShow(true)
}