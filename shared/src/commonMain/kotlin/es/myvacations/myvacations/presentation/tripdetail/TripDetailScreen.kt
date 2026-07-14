package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.shortCurrency
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.presentation.utils.LegendItem
import es.myvacations.myvacations.presentation.utils.StatusCard
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencySymbol
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.accept
import myvacations.shared.generated.resources.cancel
import myvacations.shared.generated.resources.trip_detail_budget
import myvacations.shared.generated.resources.trip_detail_delete
import myvacations.shared.generated.resources.trip_detail_delete_details
import myvacations.shared.generated.resources.trip_detail_header_nobudget
import myvacations.shared.generated.resources.trip_detail_header_spent
import myvacations.shared.generated.resources.trip_detail_header_yourcost
import myvacations.shared.generated.resources.trip_detail_traveler_budget_exceed
import myvacations.shared.generated.resources.trip_detail_traveler_budget_healthy
import myvacations.shared.generated.resources.trip_detail_traveler_budget_low
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripDetailScreen(
    viewModel: TripDetailsViewModel = koinViewModel(),
    tripId: String, onDismiss: () -> Unit,
    onEditTripClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogDelete = remember { mutableStateOf(false) }

    LaunchedEffect(tripId) {
        viewModel.setLoading(true)
        viewModel.getCurrency()
        viewModel.getTravelers(tripId)
        viewModel.getTripById(tripId)
    }

    SystemBackHandler {
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
                        })
                }
                item {
                    if (dialogDelete.value) {
                        AlertDialog(
                            onDismissRequest = { dialogDelete.value = false },
                            title = { Text(stringResource(Res.string.trip_detail_delete)) },
                            text = { Text(stringResource(Res.string.trip_detail_delete_details)) },
                            confirmButton = {
                                Text(
                                    modifier = Modifier.clickable(onClick = {
                                        dialogDelete.value = false
                                        viewModel.deleteTrip(tripId)
                                        onDismiss()
                                    }),
                                    text = stringResource(Res.string.accept),
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            dismissButton = {
                                Text(modifier = Modifier.clickable(onClick = {
                                    dialogDelete.value = false
                                }), text = stringResource(Res.string.cancel))
                            }
                        )
                    }
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
                        TravelersScreen(
                            uiState,
                            viewModel::onTravelerNameChanged,
                            viewModel::onInsertTraveler,
                            viewModel::onDeleteTraveler
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripHeader(
    trip: TripDetailUiState = TripDetailUiState(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                )
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
                    DateFormatter.formatTripDate(trip.tripUiState.startDate ?: trip.tripUiState.today)
                } - ${
                    DateFormatter.formatTripDate(trip.tripUiState.endDate ?: trip.tripUiState.today)
                }",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
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

                when {
                    uiState.tripUiState.mainBudget == 0.0 -> LegendItem(
                        Color.LightGray,
                        stringResource(Res.string.trip_detail_header_nobudget),
                        Color.White,
                        MaterialTheme.typography.titleMedium
                    )

                    uiState.tripUiState.lowBudget > 15.0 -> LegendItem(
                        Color(0xFF11AC1F),
                        stringResource(Res.string.trip_detail_traveler_budget_healthy),
                        Color.White,
                        MaterialTheme.typography.titleMedium
                    )

                    uiState.tripUiState.remainingBudget < 0.0 -> LegendItem(
                        Color.Red,
                        stringResource(
                            Res.string.trip_detail_traveler_budget_exceed,
                            uiState.tripUiState.remainingBudget.times(-1)
                                .shortCurrency() + " " + uiState.currency.toCurrencySymbol(),
                        ),
                        Color.White,
                        MaterialTheme.typography.titleMedium
                    )

                    else -> LegendItem(
                        Color(0xFFB45400),
                        stringResource(Res.string.trip_detail_traveler_budget_low),
                        Color.White,
                        MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}