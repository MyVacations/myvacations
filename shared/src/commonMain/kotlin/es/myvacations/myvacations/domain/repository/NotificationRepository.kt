package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.events.AppNotificationDomain
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun selectNotificationsByIdTravel(tripId: String): Flow<List<AppNotificationDomain>>
    fun selectAllNotifications(): Flow<List<AppNotificationDomain>>
    fun insertNotification(notification: AppNotificationDomain)
    fun updateReadNotification(id: Long, tripId: String)
    fun deleteNotification(id: Long, tripId: String)
}