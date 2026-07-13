package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.NotificationRepository

class SelectNotificationsInATripUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(tripId: String) =
        notificationRepository.selectNotificationsByIdTravel(tripId)
}