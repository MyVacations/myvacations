package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class TripsRepositoryImpl(
    private val localDataSource: TripLocalDataSource
) : TripRepository {
    override suspend fun getTrips(): Flow<List<TripDomain>> = localDataSource.getAllTrips()
            


    override suspend fun getSpecificTrip(id: String): Flow<TripDomain>? {
        return localDataSource.getById(id)?.let { entity ->
            entity.toDomainModel().copy(
                optionalExpenses = localDataSource
                    .getExpensesByTripId(entity.id)
                    .map { it.toDomainModel() },
            )
        }
    }

    override suspend fun addTrip(trip: TripDomain) {
        localDataSource.insertTrip(
            id = trip.id,
            title = trip.title,
            place = trip.place.name,
            startDate = trip.startDate.toString(),
            endDate = trip.endDate.toString(),
            travelers = trip.travelers,
            daysTraveling = trip.daysTraveling,
            mainCost = trip.mainCost,
            mainBudget = trip.mainBudget,
            cover = trip.cover.name
        )

        trip.optionalExpenses.forEach { expense ->
            localDataSource.insertExpense(
                expense.id,
                trip.id,
                expense.name,
                expense.icon.name,
                expense.amount
            )
        }
    }


    override suspend fun updateTrip(trip: TripDomain) {
        localDataSource.updateTrip(
            id = trip.id,
            title = trip.title,
            place = trip.place.name,
            startDate = trip.startDate.toString(),
            endDate = trip.endDate.toString(),
            travelers = trip.travelers,
            daysTraveling = trip.daysTraveling,
            mainCost = trip.mainCost,
            mainBudget = trip.mainBudget,
            cover = trip.cover.name
        )
    }

    override suspend fun updateExpense(trip: TripDomain) {
        trip.optionalExpenses.forEach { expense ->
            localDataSource.updateExpense(
                expense.id,
                trip.id,
                expense.name,
                expense.icon.name,
                expense.amount
            )
        }
    }

    override suspend fun deleteTrip(id: String) {
        localDataSource.deleteTrip(id)
    }

    override suspend fun deleteExpense(idExpense: String, idTrip: String) {
        localDataSource.deleteExpense(idExpense, idTrip)
    }
}