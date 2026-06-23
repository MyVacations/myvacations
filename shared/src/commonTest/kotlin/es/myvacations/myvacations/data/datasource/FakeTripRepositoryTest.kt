package es.myvacations.myvacations.data.datasource

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository
import es.myvacations.myvacations.domain.usecase.tripusecase.SaveTripUseCase
import es.myvacations.myvacations.presentation.utils.DefaultTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeTripRepository : TripRepository {
    private val trips = mutableListOf<TripDomain>()

    override fun getTrips(): Flow<List<TripDomain>> = flowOf(trips)

    override suspend fun addTrip(trip: TripDomain) {
        trips.add(trip)
    }

    override fun getSpecificTrip(id: String): Flow<TripDomain> {
        TODO("Not yet implemented")
    }


    override suspend fun updateTrip(trip: TripDomain) {
        TODO("Not yet implemented")
    }

    override suspend fun updateExpense(trip: TripDomain) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExpense(idExpense: String, idTrip: String) {
        TODO("Not yet implemented")
    }

    @Test
    fun saveTrip_shouldStoreTrip() = runTest {
        val repository = FakeTripRepository()
        val useCase = SaveTripUseCase(repository)
        useCase(DefaultTrip.tripActual)
        assertEquals(
            1,
            repository.getTrips().count()
        )
    }
}