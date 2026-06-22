package es.myvacations.myvacations.core.extensions

import es.myvacations.myvacations.domain.model.TripDomain
import es.myvacations.myvacations.domain.model.TripStatus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun TripDomain.calculateStatus(): TripStatus {
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    return when {
        today < startDate -> TripStatus.PLANNED
        today > endDate -> TripStatus.COMPLETE
        else -> TripStatus.ACTIVE
    }
}