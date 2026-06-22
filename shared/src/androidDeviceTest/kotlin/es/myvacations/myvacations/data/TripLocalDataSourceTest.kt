import androidx.test.core.app.ApplicationProvider
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import es.myvacations.myvacations.data.datasource.TripLocalDataSource


class TripLocalDataSourceTest {

    @Test
    fun insertTrip_shouldPersistTrip() {

        val driver = AndroidSqliteDriver(
            schema = MyVacationsDatabase.Schema,
            context = ApplicationProvider.getApplicationContext(),
            name = null
        )

        val database = MyVacationsDatabase(driver)

        val dataSource = TripLocalDataSource(database)

        dataSource.insertTrip(
            id = "trip_1",
            title = "Madrid",
            place = "Spain",
            startDate = "2026-01-01",
            endDate = "2026-01-10",
            travelers = 2,
            daysTraveling = 10,
            mainCost = 100.0,
            mainBudget = 200.0,
            cover = "CITY"
        )

        val trip = dataSource.getById("trip_1")

        assertNotNull(trip)
        assertEquals("Madrid", trip.title)
    }
}