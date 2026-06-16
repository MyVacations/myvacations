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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.domain.model.Greetings
import es.myvacations.myvacations.domain.model.Trip
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.presentation.tripdetail.TripDetailScreen
import es.myvacations.myvacations.presentation.trips.TripStatus
import es.myvacations.myvacations.presentation.utils.StatusChip
import es.myvacations.myvacations.presentation.utils.painter
import kotlinx.datetime.LocalDate
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.app_title
import myvacations.shared.generated.resources.greetings_afternoon
import myvacations.shared.generated.resources.greetings_evening
import myvacations.shared.generated.resources.greetings_morning
import myvacations.shared.generated.resources.greetings_night
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Preview(showBackground = true)
@Composable
fun UserAvatar(
    initials: String = "JR"
) {
    Surface(
        modifier = Modifier.size(48.dp),
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
                style = MaterialTheme.typography.headlineMedium
            )
        }

        UserAvatar("JD")
    }
}

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshGreetings()
        onPauseOrDispose { }
    }
    DashboardContent(uiState)

}

@Composable
fun DashboardContent(uiState: DashboardUiState) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp)) {
        item {
            DashboardHeader(uiState)
        }
        item {
            //TODO Mirar ChatGPT y sacar esto de aqui
            ActualTripCard()
        }

        items(50) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) { Text("Trip ${index + 1}") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActualTripCard() {
    val trip = Trip(
        "1",
        "Ejemplo",
        TripStatus.ACTIVE,
        LocalDate(2026, 6, 15),
        LocalDate(2026, 6, 20),
        2,
        1000.0,
        1000.0,
        emptyList(),
        "Ejemplo",
        TripCover.BEACH
    )
    val tripDetailOn = remember {
        mutableStateOf(false)
    }
    if (tripDetailOn.value) TripDetailScreen(onDismiss = { tripDetailOn.value = false })
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = {
                tripDetailOn.value = true
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
        )
    )
}