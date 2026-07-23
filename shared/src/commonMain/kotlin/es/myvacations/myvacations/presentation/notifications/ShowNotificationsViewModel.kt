package es.myvacations.myvacations.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.events.messageFor
import es.myvacations.myvacations.domain.events.titleFor
import es.myvacations.myvacations.domain.repository.AppInfoRepository
import es.myvacations.myvacations.domain.usecase.eventsusecase.DeleteNotificationUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.SelectAllNotificationsUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.UpdateNotificationUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.presentation.events.AppNotificationListUiState
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import es.myvacations.myvacations.presentation.mapper.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowNotificationsViewModel(
    private val selectTripByIdUseCase: GetTripByIdUseCase,
    private val selectAllNotificationsUseCase: SelectAllNotificationsUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val appInfoRepository: AppInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AppNotificationListUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun cleanUi() {
        _uiState.value = AppNotificationListUiState()
    }

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            selectAllNotificationsUseCase().collect { notificationsDomain ->
                val serverMessage = appInfoRepository.messageFromServer()
                val notifications = notificationsDomain.map { notification ->

                    val trip = selectTripByIdUseCase.invokeWithoutFlow(notification.tripId)

                    notification.copy(
                        title = notification.type.titleFor(),
                        message = notification.type.messageFor(
                            trip,
                            serverMessage
                        )
                    ).toUiState()
                }

                _uiState.value = _uiState.value.copy(
                    notifications = notifications
                )
            }
        }
    }

    fun updateAsReadAllNotificationsAvailable(notifications: List<AppNotificationUiState>) {
        notifications.forEach { notification ->
            updateAsReadSelecting(notification)
        }
    }

    fun updateAsReadSelecting(notification: AppNotificationUiState) {
        updateNotificationUseCase.invoke(notification.id, notification.tripId)
    }

    fun deleteAllNotificationsAvailable(notifications: List<AppNotificationUiState>) {
        notifications.forEach { notification ->
            deleteANotification(notification)
        }
    }

    fun deleteANotification(notification: AppNotificationUiState) {
        deleteNotificationUseCase.invoke(notification.id, notification.tripId)
    }
}