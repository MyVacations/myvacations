package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.presentation.createtrip.CreateTripsViewModel
import es.myvacations.myvacations.presentation.dashboard.DashboardViewModel
import es.myvacations.myvacations.presentation.trips.TripViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // UseCases
    factory { GetDayPeriodUseCase() }

    // ViewModels
    viewModel { DashboardViewModel(getDayPeriod = get(), getTripsUseCase = get()) }
    viewModel { TripViewModel() }
    viewModel { CreateTripsViewModel(saveTrip = get()) }
}