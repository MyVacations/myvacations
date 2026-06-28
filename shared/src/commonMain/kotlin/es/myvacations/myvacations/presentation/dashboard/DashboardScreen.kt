package es.myvacations.myvacations.presentation.dashboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import es.myvacations.myvacations.core.extensions.shortCurrencyWhen100000
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.presentation.settings.SettingsUiState
import es.myvacations.myvacations.presentation.utils.DefaultDashboardTrip
import es.myvacations.myvacations.presentation.utils.StatusChip
import es.myvacations.myvacations.presentation.utils.painter
import es.myvacations.myvacations.presentation.utils.toCurrencyName
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.actual_trip_addone
import myvacations.shared.generated.resources.app_title
import myvacations.shared.generated.resources.average_saves_from_budget
import myvacations.shared.generated.resources.average_spent
import myvacations.shared.generated.resources.greetings_afternoon
import myvacations.shared.generated.resources.greetings_evening
import myvacations.shared.generated.resources.greetings_morning
import myvacations.shared.generated.resources.greetings_night
import myvacations.shared.generated.resources.guest_user
import myvacations.shared.generated.resources.noTrips
import myvacations.shared.generated.resources.past_trips
import myvacations.shared.generated.resources.total_spent
import myvacations.shared.generated.resources.total_trips
import myvacations.shared.generated.resources.upcoming
import myvacations.shared.generated.resources.upcoming_trips
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onEditTripClick: (tripId: String) -> Unit,
    onStatisticsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshGreetings()
        onPauseOrDispose { }
    }
    DashboardContent(uiState, onEditTripClick, onStatisticsClick, viewModel::initials)
}

@Preview(showBackground = true)
@Composable
fun DashboardContent(
    uiState: DashboardUiState = DashboardUiState(),
    onEditTripClick: (tripId: String) -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    initials: (userName: String) -> String = { "" }
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        item {
            DashboardHeader(uiState, initials)
        }
        item {
            ActualTripCard(uiState, onEditTripClick)
        }
        item {
            DashboardStatSection(uiState, onStatisticsClick)
        }
        item {
            if (uiState.upcomingTrips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.upcoming_trips),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                uiState.upcomingTrips.forEach { trip ->
                    DefaultDashboardTrip(trip = trip, onClick = {
                        onEditTripClick(trip.id)
                    })
                }
            }
        }
        item {
            if (uiState.pastTrips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.past_trips),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                uiState.pastTrips.forEach { trip ->
                    DefaultDashboardTrip(trip = trip, onClick = {
                        onEditTripClick(trip.id)
                    })
                }
            }
        }
        item { Spacer(modifier = Modifier.height(65.dp)) }
    }
}

@Composable
fun DashboardHeader(uiState: DashboardUiState, initials: (userName: String) -> String) {
    val greetingText = when (uiState.greetings) {
        Greetings.MORNING -> stringResource(
            Res.string.greetings_morning,
            uiState.settings.userName.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.guest_user)
        )

        Greetings.AFTERNOON -> stringResource(
            Res.string.greetings_afternoon,
            uiState.settings.userName.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.guest_user)
        )

        Greetings.EVENING -> stringResource(
            Res.string.greetings_evening,
            uiState.settings.userName.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.guest_user)
        )

        Greetings.NIGHT -> stringResource(
            Res.string.greetings_night,
            uiState.settings.userName.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.guest_user)
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        Column {
            Text(
                text = greetingText,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(Res.string.app_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))
        }

        UserAvatar(uiState, initials)
    }
}

@Preview(showBackground = true)
@Composable
fun UserAvatar(
    uiState: DashboardUiState = DashboardUiState(),
    initials: (userName: String) -> String = { "JR" }
) {
    Surface(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(initials(uiState.settings.userName.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.guest_user)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActualTripCard(
    uiState: DashboardUiState = DashboardUiState(),
    onEditTripClick: (tripId: String) -> Unit = {}
) {
    val trip = uiState.currentTrip
    if (uiState.currentTrip != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = {
                    onEditTripClick(trip.id)
                })
        ) {
            Image(
                painter = trip.cover.painter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 24.dp
                    )
            ) {
                StatusChip(trip.tripStatus)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = trip.titleTrip,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${
                        DateFormatter.formatTripDate(trip.startDate ?: trip.today)
                    } - ${
                        DateFormatter.formatTripDate(trip.endDate ?: trip.today)
                    }",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                Text(
                    text = stringResource(Res.string.actual_trip_addone),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardStatSection(
    uiState: DashboardUiState = DashboardUiState(),
    onStatisticsClick: () -> Unit = {}
) {
    Spacer(modifier = Modifier.height(16.dp))
    if (uiState.stats.totalTrips != 0) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatCard(
                    onStatisticsClick = onStatisticsClick,
                    modifier = Modifier.weight(1f),
                    value = uiState.stats.totalTrips.toString(),
                    label = stringResource(Res.string.total_trips),
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    color = Color(0xFF2B80FF),
                )

                StatCard(
                    onStatisticsClick = onStatisticsClick,
                    modifier = Modifier.weight(1f),
                    value = uiState.stats.totalSpent.shortCurrencyWhen100000() + " " + uiState.settings.currency.toCurrencyName(),
                    label = stringResource(Res.string.total_spent),
                    icon = Icons.Default.Wallet,
                    color = Color(0xFFFF6060)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatCard(
                    onStatisticsClick = onStatisticsClick,
                    modifier = Modifier.weight(1f),
                    value = uiState.stats.averageTripCost.shortCurrencyWhen100000() + " " + uiState.settings.currency.toCurrencyName(),
                    label = stringResource(Res.string.average_spent),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = Color(0xFFFFBE42),
                )
                StatCard(
                    onStatisticsClick,
                    modifier = Modifier.weight(1f),
                    uiState.stats.upcomingTrips.toString(),
                    label = stringResource(Res.string.upcoming),
                    icon = Icons.Default.CalendarToday,
                    color = Color(0xFF9C42FF),
                )
            }

            StatCard(
                onStatisticsClick = onStatisticsClick,
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp),
                value = uiState.stats.averageSavesFromBudget.shortCurrencyWhen100000() + " " + uiState.settings.currency.toCurrencyName(),
                label = stringResource(Res.string.average_saves_from_budget),
                icon = Icons.Default.AttachMoney,
                color = Color(0xFF4CAF50),
                averageBudgetCard = true
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(Res.drawable.noTrips),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun StatCard(
    onStatisticsClick: () -> Unit,
    modifier: Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    averageBudgetCard: Boolean = false
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = {
            onStatisticsClick()
        }),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (averageBudgetCard) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = color.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color
                    )
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview() {
    DashboardContent(
        uiState = DashboardUiState(
            greetings = Greetings.MORNING,
            settings = SettingsUiState("Jesus")
        ),
        initials = { "" }
    )
}