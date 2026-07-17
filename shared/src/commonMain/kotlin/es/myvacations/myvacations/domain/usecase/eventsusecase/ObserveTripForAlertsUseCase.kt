package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.events.NotificationType
import es.myvacations.myvacations.domain.events.currentBudgetNotificationType
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripExpensesDomain
import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.repository.NotificationRepository
import es.myvacations.myvacations.domain.repository.TripRepository
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.mapper.toDomainModel
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ObserveTripForAlertsUseCase(
    private val repository: TripRepository,
    private val notificationRepository: NotificationRepository,
    private val appInfoRepository: AppInfoRepository
) {
    private var tripsInitialized = false
    private var expensesInitialized = false
    private val previousStatesBudget = mutableMapOf<String, NotificationType>()
    private val previousTrips = mutableMapOf<String, TripDomain>()
    private val previousExpenses = mutableMapOf<String, Pair<TripDomain, TripExpensesDomain>>()
    operator fun invoke() = repository.getTrips().onEach { trips ->
        val notifications = buildList {
            addAll(detectTripChanges(trips))
            addAll(detectExpenseChanges(trips))
            addAll(detectBudgetChanges(trips))
            addAll(detectedInfoChanges())
        }
        notifications.forEach {
            notificationRepository.insertNotification(it)
        }
    }

    private suspend fun detectedInfoChanges(): List<AppNotificationDomain> {
        val notifications = mutableListOf<AppNotificationDomain>()
        if (appInfoRepository.isFirstLogin()) {
            notifications += createNotification(
                trip = TripUiState().toDomainModel(),
                status = NotificationType.INFO_GENERIC_WELCOME,
            )
            appInfoRepository.markWelcomeShown()
        }

        if (appInfoRepository.messageSeen()) {
            notifications += createNotification(
                trip = TripUiState().toDomainModel(),
                status = NotificationType.INFO_UPDATES,
                ownMessage = appInfoRepository.messageFromServer()
            )
        }
        return notifications
    }

    private suspend fun detectBudgetChanges(
        trips: List<TripDomain>
    ): List<AppNotificationDomain> {
        val notifications = mutableListOf<AppNotificationDomain>()

        trips.forEach { trip ->
            val currentStatus = trip.currentBudgetNotificationType()
            val previousStatus = previousStatesBudget[trip.id]

            if (previousStatus == null) {
                previousStatesBudget[trip.id] = currentStatus
                return@forEach
            }

            if (previousStatus != currentStatus) {
                val notification = createNotification(
                    trip = trip,
                    status = currentStatus,
                )
                notifications += notification
                previousStatesBudget[trip.id] = currentStatus
            }
        }
        return notifications
    }

    private suspend fun detectTripChanges(
        trips: List<TripDomain>
    ): List<AppNotificationDomain> {
        val notifications = mutableListOf<AppNotificationDomain>()
        val currentIds = trips.map { it.id }.toSet()
        if (!tripsInitialized) {
            trips.associateByTo(previousTrips) { it.id }
            tripsInitialized = true
            return emptyList()
        }

        trips.forEach { trip ->
            val addTrip = previousTrips[trip.id]
            if (addTrip == null) {
                val notification = createNotification(
                    trip,
                    NotificationType.TRIP_CREATED,
                )
                notifications += notification
                previousTrips[trip.id] = trip
                return@forEach
            }

            if (addTrip.hasRelevantChanges(trip)) {
                val notification = createNotification(
                    trip,
                    NotificationType.TRIP_UPDATED,
                )
                notifications += notification
                previousTrips[trip.id] = trip
                return@forEach
            }
        }
        previousTrips.forEach { (tripId, trip) ->

            if (tripId !in currentIds) {

                val notification = createNotification(
                    trip,
                    NotificationType.TRIP_DELETED,
                )

                notifications += notification
            }
        }
        previousTrips.clear()

        trips.forEach { trip ->
            previousTrips[trip.id] = trip
        }
        return notifications
    }

    private suspend fun detectExpenseChanges(
        trips: List<TripDomain>
    ): List<AppNotificationDomain> {
        val notifications = mutableListOf<AppNotificationDomain>()
        val currentExpenseIds = trips
            .flatMap { it.optionalExpenses }
            .map { it.id }
            .toSet()
        if (!expensesInitialized) {
            trips.forEach { trip ->
                trip.optionalExpenses.forEach { expense ->
                    previousExpenses[expense.id] = trip to expense
                }
            }
            expensesInitialized = true
            return emptyList()
        }
        trips.forEach { trip ->
            trip.optionalExpenses.forEach { expense ->
                val previousExpense = previousExpenses[expense.id]
                if (previousExpense == null) {
                    val notification = createNotification(
                        trip,
                        NotificationType.EXPENSE_ADDED,
                    )
                    notifications += notification
                    return@forEach
                }
            }
        }

        previousExpenses.forEach { (expenseId, pair) ->
            if (expenseId !in currentExpenseIds) {
                val (trip, _) = pair
                val notification = createNotification(
                    trip,
                    NotificationType.EXPENSE_DELETED,
                )
                notifications += notification
            }
        }
        previousExpenses.clear()

        trips.forEach { trip ->
            trip.optionalExpenses.forEach { expensesDomain ->
                previousExpenses[expensesDomain.id] = trip to expensesDomain
            }
        }
        return notifications
    }


    private fun TripDomain.hasRelevantChanges(other: TripDomain): Boolean {
        return title != other.title ||
                place != other.place ||
                cover != other.cover ||
                startDate != other.startDate ||
                endDate != other.endDate ||
                mainBudget != other.mainBudget
    }

    private fun createNotification(
        trip: TripDomain,
        status: NotificationType,
        ownMessage: String = "",
    ): AppNotificationDomain {
        return AppNotificationDomain(
            id = 0.toLong(),
            tripId = trip.id,
            type = status,
            message = ownMessage,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            read = false
        )
    }
}