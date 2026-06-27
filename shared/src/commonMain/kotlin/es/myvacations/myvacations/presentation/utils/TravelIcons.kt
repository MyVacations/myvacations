package es.myvacations.myvacations.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.activity_ATTRACTIONS
import myvacations.shared.generated.resources.activity_BEACH_ACCESS
import myvacations.shared.generated.resources.activity_DIRECTIONS
import myvacations.shared.generated.resources.activity_EVENT
import myvacations.shared.generated.resources.activity_EXPLORE
import myvacations.shared.generated.resources.activity_LANDSCAPE
import myvacations.shared.generated.resources.activity_PARK
import myvacations.shared.generated.resources.activity_PHOTO_CAMERA
import myvacations.shared.generated.resources.activity_PLACE
import myvacations.shared.generated.resources.activity_RESTAURANT
import org.jetbrains.compose.resources.stringResource

enum class TravelIcon {
    PLACE,
    EXPLORE,
    DIRECTIONS_CAR,
    DIRECTIONS_BUS,
    DIRECTIONS_BOAT,
    DIRECTIONS_BIKE,
    DIRECTIONS_WALK,
    DIRECTIONS_TRANSIT,
    DIRECTIONS_TRAIN,
    DIRECTIONS_TAXI,
    RESTAURANT,
    EVENT,
    BEACH_ACCESS,
    PARK,
    LANDSCAPE,
    PHOTO_CAMERA,
    ATTRACTIONS
}

@Composable
fun TravelIcon.toImageVector(): ImageVector =
    when (this) {
        TravelIcon.PLACE -> Icons.Default.Place
        TravelIcon.EXPLORE -> Icons.Default.Explore

        TravelIcon.DIRECTIONS_CAR -> Icons.Default.DirectionsCar
        TravelIcon.DIRECTIONS_BUS -> Icons.Default.DirectionsBus
        TravelIcon.DIRECTIONS_BOAT -> Icons.Default.DirectionsBoat
        TravelIcon.DIRECTIONS_BIKE -> Icons.AutoMirrored.Filled.DirectionsBike
        TravelIcon.DIRECTIONS_WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
        TravelIcon.DIRECTIONS_TRANSIT -> Icons.Default.DirectionsTransit
        TravelIcon.DIRECTIONS_TRAIN -> Icons.Default.Train
        TravelIcon.DIRECTIONS_TAXI,
            -> Icons.Default.LocalTaxi

        TravelIcon.RESTAURANT -> Icons.Default.Restaurant
        TravelIcon.EVENT -> Icons.Default.Event
        TravelIcon.BEACH_ACCESS -> Icons.Default.BeachAccess
        TravelIcon.PARK -> Icons.Default.Park
        TravelIcon.LANDSCAPE -> Icons.Default.Landscape
        TravelIcon.PHOTO_CAMERA -> Icons.Default.PhotoCamera
        TravelIcon.ATTRACTIONS -> Icons.Default.Attractions
    }

@Composable
fun ImageVector.iconColor(): Color =
    when (this) {
        // Ubicación
        Icons.Default.Place,
        Icons.Default.Explore -> Color(0xFF09D794)

        // Transporte terrestre
        Icons.Default.DirectionsCar,
        Icons.Default.DirectionsBus,
        Icons.Default.DirectionsBoat,
        Icons.AutoMirrored.Filled.DirectionsBike,
        Icons.AutoMirrored.Filled.DirectionsWalk,
        Icons.Default.DirectionsTransit,
        Icons.Default.Train,
        Icons.Default.LocalTaxi ->
            Color(0xFF06B6D4)

        // Comida
        Icons.Default.Restaurant ->
            Color(0xFFF59E0B)

        // Actividades
        Icons.Default.Event,
        Icons.Default.BeachAccess,
        Icons.Default.Park,
        Icons.Default.Landscape,
        Icons.Default.PhotoCamera,
        Icons.Default.Attractions ->
            Color(0xFF93FF86)

        else ->
            MaterialTheme.colorScheme.primary
    }

fun TravelIcon.iconColor(): Color =
    when (this) {
        TravelIcon.PLACE, TravelIcon.EXPLORE -> Color(0xFF09D794)
        TravelIcon.DIRECTIONS_CAR,
        TravelIcon.DIRECTIONS_BUS,
        TravelIcon.DIRECTIONS_BOAT,
        TravelIcon.DIRECTIONS_BIKE,
        TravelIcon.DIRECTIONS_WALK,
        TravelIcon.DIRECTIONS_TRANSIT,
        TravelIcon.DIRECTIONS_TRAIN,
        TravelIcon.DIRECTIONS_TAXI -> Color(0xFF06B6D4)

        TravelIcon.RESTAURANT -> Color(0xFFF59E0B)
        TravelIcon.EVENT, TravelIcon.BEACH_ACCESS, TravelIcon.PARK, TravelIcon.LANDSCAPE, TravelIcon.PHOTO_CAMERA, TravelIcon.ATTRACTIONS -> Color(
            0xFF93FF86
        )
    }

@Composable
fun TravelIcon.toName(): String = when (this) {
    TravelIcon.PLACE -> stringResource(Res.string.activity_PLACE)
    TravelIcon.EXPLORE -> stringResource(Res.string.activity_EXPLORE)
    TravelIcon.DIRECTIONS_CAR,
    TravelIcon.DIRECTIONS_BUS,
    TravelIcon.DIRECTIONS_BOAT,
    TravelIcon.DIRECTIONS_BIKE,
    TravelIcon.DIRECTIONS_WALK,
    TravelIcon.DIRECTIONS_TRANSIT,
    TravelIcon.DIRECTIONS_TRAIN,
    TravelIcon.DIRECTIONS_TAXI -> stringResource(Res.string.activity_DIRECTIONS)
    TravelIcon.RESTAURANT -> stringResource(Res.string.activity_RESTAURANT)
    TravelIcon.EVENT -> stringResource(Res.string.activity_EVENT)
    TravelIcon.BEACH_ACCESS -> stringResource(Res.string.activity_BEACH_ACCESS)
    TravelIcon.PARK -> stringResource(Res.string.activity_PARK)
    TravelIcon.LANDSCAPE -> stringResource(Res.string.activity_LANDSCAPE)
    TravelIcon.PHOTO_CAMERA -> stringResource(Res.string.activity_PHOTO_CAMERA)
    TravelIcon.ATTRACTIONS -> stringResource(Res.string.activity_ATTRACTIONS)
}