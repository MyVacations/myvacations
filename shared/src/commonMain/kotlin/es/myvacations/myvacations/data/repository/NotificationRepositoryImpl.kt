package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.TripLocalDataSource
import es.myvacations.myvacations.domain.events.AppNotificationDomain
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val localDataSource: TripLocalDataSource
) : NotificationRepository {
    override fun selectNotificationsByIdTravel(tripId: String): Flow<List<AppNotificationDomain>> {
        return localDataSource.selectNotificationsByIdTravel(tripId).map { entities ->
            entities.map { entity ->
                entity.toDomainModel()
            }
        }
    }

    override fun selectAllNotifications(): Flow<List<AppNotificationDomain>> {
        return localDataSource.selectAllNotifications().map { entities ->
            entities.map { entity ->
                entity.toDomainModel()
            }
        }
    }

    override fun insertNotification(notification: AppNotificationDomain) {
        localDataSource.insertNotification(
            tripId = notification.tripId,
            type = notification.type.name,
            title = notification.title,
            message = notification.message,
            createdAt = notification.createdAt.toString()
        )
    }

    override fun updateReadNotification(id: Long, tripId: String) {
        localDataSource.updateReadNotification(id,tripId)
    }

    override fun deleteNotification(id: Long, tripId: String) {
        localDataSource.deleteNotification(id, tripId)
    }
}

