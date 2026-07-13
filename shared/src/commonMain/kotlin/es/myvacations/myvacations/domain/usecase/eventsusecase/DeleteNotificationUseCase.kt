package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.NotificationRepository

class DeleteNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(id: Long, tripId: String) =
        notificationRepository.deleteNotification(id, tripId)
}