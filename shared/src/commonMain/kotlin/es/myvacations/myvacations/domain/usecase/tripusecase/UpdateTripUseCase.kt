package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository

class UpdateTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: TripDomain) = repository.updateTrip(trip)
}