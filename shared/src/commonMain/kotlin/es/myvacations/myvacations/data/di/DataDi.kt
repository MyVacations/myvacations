package es.myvacations.myvacations.data.di

import es.myvacations.myvacations.data.datasource.SettingsLocalDataSource
import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.data.repository.SettingsRepositoryImpl
import es.myvacations.myvacations.data.repository.TripsRepositoryImpl
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.domain.repository.TripRepository
import org.koin.dsl.module

val dataModule = module {
    single { TripLocalDataSource(get()) }
    single { SettingsLocalDataSource(get()) }
    single<TripRepository> { TripsRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}