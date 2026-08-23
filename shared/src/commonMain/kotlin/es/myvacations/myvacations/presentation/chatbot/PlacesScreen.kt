package es.myvacations.myvacations.presentation.chatbot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun ItemPlace(
    uiState: ChatUiState,
    place: ElementsFoundUiState,
    openPlaceDetails: (ElementsFoundUiState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = {
                openPlaceDetails(place)
            })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconForPlaceType(place.type ?: ""),
                contentDescription = null
            )
        }

        Spacer(Modifier.width(12.dp))

        // Información
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = place.name ?: "Sin nombre",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(2.dp))

            place.distance?.let {
                Text(
                    text = if(uiState.outOfLimits) ">500 m" else formatDistance(it),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}

fun iconForPlaceType(type: String): ImageVector {
    return when {
        type.contains("restaurant", ignoreCase = true) -> Icons.Default.Restaurant
        type.contains("cafe", ignoreCase = true) -> Icons.Default.LocalCafe
        type.contains("bar", ignoreCase = true) -> Icons.Default.LocalBar
        type.contains("pub", ignoreCase = true) -> Icons.Default.LocalBar
        type.contains("fast_food", ignoreCase = true) -> Icons.Default.Fastfood
        type.contains("bakery", ignoreCase = true) -> Icons.Default.BakeryDining
        type.contains("supermarket", ignoreCase = true) -> Icons.Default.ShoppingCart
        type.contains("pharmacy", ignoreCase = true) -> Icons.Default.LocalPharmacy
        type.contains("hospital", ignoreCase = true) -> Icons.Default.LocalHospital
        type.contains("fuel", ignoreCase = true) -> Icons.Default.LocalGasStation
        else -> Icons.Default.Place
    }
}

fun formatDistance(distanceMeters: Double): String {
    return when {
        distanceMeters < 1000 -> {
            "${distanceMeters.roundToInt()} m"
        }

        distanceMeters < 10_000 -> {
            val kilometers =
                (distanceMeters / 1000 * 10).roundToInt() / 10.0

            "$kilometers km"
        }

        else -> {
            "${(distanceMeters / 1000).roundToInt()} km"
        }
    }
}