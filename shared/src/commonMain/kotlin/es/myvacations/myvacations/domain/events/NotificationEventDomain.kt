package es.myvacations.myvacations.domain.events

import es.myvacations.myvacations.domain.model.NotificationStatus
import es.myvacations.myvacations.domain.model.TripDomain
import kotlinx.datetime.LocalDateTime

enum class NotificationType {
    TRIP_CREATED,
    TRIP_UPDATED,
    TRIP_DELETED,

    EXPENSE_ADDED,
    EXPENSE_DELETED,

    BUDGET_OK,
    BUDGET_LOW,
    BUDGET_FINISHED,
    BUDGET_EXCEEDED,

    INFO_GENERIC_WELCOME,
    INFO_UPDATES
}

fun NotificationStatus.matches(type: NotificationType): Boolean =
    when (this) {
        NotificationStatus.ALL -> true

        NotificationStatus.TRIPS ->
            type in listOf(
                NotificationType.TRIP_CREATED,
                NotificationType.TRIP_UPDATED,
                NotificationType.TRIP_DELETED
            )

        NotificationStatus.EXPENSES ->
            type in listOf(
                NotificationType.EXPENSE_ADDED,
                NotificationType.EXPENSE_DELETED
            )

        NotificationStatus.BUDGETS ->
            type in listOf(
                NotificationType.BUDGET_OK,
                NotificationType.BUDGET_LOW,
                NotificationType.BUDGET_FINISHED,
                NotificationType.BUDGET_EXCEEDED
            )

        NotificationStatus.INFO ->
            type in listOf(
                NotificationType.INFO_GENERIC_WELCOME,
                NotificationType.INFO_UPDATES
            )
    }

data class AppNotificationDomain(
    val id: Long,
    val tripId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val createdAt: LocalDateTime,
    val read: Boolean = false
)

fun TripDomain.currentBudgetNotificationType(): NotificationType {

    val spent = optionalExpenses.sumOf { it.amount }
    val remaining = mainBudget - spent
    val percentageRemaining = 100 - ((spent * 100) / mainBudget)

    return when {
        remaining < 0.0 -> NotificationType.BUDGET_EXCEEDED
        remaining == 0.0 -> NotificationType.BUDGET_FINISHED
        percentageRemaining <= 15.0 -> NotificationType.BUDGET_LOW
        else -> NotificationType.BUDGET_OK
    }
}