package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.events.NotificationType
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.repository.NotificationRepository
import es.myvacations.myvacations.domain.repository.TripRepository
import es.myvacations.myvacations.presentation.utils.TravelIcon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveTripForAlertsUseCaseTest {

    private val tripRepository = TestTripRepository()
    private val notificationRepository = FakeNotificationRepository()
    private val appInfoRepository = FakeAppInfoRepository()

    private val useCase = ObserveTripForAlertsUseCase(
        repository = tripRepository,
        notificationRepository = notificationRepository,
        appInfoRepository = appInfoRepository
    )

    private fun createTestTrip(
        id: String = "1",
        title: String = "Trip 1",
        mainBudget: Double = 1000.0,
        expenses: List<TripExpensesDomain> = emptyList()
    ) = TripDomain(
        id = id,
        title = title,
        place = Country.SPAIN,
        startDate = LocalDate(2026, 7, 18),
        endDate = LocalDate(2026, 7, 25),
        mainCost = 0.0,
        mainBudget = mainBudget,
        optionalExpenses = expenses,
        cover = TripCover.BARCELONA,
        favourite = false
    )

    private fun createTestExpense(
        id: String = "e1",
        name: String = "Expense 1",
        amount: Double = 100.0
    ) = TripExpensesDomain(
        id = id,
        name = name,
        icon = TravelIcon.RESTAURANT,
        amount = amount
    )

    @Test
    fun initialEmission_doesNotGenerateNotifications() = runTest {
        val initialTrip = createTestTrip(expenses = listOf(createTestExpense()))
        tripRepository.tripsFlow.value = listOf(initialTrip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        assertTrue(notificationRepository.notifications.isEmpty())
        job.cancel()
    }

    @Test
    fun tripCreated_generatesTripCreatedNotification() = runTest {
        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        val newTrip = createTestTrip(id = "1", title = "New Trip")
        tripRepository.addTrip(newTrip)

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.TRIP_CREATED, notificationRepository.notifications.first().type)
        assertEquals("1", notificationRepository.notifications.first().tripId)
        job.cancel()
    }

    @Test
    fun tripUpdated_generatesTripUpdatedNotification() = runTest {
        val trip = createTestTrip(id = "1", title = "Trip v1")
        tripRepository.tripsFlow.value = listOf(trip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        val updatedTrip = trip.copy(title = "Trip v2")
        tripRepository.updateTrip(updatedTrip)

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.TRIP_UPDATED, notificationRepository.notifications.first().type)
        job.cancel()
    }

    @Test
    fun tripDeleted_generatesTripDeletedNotification() = runTest {
        val trip = createTestTrip(id = "1")
        tripRepository.tripsFlow.value = listOf(trip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        tripRepository.deleteTrip("1")

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.TRIP_DELETED, notificationRepository.notifications.first().type)
        job.cancel()
    }

    @Test
    fun expenseAdded_generatesExpenseAddedNotification() = runTest {
        val trip = createTestTrip(id = "1")
        tripRepository.tripsFlow.value = listOf(trip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        val updatedTrip = trip.copy(optionalExpenses = listOf(createTestExpense()))
        tripRepository.updateTrip(updatedTrip)

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.EXPENSE_ADDED, notificationRepository.notifications.first().type)
        job.cancel()
    }

    @Test
    fun expenseDeleted_generatesExpenseDeletedNotification() = runTest {
        val expense = createTestExpense(id = "e1")
        val trip = createTestTrip(id = "1", expenses = listOf(expense))
        tripRepository.tripsFlow.value = listOf(trip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        tripRepository.deleteExpense("e1", "1")

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.EXPENSE_DELETED, notificationRepository.notifications.first().type)
        job.cancel()
    }

    @Test
    fun budgetTransitions_generateCorrectNotifications() = runTest {
        val trip = createTestTrip(id = "1", mainBudget = 100.0)
        tripRepository.tripsFlow.value = listOf(trip)

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        // 1. Transition: BUDGET_OK -> BUDGET_LOW (spent 85.0, 15% remaining)
        var updatedTrip = trip.copy(optionalExpenses = listOf(createTestExpense(amount = 85.0)))
        tripRepository.updateTrip(updatedTrip)
        // Expected notifications: EXPENSE_ADDED and BUDGET_LOW
        assertEquals(2, notificationRepository.notifications.size)
        assertTrue(notificationRepository.notifications.any { it.type == NotificationType.EXPENSE_ADDED })
        assertTrue(notificationRepository.notifications.any { it.type == NotificationType.BUDGET_LOW })
        notificationRepository.notifications.clear()

        // 2. Transition: BUDGET_LOW -> BUDGET_FINISHED (spent 100.0, 0% remaining)
        updatedTrip = updatedTrip.copy(optionalExpenses = listOf(createTestExpense(amount = 100.0)))
        tripRepository.updateTrip(updatedTrip)
        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.BUDGET_FINISHED, notificationRepository.notifications.first().type)
        notificationRepository.notifications.clear()

        // 3. Transition: BUDGET_FINISHED -> BUDGET_EXCEEDED (spent 101.0, <0% remaining)
        updatedTrip = updatedTrip.copy(optionalExpenses = listOf(createTestExpense(amount = 101.0)))
        tripRepository.updateTrip(updatedTrip)
        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.BUDGET_EXCEEDED, notificationRepository.notifications.first().type)
        notificationRepository.notifications.clear()

        // 4. Transition: BUDGET_EXCEEDED -> BUDGET_OK (spent 0.0)
        updatedTrip = updatedTrip.copy(optionalExpenses = emptyList())
        tripRepository.updateTrip(updatedTrip)
        assertEquals(2, notificationRepository.notifications.size)
        assertTrue(notificationRepository.notifications.any { it.type == NotificationType.EXPENSE_DELETED })
        assertTrue(notificationRepository.notifications.any { it.type == NotificationType.BUDGET_OK })

        job.cancel()
    }

    @Test
    fun repeatedIdenticalEmissions_doNotGenerateDuplicateNotifications() = runTest {
        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        val trip = createTestTrip(id = "1")
        tripRepository.addTrip(trip)

        // First add should notify
        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.TRIP_CREATED, notificationRepository.notifications.first().type)
        notificationRepository.notifications.clear()

        // Repeated emissions of identical state
        tripRepository.tripsFlow.value = listOf(trip)
        tripRepository.tripsFlow.value = listOf(trip.copy())

        assertTrue(notificationRepository.notifications.isEmpty())
        job.cancel()
    }

    @Test
    fun firstLoginWelcomeNotification_triggeredOnlyOnce() = runTest {
        appInfoRepository.firstLogin = true

        val job = launch(UnconfinedTestDispatcher()) {
            useCase().collect()
        }

        // Trigger welcome notification
        tripRepository.tripsFlow.value = emptyList()

        assertEquals(1, notificationRepository.notifications.size)
        assertEquals(NotificationType.INFO_GENERIC_WELCOME, notificationRepository.notifications.first().type)
        assertTrue(appInfoRepository.welcomeShownCalled)
        notificationRepository.notifications.clear()

        // Next emission should not trigger welcome notification again
        tripRepository.tripsFlow.value = emptyList()
        assertTrue(notificationRepository.notifications.isEmpty())

        job.cancel()
    }

    private class TestTripRepository : TripRepository {
        val tripsFlow = MutableStateFlow<List<TripDomain>>(emptyList())

        override fun getTrips(): Flow<List<TripDomain>> = tripsFlow

        override fun getSpecificTrip(id: String): Flow<TripDomain?> {
            return tripsFlow.map { list -> list.find { it.id == id } }
        }

        override fun getSpecificTripWithoutFlow(id: String): TripDomain? {
            return tripsFlow.value.find { it.id == id }
        }

        override suspend fun addTrip(trip: TripDomain) {
            tripsFlow.value = tripsFlow.value + trip
        }

        override suspend fun updateTrip(trip: TripDomain) {
            tripsFlow.value = tripsFlow.value.map { if (it.id == trip.id) trip else it }
        }

        override suspend fun updateExpense(trip: TripDomain) {
            updateTrip(trip)
        }

        override suspend fun deleteTrip(id: String) {
            tripsFlow.value = tripsFlow.value.filter { it.id != id }
        }

        override suspend fun deleteExpense(idExpense: String, idTrip: String) {
            val trip = tripsFlow.value.find { it.id == idTrip } ?: return
            val updatedExpenses = trip.optionalExpenses.filter { it.id != idExpense }
            updateTrip(trip.copy(optionalExpenses = updatedExpenses))
        }

        override fun getTravelers(tripId: String): Flow<List<TravelersDomain>> = flowOf(emptyList())
        override suspend fun updateTraveler(traveler: TravelersDomain) {}
        override suspend fun updateMainTraveler(traveler: TravelersDomain) {}
        override suspend fun deleteTraveler(id: String, tripId: String) {}
        override suspend fun insertTraveler(traveler: TravelersDomain) {}
    }

    private class FakeNotificationRepository : NotificationRepository {
        val notifications = mutableListOf<AppNotificationDomain>()

        override fun selectNotificationsByIdTravel(tripId: String): Flow<List<AppNotificationDomain>> {
            return flowOf(notifications.filter { it.tripId == tripId })
        }

        override fun selectAllNotifications(): Flow<List<AppNotificationDomain>> {
            return flowOf(notifications)
        }

        override fun insertNotification(notification: AppNotificationDomain) {
            notifications.add(notification)
        }

        override fun updateReadNotification(id: Long, tripId: String) {}
        override fun deleteNotification(id: Long, tripId: String) {
            notifications.removeAll { it.id == id && it.tripId == tripId }
        }
    }

    private class FakeAppInfoRepository(
        var firstLogin: Boolean = false,
        var msgSeen: Boolean = false,
        var serverMessage: String = ""
    ) : AppInfoRepository {
        var welcomeShownCalled = false

        override suspend fun isFirstLogin(): Boolean = firstLogin
        override suspend fun messageFromServer(): String = serverMessage
        override suspend fun markWelcomeShown() {
            welcomeShownCalled = true
            firstLogin = false
        }
        override suspend fun messageSeen(): Boolean = msgSeen
    }
}
