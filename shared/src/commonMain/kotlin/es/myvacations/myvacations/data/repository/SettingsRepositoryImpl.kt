package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.SettingsLocalDataSource
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.model.SettingsDomain
import es.myvacations.myvacations.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(private val localDataSource: SettingsLocalDataSource) :
    SettingsRepository {

    override fun getSettings(): Flow<SettingsDomain> {
        return localDataSource.getSettings().map { entity ->
            entity.toDomainModel()
        }
    }

    override suspend fun insertDefaultSettings(settings: SettingsDomain) {
        localDataSource.insertDefaultSettings(
            settings.username,
            settings.preferedCurrency.name
        )
    }

    override suspend fun updateSettings(settings: SettingsDomain) {
        localDataSource.updateSettings(
            settings.username,
            settings.preferedCurrency.name
        )
        localDataSource.updateMainTraveler(settings.username)
    }

    override suspend fun updateWelcomeShown() {
        localDataSource.updateWelcomeShown()
    }
}