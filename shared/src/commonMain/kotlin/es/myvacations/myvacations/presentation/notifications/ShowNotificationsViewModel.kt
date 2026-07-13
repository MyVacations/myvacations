package es.myvacations.myvacations.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.usecase.eventsusecase.DeleteNotificationUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.SelectAllNotificationsUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.UpdateNotificationUseCase
import es.myvacations.myvacations.presentation.events.AppNotificationListUiState
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import es.myvacations.myvacations.presentation.mapper.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowNotificationsViewModel(
    private val selectAllNotificationsUseCase: SelectAllNotificationsUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AppNotificationListUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            selectAllNotificationsUseCase().collect { notificationDomain ->
                _uiState.value =
                    _uiState.value.copy(notifications = notificationDomain.map { notificationDomain ->
                        notificationDomain.toUiState()
                    })
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