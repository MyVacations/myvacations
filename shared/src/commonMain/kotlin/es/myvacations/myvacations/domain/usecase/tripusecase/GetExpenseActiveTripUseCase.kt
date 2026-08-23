package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class GetExpenseActiveTripUseCase(private val repository: TripRepository) {
    operator fun invoke(tripId: String) = repository.getExpensesByTripId(tripId)
}