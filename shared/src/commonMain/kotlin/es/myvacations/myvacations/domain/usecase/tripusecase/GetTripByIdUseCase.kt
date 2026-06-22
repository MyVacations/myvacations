package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class GetTripByIdUseCase(private val repository: TripRepository)  {
    suspend operator fun invoke(id: String) = repository.getSpecificTrip(id)
}