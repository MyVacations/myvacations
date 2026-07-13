package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.NotificationRepository

class UpdateNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(id:Long,tripId: String) =
        notificationRepository.updateReadNotification(id,tripId)
}