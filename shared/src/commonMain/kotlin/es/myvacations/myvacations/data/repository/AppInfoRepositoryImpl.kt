package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class AppInfoRepositoryImpl(
    val settingsRepository: SettingsRepository
) : AppInfoRepository {
    override suspend fun isFirstLogin(): Boolean {
        return settingsRepository
            .getSettings()
            .first()
            ?.welcomeShown == true
    }

    override suspend fun messageFromServer(): String {
        return ""
    }

    override suspend fun markWelcomeShown() {
        settingsRepository.updateWelcomeShown()
    }

    override suspend fun messageSeen(): Boolean {
        return false
    }
}