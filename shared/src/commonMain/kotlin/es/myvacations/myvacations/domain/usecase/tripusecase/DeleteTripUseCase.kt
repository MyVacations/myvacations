package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class DeleteTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(id: String) = repository.deleteTrip(id)
}