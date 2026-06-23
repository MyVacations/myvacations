package es.myvacations.myvacations.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import kotlinx.coroutines.Dispatchers

class UserLocalDataSource(
    private val database: MyVacationsDatabase
) {
    private val queries = database.vacationsEntityQueries

    fun getUser() =
        queries.selectUser().asFlow().mapToOneOrNull(Dispatchers.Default)

    fun insertUser(
        id: String,
        name: String
    ) = queries.insertUser(id, name)

    fun updateUser(
        id: String,
        name: String
    ) = queries.updateUser(name, id)
}