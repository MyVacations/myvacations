package es.myvacations.myvacations.domain.events

import es.myvacations.myvacations.core.extensions.shortenTitle
import es.myvacations.myvacations.core.utils.AppInfo
import es.myvacations.myvacations.domain.model.NotificationStatus
import es.myvacations.myvacations.domain.model.TripDomain
import kotlinx.datetime.LocalDateTime
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.notification_budget_exceeded
import myvacations.shared.generated.resources.notification_budget_finished
import myvacations.shared.generated.resources.notification_budget_low
import myvacations.shared.generated.resources.notification_budget_ok
import myvacations.shared.generated.resources.notification_budget_title
import myvacations.shared.generated.resources.notification_info_title
import myvacations.shared.generated.resources.notification_trip_created
import myvacations.shared.generated.resources.notification_trip_delete
import myvacations.shared.generated.resources.notification_trip_expense_created
import myvacations.shared.generated.resources.notification_trip_expense_delete
import myvacations.shared.generated.resources.notification_trip_title
import myvacations.shared.generated.resources.notification_trip_update
import myvacations.shared.generated.resources.notification_welcome
import org.jetbrains.compose.resources.getString

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
    val title: String = "",
    val message: String = "",
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

suspend fun NotificationType.titleFor(): String =
    when (this) {
        NotificationType.TRIP_CREATED, NotificationType.TRIP_UPDATED, NotificationType.TRIP_DELETED, NotificationType.EXPENSE_ADDED, NotificationType.EXPENSE_DELETED -> getString(
            Res.string.notification_trip_title
        )

        NotificationType.BUDGET_OK, NotificationType.BUDGET_EXCEEDED, NotificationType.BUDGET_LOW, NotificationType.BUDGET_FINISHED -> getString(
            Res.string.notification_budget_title
        )

        NotificationType.INFO_UPDATES, NotificationType.INFO_GENERIC_WELCOME -> getString(Res.string.notification_info_title)
    }

suspend fun NotificationType.messageFor(
    trip: TripDomain?,
    ownMessage: String?
): String =
    when (this) {
        NotificationType.TRIP_CREATED -> getString(
            Res.string.notification_trip_created,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.TRIP_UPDATED -> getString(
            Res.string.notification_trip_update,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.TRIP_DELETED -> getString(
            Res.string.notification_trip_delete,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.EXPENSE_ADDED -> getString(
            Res.string.notification_trip_expense_created,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.EXPENSE_DELETED -> getString(
            Res.string.notification_trip_expense_delete,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.BUDGET_OK -> getString(
            Res.string.notification_budget_ok,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.BUDGET_LOW -> getString(
            Res.string.notification_budget_low,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.BUDGET_FINISHED -> getString(
            Res.string.notification_budget_finished,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.BUDGET_EXCEEDED -> getString(
            Res.string.notification_budget_exceeded,
            trip?.title?.shortenTitle() ?: ""
        )

        NotificationType.INFO_UPDATES -> ownMessage ?: ""
        NotificationType.INFO_GENERIC_WELCOME -> getString(
            Res.string.notification_welcome,
            AppInfo.appName
        )

    }