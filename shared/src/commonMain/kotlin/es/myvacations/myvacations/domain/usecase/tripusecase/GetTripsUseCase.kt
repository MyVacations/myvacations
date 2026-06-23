package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.repository.TripRepository

class GetTripsUseCase(private val repository: TripRepository)  {
    operator fun invoke() = repository.getTrips()
}