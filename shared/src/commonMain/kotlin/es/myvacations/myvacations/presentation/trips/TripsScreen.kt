package es.myvacations.myvacations.presentation.trips

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import es.myvacations.myvacations.presentation.utils.DefaultTrip

@Composable
fun TripsScreen(onEditTripClick: (tripId: String) -> Unit) {
    val trip = DefaultTrip.tripActual

    Button(onClick = {
        onEditTripClick(trip.id)
    }) {
        Text("Hola")
    }
}