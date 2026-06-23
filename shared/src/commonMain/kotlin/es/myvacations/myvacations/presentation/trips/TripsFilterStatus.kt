package es.myvacations.myvacations.presentation.trips

import es.myvacations.myvacations.domain.model.TripStatus

data class TripsFilterStatus(val query: String = "", val status: TripStatus? = null)