package es.myvacations.myvacations.data.di

import es.myvacations.myvacations.data.datasource.local.ModelLocalDataSourceImpl
import es.myvacations.myvacations.data.datasource.local.SettingsLocalDataSource
import es.myvacations.myvacations.data.datasource.local.TripLocalDataSource
import es.myvacations.myvacations.data.datasource.remote.ModelLocalDataSource
import es.myvacations.myvacations.data.datasource.remote.ModelRemoteDataSource
import es.myvacations.myvacations.data.repository.AiRepositoryImpl
import es.myvacations.myvacations.data.repository.AppInfoRepositoryImpl
import es.myvacations.myvacations.data.repository.AppWidgetUpdaterRepositoryImpl
import es.myvacations.myvacations.data.repository.GetDeviceCalendarRepository
import es.myvacations.myvacations.data.repository.NotificationRepositoryImpl
import es.myvacations.myvacations.data.repository.SettingsRepositoryImpl
import es.myvacations.myvacations.data.repository.TripsRepositoryImpl
import es.myvacations.myvacations.data.repository.remote.ModelRemoteDataSourceImpl
import es.myvacations.myvacations.domain.manager.DatabaseInitializer
import es.myvacations.myvacations.domain.manager.NotificationObserverManager
import es.myvacations.myvacations.domain.manager.WidgetObserverManager
import es.myvacations.myvacations.domain.repository.AIRepository
import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.repository.DeviceCalendarRepository
import es.myvacations.myvacations.domain.repository.NotificationRepository
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.domain.repository.TripRepository
import es.myvacations.myvacations.domain.repository.WidgetUpdater
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    single<ModelLocalDataSource> { ModelLocalDataSourceImpl(dispatcher = Dispatchers.IO) }
    single { TripLocalDataSource(get()) }
    single { SettingsLocalDataSource(get()) }
    single<ModelRemoteDataSource> {
        ModelRemoteDataSourceImpl(
            client = get()
        )
    }
    single<TripRepository> { TripsRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<AppInfoRepository> { AppInfoRepositoryImpl(get()) }
    single { DatabaseInitializer(get()) }
    single { NotificationObserverManager(get()) }
    single { WidgetObserverManager(get()) }
    single<DeviceCalendarRepository> {
        GetDeviceCalendarRepository()
    }
    single<WidgetUpdater> {
        AppWidgetUpdaterRepositoryImpl()
    }

    single<AIRepository> {
        AiRepositoryImpl()
    }
}