package es.myvacations.myvacations.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import kotlinx.coroutines.Dispatchers

class TripLocalDataSource(
    private val database: MyVacationsDatabase
) {

    private val queries = database.tripEntityQueries

    fun getAllTrips() =
        queries.selectAll().asFlow().mapToList(Dispatchers.Default)

    fun getById(id: String) =
        queries.selectById(id).asFlow()
            .mapToOneOrNull(Dispatchers.Default)

    fun getExpensesByTripId(tripId: String) = queries.selectExpenseByTripId(tripId).asFlow().mapToList(Dispatchers.Default)

    fun insertTrip(
        id: String,
        title: String,
        place: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        daysTraveling: Int,
        mainCost: Double,
        mainBudget: Double,
        cover: String
    ) {
        queries.insertTrip(
            id = id,
            title = title,
            place = place,
            startDate = startDate,
            endDate = endDate,
            travelers = travelers.toLong(),
            daysTraveling = daysTraveling.toLong(),
            mainCost = mainCost,
            mainBudget = mainBudget,
            cover = cover
        )
    }

    fun insertExpense(
        idExpense: String,
        tripId: String,
        nameExpense: String,
        icon: String,
        amount: Double
    ) {
        queries.insertExpense(idExpense, tripId, nameExpense, icon, amount)
    }

    fun updateTrip(
        id: String,
        title: String,
        place: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        daysTraveling: Int,
        mainCost: Double,
        mainBudget: Double,
        cover: String
    ) {
        queries.updateTrip(
            id = id,
            title = title,
            place = place,
            startDate = startDate,
            endDate = endDate,
            travelers = travelers.toLong(),
            daysTraveling = daysTraveling.toLong(),
            mainCost = mainCost,
            mainBudget = mainBudget,
            cover = cover
        )
    }

    fun updateExpense(
        idExpense: String,
        tripId: String,
        nameExpense: String,
        icon: String,
        amount: Double
    ) {
        queries.updateExpense(id = idExpense, tripId = tripId, nameExpense = nameExpense, icon = icon, amount = amount)
    }

    fun deleteTrip(id: String) {
        queries.deleteTrip(id)
    }

    fun deleteExpense(id: String, tripId: String) {
        queries.deleteExpense(id = id, tripId = tripId)
    }
}