package es.myvacations.myvacations.presentation.trips

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.extensions.shortCurrencyWhen1000
import es.myvacations.myvacations.core.extensions.shortCurrencyWhen100000
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.model.flag
import es.myvacations.myvacations.domain.model.toName
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.utils.StatusCard
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencyName
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.in_x_days
import myvacations.shared.generated.resources.subtitle_dashboard
import myvacations.shared.generated.resources.trip_detail_header_costperperson_
import myvacations.shared.generated.resources.trip_detail_search_default_value
import myvacations.shared.generated.resources.trips_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripsScreen(
    viewModel: TripViewModel = koinViewModel(),
    openTripDetail: (tripId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchValue by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf(TripStatus.ALL) }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
    {
        Text(
            text = stringResource(Res.string.trips_title),
            style = MaterialTheme.typography.headlineSmall
        )
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                CircularProgressIndicator()
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchValue,
                onValueChange = { searchValue = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(stringResource(Res.string.trip_detail_search_default_value))
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TripStatus.entries.toTypedArray()) { filter ->
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
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(
                    uiState.trips.filter {
                        it.titleTrip.contains(searchValue, ignoreCase = true)
                                && if (selectedFilterStatus != TripStatus.ALL) it.tripStatus == selectedFilterStatus else true
                    }.sortedWith(
                        compareBy(
                            { !it.titleTrip.startsWith(searchValue, ignoreCase = true) },
                            { it.titleTrip.length }
                        )
                    )) { trip ->
                    TripCard(trip, onClick = { openTripDetail(trip.id) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripCard(
    trip: TripUiState = TripUiState(),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors()
    ) {
        Column {
            Box {
                Image(
                    painter = trip.cover.painter(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Row(modifier.padding(16.dp)) {
                    Spacer(modifier.weight(1f))
                    StatusCard(trip.tripStatus)
                }
            }

            Spacer(modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trip.placeTrip.flag,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = trip.titleTrip,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(
                            Res.string.subtitle_dashboard,
                            trip.placeTrip.displayName(),
                            trip.totalDays,
                            trip.travelers
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = trip.totalCost.shortCurrencyWhen100000() + " " + trip.currency.toCurrencyName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (trip.tripStatus == TripStatus.PLANNED) {
                        Text(
                            text = stringResource(
                                Res.string.in_x_days,
                                trip.remainingDays
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${
                    DateFormatter.formatTripDate(trip.startDate ?: trip.today)
                } - ${
                    DateFormatter.formatTripDate(trip.endDate ?: trip.today)
                }",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier.width(8.dp))
            Text(
                text = trip.costPerPerson.shortCurrencyWhen1000() + " " + trip.currency.toCurrencyName() + " /" + stringResource(
                    Res.string.trip_detail_header_costperperson_
                ),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}