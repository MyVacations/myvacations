package es.myvacations.myvacations.presentation.utils

import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.model.TripStatus
import kotlinx.datetime.LocalDate

object DefaultTrip {
    val tripActual = TripDomain(
        "1",
        "Playa actual",
        Country.SPAIN,
        LocalDate(2026, 6, 16),
        LocalDate(2026, 6, 20),
        2,
        1,
        1000.0,
        1000.0,
        emptyList(),
        TripCover.BEACH
    )
    val tripPast = TripDomain(
        "2",
        "Playa pasado",
        Country.SPAIN,
        LocalDate(2026, 6, 13),
        LocalDate(2026, 6, 16),
        2, 1,
        1000.0,
        1000.0,
        emptyList(),
        TripCover.BEACH
    )
    val tripUpcoming = TripDomain(
        "3",
        "Playa futuro",
        Country.SPAIN,
        LocalDate(2026, 6, 25),
        LocalDate(2026, 6, 30),
        2, 1,
        1000.0,
        1000.0,
        emptyList(),
        TripCover.BEACH
    )
}