package es.myvacations.myvacations.domain.di

import es.myvacations.myvacations.domain.usecase.tripusecase.*
import es.myvacations.myvacations.domain.usecase.expenseusecase.*
import org.koin.dsl.module

val domainModule = module {
    factory { GetTripsUseCase(get()) }
    factory { GetTripByIdUseCase(get()) }
    factory { SaveTripUseCase(get()) }
    factory { UpdateTripUseCase(get()) }
    factory { DeleteTripUseCase(get()) }
    factory { UpdateExpenseUseCase(get()) }
    factory { DeleteExpenseUseCase(get()) }
}