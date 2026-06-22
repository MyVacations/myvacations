package es.myvacations.myvacations.presentation.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TravelIcon {
    FLIGHT,
    FLIGHT_TAKEOFF,
    FLIGHT_LAND,

    PLACE,
    MAP,
    PUBLIC,
    EXPLORE,
    NAVIGATION,

    HOTEL,

    DIRECTIONS_CAR,
    DIRECTIONS_BUS,
    DIRECTIONS_BOAT,
    DIRECTIONS_BIKE,
    DIRECTIONS_WALK,
    DIRECTIONS_TRANSIT,
    TRAIN,
    LOCAL_TAXI,

    RESTAURANT,
    LOCAL_CAFE,
    LOCAL_BAR,
    FASTFOOD,

    ATTACH_MONEY,
    ACCOUNT_BALANCE_WALLET,
    CREDIT_CARD,
    PAYMENTS,
    RECEIPT,

    EVENT,
    ACCESS_TIME,
    BEACH_ACCESS,
    PARK,
    LANDSCAPE,
    PHOTO_CAMERA,
    ATTRACTIONS
}

@Composable
fun TravelIcon.toImageVector(): ImageVector =
    when (this) {
        TravelIcon.FLIGHT -> Icons.Default.Flight
        TravelIcon.FLIGHT_TAKEOFF -> Icons.Default.FlightTakeoff
        TravelIcon.FLIGHT_LAND -> Icons.Default.FlightLand

        TravelIcon.PLACE -> Icons.Default.Place
        TravelIcon.MAP -> Icons.Default.Map
        TravelIcon.PUBLIC -> Icons.Default.Public
        TravelIcon.EXPLORE -> Icons.Default.Explore
        TravelIcon.NAVIGATION -> Icons.Default.Navigation

        TravelIcon.HOTEL -> Icons.Default.Hotel

        TravelIcon.DIRECTIONS_CAR -> Icons.Default.DirectionsCar
        TravelIcon.DIRECTIONS_BUS -> Icons.Default.DirectionsBus
        TravelIcon.DIRECTIONS_BOAT -> Icons.Default.DirectionsBoat
        TravelIcon.DIRECTIONS_BIKE -> Icons.AutoMirrored.Filled.DirectionsBike
        TravelIcon.DIRECTIONS_WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
        TravelIcon.DIRECTIONS_TRANSIT -> Icons.Default.DirectionsTransit
        TravelIcon.TRAIN -> Icons.Default.Train
        TravelIcon.LOCAL_TAXI -> Icons.Default.LocalTaxi

        TravelIcon.RESTAURANT -> Icons.Default.Restaurant
        TravelIcon.LOCAL_CAFE -> Icons.Default.LocalCafe
        TravelIcon.LOCAL_BAR -> Icons.Default.LocalBar
        TravelIcon.FASTFOOD -> Icons.Default.Fastfood

        TravelIcon.ATTACH_MONEY -> Icons.Default.AttachMoney
        TravelIcon.ACCOUNT_BALANCE_WALLET -> Icons.Default.AccountBalanceWallet
        TravelIcon.CREDIT_CARD -> Icons.Default.CreditCard
        TravelIcon.PAYMENTS -> Icons.Default.Payments
        TravelIcon.RECEIPT -> Icons.Default.Receipt

        TravelIcon.EVENT -> Icons.Default.Event
        TravelIcon.ACCESS_TIME -> Icons.Default.AccessTime
        TravelIcon.BEACH_ACCESS -> Icons.Default.BeachAccess
        TravelIcon.PARK -> Icons.Default.Park
        TravelIcon.LANDSCAPE -> Icons.Default.Landscape
        TravelIcon.PHOTO_CAMERA -> Icons.Default.PhotoCamera
        TravelIcon.ATTRACTIONS -> Icons.Default.Attractions
    }

@Composable
fun ImageVector.iconColor(): Color =
    when (this) {

        // Transporte aéreo
        Icons.Default.Flight,
        Icons.Default.FlightTakeoff,
        Icons.Default.FlightLand ->
            Color(0xFF60A5FA)

        // Ubicación
        Icons.Default.Place,
        Icons.Default.Map,
        Icons.Default.Public,
        Icons.Default.Explore,
        Icons.Default.Navigation ->
            Color(0xFF09D794)

        // Alojamiento
        Icons.Default.Hotel ->
            Color(0xFF8B5CF6)

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
        Icons.Default.Restaurant,
        Icons.Default.LocalCafe,
        Icons.Default.LocalBar,
        Icons.Default.Fastfood ->
            Color(0xFFF59E0B)

        // Dinero
        Icons.Default.AttachMoney,
        Icons.Default.AccountBalanceWallet,
        Icons.Default.CreditCard,
        Icons.Default.Payments,
        Icons.Default.Receipt ->
            Color(0xFF54FA93)

        // Actividades
        Icons.Default.Event,
        Icons.Default.AccessTime,
        Icons.Default.BeachAccess,
        Icons.Default.Park,
        Icons.Default.Landscape,
        Icons.Default.PhotoCamera,
        Icons.Default.Attractions ->
            Color(0xFF93FF86)

        else ->
            MaterialTheme.colorScheme.primary
    }