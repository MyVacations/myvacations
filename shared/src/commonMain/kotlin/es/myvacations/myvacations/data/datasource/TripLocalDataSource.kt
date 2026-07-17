package es.myvacations.myvacations.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class TripLocalDataSource(
    private val database: MyVacationsDatabase
) {
    private val queries = database.vacationsEntityQueries

    fun getAllTrips() =
        queries.selectAllTrips().asFlow().mapToList(Dispatchers.IO)

    fun getById(id: String) =
        queries.selectTripById(id).asFlow().mapToOneOrNull(Dispatchers.IO)

    fun getByIdWithoutFlow(id: String) =
        queries.selectTripById(id).executeAsOneOrNull()

    fun getExpensesByTripId(tripId: String) = queries.selectExpenseByTripId(tripId).executeAsList()

    fun getExpensesByTripIdAndExpenseID(tripId: String, expenseId: String) =
        queries.selectExpenseByTripIdAndExpenseId(
            tripId,
            expenseId
        ).executeAsOne()

    fun insertTrip(
        id: String,
        title: String,
        place: String,
        startDate: String,
        endDate: String,
        mainCost: Double,
        mainBudget: Double,
        cover: String,
        favourite: Boolean
    ) {
        queries.insertTrip(
            id = id,
            title = title,
            place = place,
            startDate = startDate,
            endDate = endDate,
            travelers = 0.toLong(),
            daysTraveling = 0.toLong(),
            mainCost = mainCost,
            mainBudget = mainBudget,
            cover = cover,
            favourite = favourite
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
        mainCost: Double,
        mainBudget: Double,
        cover: String,
        favourite: Boolean
    ) {
        queries.updateTrip(
            id = id,
            title = title,
            place = place,
            startDate = startDate,
            endDate = endDate,
            travelers = 0.toLong(),
            daysTraveling = 0.toLong(),
            mainCost = mainCost,
            mainBudget = mainBudget,
            cover = cover,
            favourite = favourite
        )
    }

    fun updateExpense(
        idExpense: String,
        tripId: String,
        nameExpense: String,
        icon: String,
        amount: Double
    ) {
        queries.updateExpense(
            id = idExpense,
            tripId = tripId,
            nameExpense = nameExpense,
            icon = icon,
            amount = amount
        )
    }

    fun deleteTrip(id: String) {
        queries.deleteTrip(id)
    }

    fun deleteExpense(id: String, tripId: String) {
        queries.deleteExpense(id = id, tripId = tripId)
    }

    fun selectTravelersForInternalQuery(tripId: String) =
        queries.selectTravelers(tripId).executeAsList()

    fun selectTravelers(tripId: String) =
        queries.selectTravelers(tripId).asFlow().mapToList(Dispatchers.IO)

    fun insertTravelers(
        id: String,
        tripId: String,
        travelerName: String,
        mainUser: Boolean = false
    ) {
        queries.insertTravelers(id, tripId, travelerName, mainUser)
    }

    fun updateTraveler(id: String, tripId: String, travelerName: String) {
        queries.updateTraveler(id = id, tripId = tripId, travelerName = travelerName)
    }

    fun updateMainTraveler(travelerName: String) {
        queries.updateMainUser(travelerName = travelerName)
    }

    fun deleteTraveler(id: String, tripId: String) {
        val travelers =
            (queries.selectTravelersAccountByTripId(tripId).executeAsOne() - 1).coerceAtLeast(1)
        queries.updateTravelersAccount(travelers, tripId)
        queries.deleteTraveler(id = id, tripId = tripId)
    }

    fun getNameSettings() = queries.selectSettings().executeAsOne().name

    fun updateNameSettings(
        name: String
    ) = queries.updateNameSettings(name = name)

    fun selectNotificationsByIdTravel(tripId: String) =
        queries.selectNotificationsByIdTravel(tripId).asFlow().mapToList(
            Dispatchers.IO
        )

    fun selectAllNotifications() =
        queries.selectAllNotifications().asFlow().mapToList(Dispatchers.IO)

    fun insertNotification(
        tripId: String,
        type: String,
        createdAt: String,
        read: Boolean = false
    ) {
        queries.insertNotification(tripId, type, createdAt, read)
    }

    fun updateReadNotification(id: Long, tripId: String) {
        queries.updateReadNotification(id = id, tripId = tripId, read = true)
    }

    fun deleteNotification(id: Long, tripId: String) {
        queries.deleteNotification(id, tripId)
    }
}