package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.presentation.createedittrip.CreateEditTripsViewModel
import es.myvacations.myvacations.presentation.dashboard.DashboardViewModel
import es.myvacations.myvacations.presentation.notifications.ShowNotificationsViewModel
import es.myvacations.myvacations.presentation.settings.SettingsViewModel
import es.myvacations.myvacations.presentation.statistics.StatisticsViewModel
import es.myvacations.myvacations.presentation.tripdetail.TripDetailsViewModel
import es.myvacations.myvacations.presentation.trips.TripViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //Si hiciese falta usar crashlytics en algún viewmodel
    //crashReporter = get()

    // ViewModels
    viewModel {
        DashboardViewModel(
            selectAllNotificationsUseCase = get(),
            getSettingsUseCase = get(),
            getDayPeriod = get(),
            getTripsUseCase = get()
        )
    }
    viewModel { TripViewModel(getTripsUseCase = get()) }
    viewModel {
        TripDetailsViewModel(
            getTripByIdUseCase = get(),
            getSettingUseCase = get(),
            deleteTripUseCase = get(),
            editTrip = get(),
            selectedTravelers = get(),
            updateTravelers = get(),
            deleteTravelerUseCase = get(),
            insertTravelerUseCase = get(),
            updateMainTravelerUseCase = get()
        )
    }
    viewModel {
        CreateEditTripsViewModel(
            saveTrip = get(),
            getTripIdUseCase = get(),
            editTrip = get(),
            getSettingsUseCase = get()
        )
    }
    viewModel {
        SettingsViewModel(
            getSettingsUseCase = get(),
            updateSettingsUseCase = get()
        )
    }
    viewModel { StatisticsViewModel(getTripsUseCase = get()) }

    viewModel { ShowNotificationsViewModel(selectTripByIdUseCase = get(), selectAllNotificationsUseCase = get(), updateNotificationUseCase = get(), deleteNotificationUseCase = get(), appInfoRepository = get()) }
}