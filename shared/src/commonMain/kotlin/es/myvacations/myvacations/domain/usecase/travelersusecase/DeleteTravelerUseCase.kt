package es.myvacations.myvacations.domain.usecase.travelersusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class DeleteTravelerUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(id: String, idTrip: String) = repository.deleteTraveler(id,idTrip)
}