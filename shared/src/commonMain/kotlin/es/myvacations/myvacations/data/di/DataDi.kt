package es.myvacations.myvacations.data.di

import es.myvacations.myvacations.data.datasource.SettingsLocalDataSource
import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.data.repository.AppInfoRepositoryImpl
import es.myvacations.myvacations.data.repository.NotificationRepositoryImpl
import es.myvacations.myvacations.data.repository.SettingsRepositoryImpl
import es.myvacations.myvacations.data.repository.TripsRepositoryImpl
import es.myvacations.myvacations.domain.manager.DatabaseInitializer
import es.myvacations.myvacations.domain.manager.NotificationObserverManager
import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.repository.NotificationRepository
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.domain.repository.TripRepository
import org.koin.dsl.module

val dataModule = module {
    single { TripLocalDataSource(get()) }
    single { SettingsLocalDataSource(get()) }
    single<TripRepository> { TripsRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<AppInfoRepository> { AppInfoRepositoryImpl(get()) }
    single { DatabaseInitializer(get()) }
    single { NotificationObserverManager(get()) }
}