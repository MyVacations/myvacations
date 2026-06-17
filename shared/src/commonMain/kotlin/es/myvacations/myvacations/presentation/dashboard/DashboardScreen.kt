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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.domain.model.Greetings
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.presentation.utils.DefaultDashboardTrip
import es.myvacations.myvacations.presentation.utils.DefaultTrip
import es.myvacations.myvacations.presentation.utils.StatusChip
import es.myvacations.myvacations.presentation.utils.painter
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.app_title
import myvacations.shared.generated.resources.average_saves_from_budget
import myvacations.shared.generated.resources.average_spent
import myvacations.shared.generated.resources.greetings_afternoon
import myvacations.shared.generated.resources.greetings_evening
import myvacations.shared.generated.resources.greetings_morning
import myvacations.shared.generated.resources.greetings_night
import myvacations.shared.generated.resources.past_trips
import myvacations.shared.generated.resources.total_spent
import myvacations.shared.generated.resources.total_trips
import myvacations.shared.generated.resources.upcoming
import myvacations.shared.generated.resources.upcoming_trips
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Preview(showBackground = true)
@Composable
fun UserAvatar(
    initials: String = "JR"
) {
    Surface(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(initials)
        }
    }
}

@Composable
fun DashboardHeader(uiState: DashboardUiState) {
    val greetingText = when (uiState.greetings) {
        Greetings.MORNING -> stringResource(
            Res.string.greetings_morning,
            uiState.userName
        )

        Greetings.AFTERNOON -> stringResource(
            Res.string.greetings_afternoon,
            uiState.userName
        )

        Greetings.EVENING -> stringResource(
            Res.string.greetings_evening,
            uiState.userName
        )

        Greetings.NIGHT -> stringResource(
            Res.string.greetings_night,
            uiState.userName
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

        UserAvatar()
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardStatSection(onStatisticsClick: () -> Unit = {}) {
    Spacer(modifier = Modifier.height(16.dp))
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
                value = 1.toString(),//stats.totalTrips.toString(),
                label = stringResource(Res.string.total_trips),
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                color = Color(0xFF2B80FF),
            )

            StatCard(
                onStatisticsClick = onStatisticsClick,
                modifier = Modifier.weight(1f),//stats.totalTrips.toString(),
                value = 1.toString(),
                label = stringResource(Res.string.total_spent),
                icon = Icons.Default.Wallet,
                color = Color(0xFFFF6060),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatCard(
                onStatisticsClick = onStatisticsClick,
                modifier = Modifier.weight(1f),//stats.totalTrips.toString(),
                value = 1.toString(),
                label = stringResource(Res.string.average_spent),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                color = Color(0xFFFFBE42),
            )
            StatCard(
                onStatisticsClick,
                modifier = Modifier.weight(1f),//value = stats.totalTrips.toString(),
                1.toString(),
                label = stringResource(Res.string.upcoming),
                icon = Icons.Default.CalendarToday,
                color = Color(0xFF9C42FF),
            )
        }

        StatCard(
            onStatisticsClick = onStatisticsClick,
            modifier = Modifier.fillMaxWidth()
                .height(120.dp),//stats.averageSavesFromBudget.toString(),
            value = 1.toString(),
            label = stringResource(Res.string.average_saves_from_budget),
            icon = Icons.Default.AttachMoney,
            color = Color(0xFF4CAF50),
            averageBudgetCard = true
        )
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
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
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
    DashboardContent(uiState, onEditTripClick, onStatisticsClick)
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onEditTripClick: (tripId: String) -> Unit,
    onStatisticsClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        item {
            DashboardHeader(uiState)
        }
        item {
            ActualTripCard(onEditTripClick)
        }
        item {
            DashboardStatSection(onStatisticsClick)
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

@Preview(showBackground = true)
@Composable
fun ActualTripCard(onEditTripClick: (tripId: String) -> Unit = {}) {
    val trip = DefaultTrip.tripActual
    //TODO usar el id del trip
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                )
        ) {
            StatusChip(trip.tripStatus, trip.tripStatus == TripStatus.ACTIVE)
            Spacer(Modifier.height(8.dp))
            Text(
                text = trip.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${
                    DateFormatter.formatTripDate(trip.startDate)
                } - ${
                    DateFormatter.formatTripDate(trip.endDate)
                }",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium
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
            userName = "Jesus"
        ),
        {},
        {}
    )
}