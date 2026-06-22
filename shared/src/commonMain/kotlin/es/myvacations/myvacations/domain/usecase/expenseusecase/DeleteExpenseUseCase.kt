package es.myvacations.myvacations.domain.usecase.expenseusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class DeleteExpenseUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(idExpense: String, idTrip: String) =
        repository.deleteExpense(idExpense, idTrip)
}