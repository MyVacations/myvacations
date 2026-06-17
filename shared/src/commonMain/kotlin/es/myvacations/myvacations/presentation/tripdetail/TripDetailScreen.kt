package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.myvacations.myvacations.core.navigation.SystemBackHandler
import es.myvacations.myvacations.core.utils.DateFormatter
import es.myvacations.myvacations.domain.model.Trip
import es.myvacations.myvacations.presentation.utils.DefaultTrip
import es.myvacations.myvacations.presentation.utils.StatusChip
import es.myvacations.myvacations.presentation.utils.painter

@Composable
fun TripDetailScreen(onDismiss: () -> Unit) {
    val trip = DefaultTrip.tripActual

    SystemBackHandler {
        onDismiss()
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            item {
                TripHeader(trip, onBackClick = onDismiss, onEditClick = {}, onDeleteClick = {})
            }
            item {
                Text("Contenido")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripHeader(
    trip: Trip = DefaultTrip.tripActual,
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
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
            StatusChip(trip.tripStatus)
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