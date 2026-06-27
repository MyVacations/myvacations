package es.myvacations.myvacations.domain.usecase.travelersusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class GetTravelersUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(tripId: String) = repository.getTravelers(tripId)
}