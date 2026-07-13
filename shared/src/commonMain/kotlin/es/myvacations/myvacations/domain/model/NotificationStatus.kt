package es.myvacations.myvacations.domain.model

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.notification_filter_all
import myvacations.shared.generated.resources.notification_filter_budgets
import myvacations.shared.generated.resources.notification_filter_expenses
import myvacations.shared.generated.resources.notification_filter_info
import myvacations.shared.generated.resources.notification_filter_trips
import myvacations.shared.generated.resources.trip_detail_active
import myvacations.shared.generated.resources.trip_detail_all
import myvacations.shared.generated.resources.trip_detail_past
import myvacations.shared.generated.resources.trip_detail_upcoming
import org.jetbrains.compose.resources.stringResource

enum class NotificationStatus {
    ALL,
    TRIPS,
    EXPENSES,
    BUDGETS,
    INFO
}

@Composable
fun NotificationStatus.toName() = when (this) {
    NotificationStatus.ALL -> stringResource(Res.string.notification_filter_all)
    NotificationStatus.TRIPS -> stringResource(Res.string.notification_filter_trips)
    NotificationStatus.EXPENSES -> stringResource(Res.string.notification_filter_expenses)
    NotificationStatus.BUDGETS -> stringResource(Res.string.notification_filter_budgets)
    NotificationStatus.INFO -> stringResource(Res.string.notification_filter_info)
}