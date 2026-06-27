package es.myvacations.myvacations.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class SettingsLocalDataSource(
    private val database: MyVacationsDatabase
) {
    private val queries = database.vacationsEntityQueries

    fun getSettings() =
        queries.selectSettings().asFlow().mapToOne(Dispatchers.IO)

    fun insertDefaultSettings(
        name: String,
        currency: String
    ) = queries.insertDefaultUser(name, currency)

    fun updateSettings(
        name: String,
        currency: String
    ) = queries.updateSettings(name = name, currency = currency)

    fun updateMainTraveler(travelerName: String) {
        queries.updateMainUser(travelerName = travelerName)
    }
}