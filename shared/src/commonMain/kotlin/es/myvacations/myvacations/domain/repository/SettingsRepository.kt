package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.SettingsDomain
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsDomain?>
    suspend fun insertDefaultSettings(settings: SettingsDomain)
    suspend fun updateSettings(settings: SettingsDomain)
    suspend fun updateFirstLogin()
    suspend fun updateWelcomeShow(boolean: Boolean)
    suspend fun updateIATutorialSettings(boolean: Boolean)
    suspend fun updateDownloadStage(stage: String?)
}