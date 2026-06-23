package es.myvacations.myvacations.data.di

import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.data.datasource.UserLocalDataSource
import es.myvacations.myvacations.data.repository.TripsRepositoryImpl
import es.myvacations.myvacations.data.repository.UserRepositoryImpl
import es.myvacations.myvacations.domain.repository.TripRepository
import es.myvacations.myvacations.domain.repository.UserRepository
import org.koin.dsl.module


val dataModule = module {
    single { TripLocalDataSource(get()) }
    single { UserLocalDataSource(get()) }
    single<TripRepository> { TripsRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}