package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.Uuid

class TripsRepositoryImpl(
    private val localDataSource: TripLocalDataSource
) : TripRepository {
    override fun getTrips(): Flow<List<TripDomain>> {
        return localDataSource.getAllTrips().map { entities ->
            entities.map { entity ->
                entity.toDomainModel().copy(
                    optionalExpenses = localDataSource.getExpensesByTripId(entity.id)
                        .map { expense ->
                            expense.toDomainModel()
                        }
                )
            }
        }
    }

    override fun getSpecificTrip(id: String): Flow<TripDomain?> {
        return localDataSource.getById(id).map { entity ->
            entity?.toDomainModel()?.copy(
                optionalExpenses = localDataSource.getExpensesByTripId(entity.id)
                    .map { expense ->
                        expense.toDomainModel()
                    }
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

        repeat(trip.travelers) {
            if (it == 0) localDataSource.insertTravelers(
                Uuid.random().toHexString(),
                trip.id,
                localDataSource.getNameSettings(),
                true
            ) else localDataSource.insertTravelers(
                Uuid.random().toHexString(), trip.id, ""
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
            mainBudget = trip.mainBudget ?: 0.0,
            cover = trip.cover.name
        )
        val currentExpenses =
            localDataSource.getExpensesByTripId(trip.id)

        val currentTravelersIndices =
            localDataSource.selectTravelersForInternalQuery(trip.id).size


        val newExpenseIds =
            trip.optionalExpenses.map { it.id }.toSet()

        val newTravelersIndices = trip.travelers

        currentExpenses.filter { it.id !in newExpenseIds }
            .forEach { expense ->
                localDataSource.deleteExpense(expense.id, trip.id)
            }

        val difference = currentTravelersIndices - newTravelersIndices

        if (difference > 0) {
            repeat(difference) {
                val lastTraveler =
                    localDataSource.selectTravelersForInternalQuery(trip.id).last()
                localDataSource.deleteTraveler(
                    lastTraveler.id,
                    trip.id
                )
            }
        } else if (difference < 0) {
            repeat(-difference) {
                localDataSource.insertTravelers(
                    id = Uuid.random().toHexString(),
                    tripId = trip.id,
                    travelerName = ""
                )
            }
        }

        trip.optionalExpenses.forEach { expense ->
            if (localDataSource.getExpensesByTripIdAndExpenseID(trip.id, expense.id)) {
                localDataSource.updateExpense(
                    expense.id,
                    trip.id,
                    expense.name,
                    expense.icon.name,
                    expense.amount
                )
            } else {
                localDataSource.insertExpense(
                    expense.id,
                    trip.id,
                    expense.name,
                    expense.icon.name,
                    expense.amount
                )
            }
        }
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

    override fun getTravelers(tripId: String): Flow<List<TravelersDomain>> {
        return localDataSource.selectTravelers(tripId).map { entities ->
            entities.map { entity ->
                entity.toDomainModel()
            }
        }
    }

    override suspend fun updateTraveler(traveler: TravelersDomain) {
        localDataSource.updateTraveler(traveler.id, traveler.tripId, traveler.travelerName)
    }

    override suspend fun updateMainTraveler(traveler: TravelersDomain) {
        localDataSource.updateMainTraveler(traveler.travelerName)
        localDataSource.updateNameSettings(traveler.travelerName)
    }

    override suspend fun deleteTraveler(id: String, tripId: String) {
        localDataSource.deleteTraveler(id, tripId)
    }

    override suspend fun insertTraveler(traveler: TravelersDomain) {
        localDataSource.insertTravelers(
            traveler.id,
            traveler.tripId,
            traveler.travelerName
        )
    }
}