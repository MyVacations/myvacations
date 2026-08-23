package es.myvacations.myvacations.domain.di

import es.myvacations.myvacations.domain.repository.PlacesRepository
import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.AdsUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.ClassifyIntentUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.MapAndLocationUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.latestmodelrelease.EnsureModelInstalledUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.overpass.PlacesUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.AddEventToCalendarUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.DeleteNotificationUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.ObserveTripForAlertsUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.PlacesWidgetObserverUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.SelectAllNotificationsUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.SelectNotificationsInATripUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.TripsWidgetObserverUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.UpdateNotificationUseCase
import es.myvacations.myvacations.domain.usecase.expenseusecase.DeleteExpenseUseCase
import es.myvacations.myvacations.domain.usecase.expenseusecase.UpdateExpenseUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.InitializeDatabaseSettingsUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.UpdateSettingsUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.UpdateWelcomeShowUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.DeleteTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.GetTravelersUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.InsertTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.UpdateMainTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.UpdateTravelerUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.DeleteTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetActiveTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetExpenseActiveTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.SaveTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.UpdateTripUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // UseCases
    factory { GetDayPeriodUseCase() }
    factory { GetTripsUseCase(get()) }
    factory { GetTripByIdUseCase(get()) }
    factory { SaveTripUseCase(get()) }
    factory { UpdateTripUseCase(get()) }
    factory { DeleteTripUseCase(get()) }
    factory { UpdateExpenseUseCase(get()) }
    factory { DeleteExpenseUseCase(get()) }
    factory { GetSettingsUseCase(get()) }
    factory { InitializeDatabaseSettingsUseCase(get()) }
    factory { UpdateSettingsUseCase(get()) }
    factory { UpdateWelcomeShowUseCase(get()) }
    factory { DeleteTravelerUseCase(get()) }
    factory { GetTravelersUseCase(get()) }
    factory { UpdateTravelerUseCase(get()) }
    factory { UpdateMainTravelerUseCase(get()) }
    factory { InsertTravelerUseCase(get()) }
    factory { SelectAllNotificationsUseCase(get()) }
    factory { SelectNotificationsInATripUseCase(get()) }
    factory { UpdateNotificationUseCase(get()) }
    factory { DeleteNotificationUseCase(get()) }
    factory { AddEventToCalendarUseCase(get()) }
    factory { ClassifyIntentUseCase(get()) }
    factoryOf(::ObserveTripForAlertsUseCase)
    factoryOf(::TripsWidgetObserverUseCase)
    factoryOf(::PlacesWidgetObserverUseCase)
    factoryOf(::GetActiveTripUseCase)
    factoryOf(::GetExpenseActiveTripUseCase)
    factory { EnsureModelInstalledUseCase(get()) }
    factory { MapAndLocationUseCase(get()) }
    factory {
        PlacesUseCase(
            placesRepository = get<PlacesRepository>()
        )
    }
    factory {
        AdsUseCase(
            get()
        )
    }

}