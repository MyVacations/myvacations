package es.myvacations.myvacations.presentation.utils

import es.myvacations.myvacations.domain.model.Trip
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripStatus
import kotlinx.datetime.LocalDate

object DefaultTrip {
    val tripActual = Trip(
        "1",
        "Playa actual",
        Country.SPAIN,
        TripStatus.ACTIVE,
        LocalDate(2026, 6, 16),
        LocalDate(2026, 6, 20),
        2,
        1000.0,
        1000.0,
        emptyList(),
        "Ejemplo",
        TripCover.BEACH
    )
    val tripPast = Trip(
        "2",
        "Playa pasado",
        Country.SPAIN,
        TripStatus.COMPLETE,
        LocalDate(2026, 6, 13),
        LocalDate(2026, 6, 16),
        2,
        1000.0,
        1000.0,
        emptyList(),
        "Ejemplo",
        TripCover.BEACH
    )
    val tripUpcoming = Trip(
        "3",
        "Playa futuro",
        Country.SPAIN,
        TripStatus.PLANNED,
        LocalDate(2026, 6, 25),
        LocalDate(2026, 6, 30),
        2,
        1000.0,
        1000.0,
        emptyList(),
        "Ejemplo",
        TripCover.BEACH
    )
}