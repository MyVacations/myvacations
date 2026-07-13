package es.myvacations.myvacations.domain.usecase.tripusecase

import es.myvacations.myvacations.data.datasource.FakeTripRepository
import es.myvacations.myvacations.presentation.utils.DefaultTrip
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveTripUseCaseTest {

    @Test
    fun saveTrip_addsTripToRepository() = runTest {
        val repository = FakeTripRepository()
        val useCase = SaveTripUseCase(repository)
        
        useCase(DefaultTrip.tripActual)
        
        val tripsList = repository.getTrips().first()
        assertEquals(1, tripsList.size)
        assertEquals(DefaultTrip.tripActual, tripsList.first())
    }
}
