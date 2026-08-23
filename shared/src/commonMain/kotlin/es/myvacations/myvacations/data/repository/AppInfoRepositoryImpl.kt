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
            ?.firstLogin == true
    }

    override suspend fun messageFromServer(): String {
        return ""
    }

    override suspend fun updateFirstLogin() {
        settingsRepository.updateFirstLogin()
    }

    override suspend fun messageSeen(): Boolean {
        return false
    }
}