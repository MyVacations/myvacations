package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.NotificationRepository

class SelectAllNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke() = notificationRepository.selectAllNotifications()
}