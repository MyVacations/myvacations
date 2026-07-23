package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.shortCurrency
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.domain.repository.CalendarAddEventResult
import es.myvacations.myvacations.presentation.events.CalendarColor
import es.myvacations.myvacations.presentation.events.CalendarPermissionHandler
import es.myvacations.myvacations.presentation.utils.AppDropDown
import es.myvacations.myvacations.presentation.utils.LegendItem
import es.myvacations.myvacations.presentation.utils.StatusCard
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import es.myvacations.myvacations.presentation.utils.calendar.DeviceCalendar
import es.myvacations.myvacations.presentation.utils.calendar.loadNameFromAccountType
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.new_trip_add
import myvacations.shared.generated.resources.new_trip_clear
import myvacations.shared.generated.resources.new_trip_error
import myvacations.shared.generated.resources.nocolors
import myvacations.shared.generated.resources.snackbarCalendarSync
import myvacations.shared.generated.resources.snackbarCalendarSyncDelete
import myvacations.shared.generated.resources.snackbarCalendarSyncUpdate
import myvacations.shared.generated.resources.snackbarNoCalendarFind
import myvacations.shared.generated.resources.syncawait
import myvacations.shared.generated.resources.syncedit
import myvacations.shared.generated.resources.synceditdelete
import myvacations.shared.generated.resources.syncselectcalendar
import myvacations.shared.generated.resources.syncselectcalendartitle
import myvacations.shared.generated.resources.syncwith
import myvacations.shared.generated.resources.syncwithnotnow
import myvacations.shared.generated.resources.trip_detail_budget
import myvacations.shared.generated.resources.trip_detail_delete
import myvacations.shared.generated.resources.trip_detail_delete_details
import myvacations.shared.generated.resources.trip_detail_header_spent
import myvacations.shared.generated.resources.trip_detail_header_yourcost
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripDetailScreen(
    viewModel: TripDetailsViewModel = koinViewModel(),
    tripId: String, onDismiss: () -> Unit,
    onEditTripClick: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogDelete = remember { mutableStateOf(false) }
    val dialogRequestingAsyncPermission = remember { mutableStateOf(false) }
    val dialogRequestingCalendarPermissions = remember { mutableStateOf(false) }
    val dialogDeleteCalendarSync = remember { mutableStateOf(false) }
    val dialogAvailableCalendars = remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        viewModel.setLoading(true)
        viewModel.getCurrency()
        viewModel.getTravelers(tripId)
        viewModel.getTripById(tripId)
    }
    LaunchedEffect(Unit)
    {
        viewModel.eventsSync.collect { event ->
            when (event) {
                is CalendarAddEventResult.Success -> {
                    dialogRequestingAsyncPermission.value = false
                    viewModel.updateEventCreated()
                    onShowSnackbar(
                        getString(
                            when (event.status) {
                                CalendarStatus.INSERT -> Res.string.snackbarCalendarSync
                                CalendarStatus.UPDATE -> Res.string.snackbarCalendarSyncUpdate
                                CalendarStatus.DELETE -> Res.string.snackbarCalendarSyncDelete
                            }
                        )
                    )
                }

                is CalendarAddEventResult.PermissionDenied -> {
                    dialogRequestingCalendarPermissions.value = false
                }

                CalendarAddEventResult.NoCalendarAvailable -> {
                    onShowSnackbar(
                        getString(Res.string.snackbarNoCalendarFind)
                    )
                }

                is CalendarAddEventResult.Error -> {

                    onShowSnackbar(
                        getString(Res.string.new_trip_error)
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.observeCalendarChanges()

        onDispose {
            viewModel.stopObservingCalendarChanges()
        }
    }
    CalendarPermissionHandler(
        onUpdateResponse = {
            viewModel.getCalendars()
            dialogAvailableCalendars.value = true
            dialogRequestingCalendarPermissions.value = false
        },
        dialogRequestingCalendarPermissions = dialogRequestingCalendarPermissions.value
    )
    DialogCalendars(
        startCalendarRequest = {
            viewModel.updateCalendarId(it.id.toString())
            dialogAvailableCalendars.value = false
            if (it.isSync) dialogDeleteCalendarSync.value =
                true else dialogRequestingAsyncPermission.value = true
        },
        updateDialog = { dialogAvailableCalendars.value = it },
        dialogAvailableCalendars = dialogAvailableCalendars.value,
        calendarList = uiState.calendarStatus.calendarsAvailable
    )

    DialogScreenRequestingPermissionForCalendarSync(
        startSendingCalendarRequest = {
            viewModel.startSendingCalendarRequest()
        },
        updateColor = {
            viewModel.updateColor(it)
        },
        updateDialog = { dialogRequestingAsyncPermission.value = it },
        dialogRequestingPermission = dialogRequestingAsyncPermission.value,
        listColor = uiState.calendarStatus.let { it.calendarsAvailable.find { calendar -> it.calendarID == calendar.id.toString() }?.colorList ?: emptyList() }
    )

    DialogEditDeleteCalendarSync(
        startSendingCalendarRequest = {
            viewModel.updateStatus(it)
        },
        updateDialogRequestingPermission = {
            dialogRequestingAsyncPermission.value = it
        },
        updateDirectlyAsync = {
            viewModel.startSendingCalendarRequest()
        },
        updateDialog = { dialogDeleteCalendarSync.value = it },
        dialogDeleteCalendarSync = dialogDeleteCalendarSync.value
    )

    DialogDeleteTrip(
        deletingTrip = {
            viewModel.deleteTrip(tripId)
            onDismiss()
        },
        updateDeleteTrip = {
            dialogDelete.value = it
        },
        dialogDelete = dialogDelete.value
    )

    SystemBackHandler {
        viewModel.cleanUi()
        onDismiss()
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    } else {
        Scaffold { _ ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 12.dp)
            ) {
                item {
                    TripHeader(
                        uiState,
                        onBackClick = onDismiss,
                        onEditClick = { onEditTripClick() },
                        onDeleteClick = {
                            dialogDelete.value = true
                        },
                        onFavouriteClick = {
                            viewModel.updateFavourite(!uiState.tripUiState.favourite)
                        },
                        updateCheckPermissions = {
                            dialogRequestingCalendarPermissions.value = it
                        }
                    )
                }
                item {
                    TripCostCard(uiState = uiState)
                }
                item {
                    PrimaryTabRow(
                        selectedTabIndex = uiState.selectedTab.ordinal
                    ) {
                        TripDetailsTab.entries.forEach { tab ->
                            Tab(
                                selected = uiState.selectedTab == tab,
                                onClick = {
                                    viewModel.onTabSelected(tab)
                                },
                                text = {
                                    Text(tab.toTripDetailName())
                                }
                            )
                        }
                    }
                }
                when (uiState.selectedTab) {
                    TripDetailsTab.OVERVIEW -> item {
                        OverViewScreen(uiState)
                    }

                    TripDetailsTab.EXPENSES -> item {
                        ExpensesScreen(uiState)
                    }

                    TripDetailsTab.TRAVELER -> item {
                        /*
                        TravelersScreen(
                            uiState,
                            viewModel::onTravelerNameChanged,
                            viewModel::onInsertTraveler,
                            viewModel::onDeleteTraveler
                        )

                         */
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDeleteTrip(
    deletingTrip: () -> Unit,
    updateDeleteTrip: (Boolean) -> Unit,
    dialogDelete: Boolean
) {
    if (dialogDelete) {
        AlertDialog(
            onDismissRequest = { updateDeleteTrip(false) },
            title = { Text(stringResource(Res.string.trip_detail_delete)) },
            text = { Text(stringResource(Res.string.trip_detail_delete_details)) },
            confirmButton = {
                Text(
                    modifier = Modifier.clickable(onClick = {
                        updateDeleteTrip(false)
                        deletingTrip()
                    }),
                    text = stringResource(Res.string.accept),
                    color = MaterialTheme.colorScheme.error
                )
            },
            dismissButton = {
                Text(modifier = Modifier.clickable(onClick = {
                    updateDeleteTrip(false)
                }), text = stringResource(Res.string.cancel))
            }
        )
    }
}

@Composable
fun DialogCalendars(
    startCalendarRequest: (DeviceCalendar) -> Unit,
    updateDialog: (Boolean) -> Unit,
    dialogAvailableCalendars: Boolean,
    calendarList: List<DeviceCalendar>,
) {
    if (dialogAvailableCalendars) {
        AlertDialog(
            onDismissRequest = { updateDialog(false) },
            title = {
                Text(
                    stringResource(Res.string.syncselectcalendar),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(calendarList) { calendar ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    startCalendarRequest(calendar)
                                    //Agregar el otro dialog dialogRequestingAsyncPermission
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (calendar.isSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                                contentDescription = "SyncOnOrOff",
                                tint = if (calendar.isSync) Color.Cyan else Color.White.copy(
                                    alpha = 0.7f
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = calendar.accountType.loadNameFromAccountType(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = stringResource(
                                        Res.string.syncselectcalendartitle,
                                        calendar.accountName
                                    ),
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(Res.string.syncawait))
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripHeader(
    trip: TripDetailUiState = TripDetailUiState(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onFavouriteClick: () -> Unit = {},
    updateCheckPermissions: (Boolean) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Image(
            painter = trip.tripUiState.cover.painter(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeaderActionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBackClick
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeaderActionButton(
                        icon = Icons.Default.Edit,
                        onClick = onEditClick
                    )
                    Spacer(Modifier.width(8.dp))
                    HeaderActionButton(
                        icon = Icons.Default.Delete,
                        containerColor = Color(0xFFFF4D4D),
                        onClick = onDeleteClick
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                ) {
                    StatusCard(trip.tripUiState.tripStatus)
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = trip.tripUiState.titleTrip,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${
                            DateFormatter.formatTripDate(trip.tripUiState.startDate)
                        } - ${
                            DateFormatter.formatTripDate(trip.tripUiState.endDate)
                        }",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = {
                        updateCheckPermissions(true)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (trip.calendarStatus.calendarSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                        contentDescription = null,
                        tint = if (trip.calendarStatus.calendarSync) Color.Cyan else Color.White.copy(
                            alpha = 0.7f
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onFavouriteClick,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (trip.tripUiState.favourite) Color.Yellow else Color.White.copy(
                            alpha = 0.7f
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderActionButton(
    icon: ImageVector,
    containerColor: Color = Color.Black.copy(alpha = 0.35f),
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .background(
                color = containerColor,
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripCostCard(
    uiState: TripDetailUiState = TripDetailUiState(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF006D77)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.trip_detail_header_yourcost).uppercase(),
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.tripUiState.individualCost.shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Spacer(modifier.weight(1f))
                Row {
                    Text(
                        text = "${stringResource(Res.string.trip_detail_budget)}:",
                        textAlign = TextAlign.End,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "${uiState.tripUiState.mainBudget.shortCurrency()} ${uiState.currency.toCurrencySymbol()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    Text(
                        text = "${stringResource(Res.string.trip_detail_header_spent)}:",
                        modifier = Modifier.width(70.dp),
                        textAlign = TextAlign.End,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "${uiState.tripUiState.totalOptionalExpenses.shortCurrency()} ${uiState.currency.toCurrencySymbol()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier.weight(1f))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier.weight(1f))

                when {
                    uiState.tripUiState.mainBudget == 0.0 -> LegendItem(
                        Color.LightGray,
                        "0.0",
                        Color.White,
                        MaterialTheme.typography.titleMedium,
                        Icons.Filled.AttachMoney
                    )

                    uiState.tripUiState.lowBudget > 15.0 -> LowBudgetLegendItem(uiState)
                    uiState.tripUiState.remainingBudget < 0.0 -> EmptyLegendItem(uiState)

                    else -> LegendItem(
                        Color(0xFFB45400),
                        if (uiState.tripUiState.mainBudget != 0.0 && uiState.tripUiState.totalOptionalExpenses != 0.0) uiState.tripUiState.remainingBudget.shortCurrency() + " " + uiState.currency.toCurrencySymbol() else "-",
                        Color.White,
                        MaterialTheme.typography.titleMedium,
                        Icons.Filled.AttachMoney
                    )
                }
            }
        }
    }
}

@Composable
fun LowBudgetLegendItem(uiState: TripDetailUiState) {
    LegendItem(
        Color(0xFF11AC1F),
        if (uiState.tripUiState.mainBudget != 0.0 && uiState.tripUiState.totalOptionalExpenses != 0.0) uiState.tripUiState.remainingBudget.shortCurrency() + " " + uiState.currency.toCurrencySymbol() else "-",
        Color.White,
        MaterialTheme.typography.titleMedium,
        Icons.Filled.AttachMoney
    )
}

@Composable
fun EmptyLegendItem(uiState: TripDetailUiState) {
    LegendItem(
        Color.Red,
        if (uiState.tripUiState.mainBudget != 0.0 && uiState.tripUiState.totalOptionalExpenses != 0.0) uiState.tripUiState.remainingBudget.shortCurrency() + " " + uiState.currency.toCurrencySymbol() else "-",
        Color.White,
        MaterialTheme.typography.titleMedium,
        Icons.Filled.AttachMoney
    )
}

@Composable
fun DialogScreenRequestingPermissionForCalendarSync(
    startSendingCalendarRequest: () -> Unit,
    updateColor: (CalendarColor) -> Unit,
    updateDialog: (Boolean) -> Unit,
    dialogRequestingPermission: Boolean,
    listColor: List<CalendarColor> = emptyList()
) {
    val colorToUpdate = remember { mutableStateOf(CalendarColor()) }
    if (dialogRequestingPermission) {
        AlertDialog(
            onDismissRequest = { updateDialog(false) },
            title = {
                Text(stringResource(Res.string.syncwith))
            },
            text = {
                if(listColor.isNotEmpty())
                {
                    AppDropDown(
                        modifier = Modifier.fillMaxWidth(),
                        menuModifier = Modifier
                            .width(200.dp)
                            .heightIn(max = 300.dp),
                        items = listColor,
                        selectedItem = colorToUpdate.value,
                        onItemSelected = { colorToUpdate.value = it },
                        itemLabel = {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            color = Color(it.color),
                                            shape = CircleShape
                                        )
                                )
                            }
                        },
                    )
                }
                else{
                    Text(stringResource(Res.string.nocolors))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        updateColor(colorToUpdate.value)
                        startSendingCalendarRequest()
                    }
                ) {
                    Text(stringResource(Res.string.new_trip_add))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        updateDialog(false)
                    }
                ) {
                    Text(stringResource(Res.string.syncwithnotnow))
                }
            }
        )
    }
}

@Composable
fun DialogEditDeleteCalendarSync(
    startSendingCalendarRequest: (CalendarStatus) -> Unit,
    updateDialog: (Boolean) -> Unit,
    updateDialogRequestingPermission: (Boolean) -> Unit,
    dialogDeleteCalendarSync: Boolean,
    updateDirectlyAsync: () -> Unit
) {
    if (dialogDeleteCalendarSync) {
        AlertDialog(
            onDismissRequest = { updateDialog(false) },
            title = {
                Text(stringResource(Res.string.synceditdelete))
            },
            confirmButton = {},
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            startSendingCalendarRequest(CalendarStatus.UPDATE)
                            updateDialogRequestingPermission(true)
                            updateDialog(false)
                        }
                    ) {
                        Text(stringResource(Res.string.syncedit))
                    }
                    Spacer(Modifier.height(6.dp))
                    TextButton(
                        onClick = {
                            startSendingCalendarRequest(CalendarStatus.DELETE)
                            updateDirectlyAsync()
                            updateDialog(false)
                        }
                    ) {
                        Text(stringResource(Res.string.new_trip_clear))
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            updateDialog(false)
                        }
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        )
    }
}