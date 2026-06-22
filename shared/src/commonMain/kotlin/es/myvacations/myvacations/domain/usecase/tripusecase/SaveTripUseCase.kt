package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository

class SaveTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: TripDomain) = repository.addTrip(trip)
}