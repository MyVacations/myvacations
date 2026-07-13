package es.myvacations.myvacations.domain.mapper

import es.myvacations.myvacations.data.database.NotificationData
import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.events.NotificationType
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import kotlinx.datetime.LocalDateTime


fun NotificationData.toDomainModel() = AppNotificationDomain(
    id = id,
    tripId = tripId,
    type = NotificationType.valueOf(type),
    createdAt = LocalDateTime.parse(createdAt),
    read = read
)

fun AppNotificationDomain.toUiModel() = AppNotificationUiState(
    id = id,
    tripId = tripId,
    type = type,
    title = title,
    message = message,
    createdAt = createdAt,
    read = read
)