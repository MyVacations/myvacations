package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import es.myvacations.myvacations.presentation.dashboard.DashboardViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val appModule = module {

    // Repositories
    // single<TripRepository> { TripRepositoryImpl() }

    // UseCases
     factory { GetDayPeriodUseCase() }

    // ViewModels
     viewModel { DashboardViewModel(getDayPeriod = get()) }
}