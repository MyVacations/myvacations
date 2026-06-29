package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.presentation.createedittrip.CreateEditTripsViewModel
import es.myvacations.myvacations.presentation.dashboard.DashboardViewModel
import es.myvacations.myvacations.presentation.settings.SettingsViewModel
import es.myvacations.myvacations.presentation.statistics.StatisticsViewModel
import es.myvacations.myvacations.presentation.tripdetail.TripDetailsViewModel
import es.myvacations.myvacations.presentation.trips.TripViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel {
        DashboardViewModel(
            initializeDatabaseSettingsUseCase = get(),
            getSettingsUseCase = get(),
            getDayPeriod = get(),
            getTripsUseCase = get(),
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
}