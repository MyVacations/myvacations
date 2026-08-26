package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.local.SettingsLocalDataSource
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.presentation.utils.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(private val localDataSource: SettingsLocalDataSource) :
    SettingsRepository {

    override fun getSettings(): Flow<SettingsDomain> {
        return localDataSource.getSettings().map { entity ->
            entity?.toDomainModel() ?: SettingsDomain(
                username = "",
                preferredCurrency = Currency.EURO,
                welcomeShow = true,
                iaTutorial = true,
                firstLogin = true,
                modelStage = null
            )
        }
    }

    override suspend fun insertDefaultSettings(settings: SettingsDomain) {
        localDataSource.insertDefaultSettings(
            settings.username,
            settings.preferredCurrency.name
        )
    }

    override suspend fun updateSettings(settings: SettingsDomain) {
        localDataSource.updateSettings(
            settings.username,
            settings.preferredCurrency.name
        )
        localDataSource.updateMainTraveler(settings.username)
    }

    override suspend fun updateFirstLogin() {
        localDataSource.updateFirstLogin()
    }

    override suspend fun updateWelcomeShow(boolean: Boolean) {
        localDataSource.updateWelcomeShow(boolean)
    }

    override suspend fun updateIATutorialSettings(boolean: Boolean) {
        localDataSource.updateIATutorialSettings(boolean)
    }

    override suspend fun updateDownloadStage(stage: String?) {
        localDataSource.updateDownloadStage(stage)
    }

}