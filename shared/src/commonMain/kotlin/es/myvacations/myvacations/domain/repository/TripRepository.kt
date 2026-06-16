package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.Trip

interface TripRepository {
    suspend fun getTrips(): List<Trip>
    suspend fun addTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)
}