package es.myvacations.myvacations.presentation.trips

import androidx.lifecycle.ViewModel
import es.myvacations.myvacations.domain.model.TripDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TripViewModel : ViewModel() {
    private val _filterTrip = MutableStateFlow(
        TripsUiState()
    )
    val filter: StateFlow<TripsUiState> = _filterTrip

    fun addTrip(trip: TripDomain) {

    }

    fun getTrips() = _filterTrip.value.getFilteredTrips()

}