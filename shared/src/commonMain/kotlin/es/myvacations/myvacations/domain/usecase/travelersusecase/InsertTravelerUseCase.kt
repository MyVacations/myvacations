package es.myvacations.myvacations.domain.usecase.travelersusecase

import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.repository.TripRepository

class InsertTravelerUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(travelersDomain: TravelersDomain) = repository.insertTraveler(travelersDomain)
}