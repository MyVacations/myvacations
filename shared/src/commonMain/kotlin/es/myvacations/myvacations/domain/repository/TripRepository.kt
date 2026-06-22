package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.TripDomain
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun getTrips(): Flow<List<TripDomain>>
    suspend fun getSpecificTrip(id: String): Flow<TripDomain?>
    suspend fun addTrip(trip: TripDomain)
    suspend fun updateTrip(trip: TripDomain)
    suspend fun updateExpense(trip: TripDomain)
    suspend fun deleteTrip(id: String)
    suspend fun deleteExpense(idExpense: String, idTrip: String)
}