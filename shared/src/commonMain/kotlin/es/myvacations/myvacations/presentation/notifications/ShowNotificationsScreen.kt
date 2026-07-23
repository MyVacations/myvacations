package es.myvacations.myvacations.presentation.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.toRelativeTime
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.domain.events.NotificationType
import es.myvacations.myvacations.domain.events.matches
import es.myvacations.myvacations.domain.model.NotificationStatus
import es.myvacations.myvacations.domain.model.toName
import es.myvacations.myvacations.presentation.events.AppNotificationUiState
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.notification_delete_all
import myvacations.shared.generated.resources.notification_new_ones
import myvacations.shared.generated.resources.notification_old_ones
import myvacations.shared.generated.resources.notification_read_all
import myvacations.shared.generated.resources.notification_title
import myvacations.shared.generated.resources.notification_uptodate
import myvacations.shared.generated.resources.relative_days_ago
import myvacations.shared.generated.resources.relative_hours_ago
import myvacations.shared.generated.resources.relative_minutes_ago
import myvacations.shared.generated.resources.relative_now
import myvacations.shared.generated.resources.relative_yesterday
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShowNotificationsScreen(
    showNotificationsViewModel: ShowNotificationsViewModel = koinViewModel(),
    onDismiss: () -> Unit = {},
    onClick: (tripId: String) -> Unit = {}
) {
    val uiState by showNotificationsViewModel.uiState.collectAsState()

    SystemBackHandler()
    {
        showNotificationsViewModel.cleanUi()
        onDismiss()
    }

    var selectedFilterStatus by remember { mutableStateOf(NotificationStatus.ALL) }
    val notificationsFilterNotRead = uiState.notifications.filter { notificationUiState ->
        !notificationUiState.read && selectedFilterStatus.matches(notificationUiState.type)
    }
    val notificationsFilterRead = uiState.notifications.filter { notificationUiState ->
        notificationUiState.read && selectedFilterStatus.matches(notificationUiState.type)
    }
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                selectedFilterStatus = NotificationStatus.ALL
                onDismiss()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Text(
                text = stringResource(Res.string.notification_title),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(NotificationStatus.entries.toTypedArray()) { filter ->
                Surface(
                    modifier = Modifier.clickable {
                        selectedFilterStatus = filter
                    },
                    shape = RoundedCornerShape(50),
                    color = if (filter == selectedFilterStatus)
                        MaterialTheme.colorScheme.primary
                    else
                        Color(0xFFF2F5F7)
                ) {
                    Text(
                        filter.toName(),
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 10.dp
                        ),
                        color = if (filter == selectedFilterStatus)
                            Color.Black
                        else
                            Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(
                            Res.string.notification_new_ones,
                            notificationsFilterNotRead.size
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        modifier = Modifier.clickable {
                            showNotificationsViewModel.updateAsReadAllNotificationsAvailable(
                                notificationsFilterNotRead
                            )
                        })
                    {
                        Text(
                            text = stringResource(Res.string.notification_read_all),
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            notificationsNotRead(notificationsFilterNotRead, functionItemsNotification = {
                items(notificationsFilterNotRead.sortedByDescending { it.createdAt }) { notification ->
                    InsideNotificationNotRead(notification, showNotificationsViewModel, onClick)
                }
            }, functionNoNotifications = {
                item {
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            })
            //OldOnes
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(
                            Res.string.notification_old_ones,
                            notificationsFilterRead.size
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        modifier = Modifier.clickable {
                            showNotificationsViewModel.deleteAllNotificationsAvailable(uiState.notifications)
                        })
                    {
                        Text(
                            text = stringResource(Res.string.notification_delete_all),
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            notificationsRead(notificationsFilterRead, functionItemsNotification = {
                items(notificationsFilterRead.sortedByDescending { it.createdAt }) { notification ->
                    InsideNotificationRead(notification, showNotificationsViewModel, onClick)
                }
            }, functionNoNotifications = {
                item {
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            })
            item {
                ItemUiNoNotificationCard(notificationsFilterNotRead, notificationsFilterRead)
            }
        }
    }
}

@Composable
fun InsideNotificationNotRead(
    notification: AppNotificationUiState,
    showNotificationsViewModel: ShowNotificationsViewModel,
    onClick: (String) -> Unit,
) {
    NotificationItemScreen(notification = notification) {
        when (notification.type) {
            NotificationType.INFO_GENERIC_WELCOME, NotificationType.INFO_UPDATES -> showNotificationsViewModel.updateAsReadSelecting(
                notification
            )

            else -> {
                showNotificationsViewModel.updateAsReadSelecting(notification)
                onClick(notification.tripId)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun InsideNotificationRead(
    notification: AppNotificationUiState,
    showNotificationsViewModel: ShowNotificationsViewModel,
    onClick: (String) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = { distance -> distance * 0.5f }
    )
    val scale by animateFloatAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            1.2f
        } else {
            1f
        },
        label = "DeleteScale"
    )
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            showNotificationsViewModel.deleteANotification(notification)
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeToDismissBoxBackground(scale, dismissState)
        }
    ) {
        NotificationItemScreen(true, notification) {
            if (notification.type != NotificationType.INFO_GENERIC_WELCOME && notification.type != NotificationType.INFO_UPDATES) onClick(
                notification.tripId
            )
        }
    }
}

fun LazyListScope.notificationsNotRead(
    notificationsFilterNotRead: List<AppNotificationUiState>,
    functionItemsNotification: LazyListScope.() -> Unit,
    functionNoNotifications: LazyListScope.() -> Unit
) =
    if (notificationsFilterNotRead.isNotEmpty()) functionItemsNotification() else functionNoNotifications()

fun LazyListScope.notificationsRead(
    notificationsFilterRead: List<AppNotificationUiState>,
    functionItemsNotification: LazyListScope.() -> Unit,
    functionNoNotifications: LazyListScope.() -> Unit
) =
    if (notificationsFilterRead.isNotEmpty()) functionItemsNotification() else functionNoNotifications()

@Composable
fun ItemUiNoNotificationCard(
    notificationsFilterNotRead: List<AppNotificationUiState>,
    notificationsFilterRead: List<AppNotificationUiState>
) {
    if (notificationsFilterNotRead.isEmpty() && notificationsFilterRead.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.notification_uptodate),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun SwipeToDismissBoxBackground(
    scale: Float,
    dismissState: SwipeToDismissBoxState
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Transparent
        },
        label = "SwipeBackgroundColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.SwipeLeft,
            contentDescription = null,
            modifier = Modifier.scale(scale),
            tint = Color.White
        )
    }
}

@Composable
private fun NotificationItemScreen(
    read: Boolean = false,
    notification: AppNotificationUiState,
    changeClickable: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                changeClickable()
            }
            .padding(vertical = 12.dp).padding(start = 16.dp, end = if (!read) 16.dp else 36.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (!read) {
            Box(
                modifier = Modifier
                    .padding(top = 28.dp, end = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2D8CFF))
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))
            val now = stringResource(Res.string.relative_now)
            val yesterday = stringResource(Res.string.relative_yesterday)
            val minutesAgo = stringResource(Res.string.relative_minutes_ago)
            val hoursAgo = stringResource(Res.string.relative_hours_ago)
            val daysAgo = stringResource(Res.string.relative_days_ago)

            Text(
                text = notification.createdAt.toRelativeTime(
                    nowString = now,
                    minutesAgo = {
                        minutesAgo.replace($$"%1$s", it.toString())
                    },
                    hoursAgo = {
                        hoursAgo.replace($$"%1$s", it.toString())
                    },
                    yesterday = yesterday,
                    daysAgo = {
                        daysAgo.replace($$"%1$s", it.toString())
                    }
                ))
        }
    }
}