package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.presentation.createtrip.CreateEditTripsViewModel
import es.myvacations.myvacations.presentation.dashboard.DashboardViewModel
import es.myvacations.myvacations.presentation.tripdetail.TripDetailsViewModel
import es.myvacations.myvacations.presentation.trips.TripViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel {
        DashboardViewModel(
            getDayPeriod = get(),
            getTripsUseCase = get(),
            getUserUseCase = get()
        )
    }
    viewModel { TripViewModel(getTripsUseCase = get()) }
    viewModel { TripDetailsViewModel(getTripByIdUseCase = get(), deleteTripUseCase = get()) }
    viewModel {
        CreateEditTripsViewModel(
            saveTrip = get(),
            getTripIdUseCase = get(),
            editTrip = get()
        )
    }
}