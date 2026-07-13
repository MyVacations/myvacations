package es.myvacations.myvacations.presentation.events

import es.myvacations.myvacations.domain.events.NotificationType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class AppNotificationUiState(
    val id: Long = 0L,
    val tripId: String = "",
    val type: NotificationType = NotificationType.TRIP_CREATED,
    val title: String = "",
    val message: String = "",
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val read: Boolean = false
)

data class AppNotificationListUiState(
    val notifications: List<AppNotificationUiState> = emptyList()
)