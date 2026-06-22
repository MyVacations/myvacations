package es.myvacations.myvacations.domain.usecase.expenseusecase

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository

class UpdateExpenseUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: TripDomain) = repository.updateExpense(trip)
}