package es.myvacations.myvacations.domain.model

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.trip_detail_active
import myvacations.shared.generated.resources.trip_detail_all
import myvacations.shared.generated.resources.trip_detail_favourite
import myvacations.shared.generated.resources.trip_detail_past
import myvacations.shared.generated.resources.trip_detail_upcoming
import org.jetbrains.compose.resources.stringResource

enum class TripStatus {
    ALL,
    FAVOURITE,
    PLANNED,
    ACTIVE,
    COMPLETE
}

@Composable
fun TripStatus.toName() = when (this) {
    TripStatus.ALL -> stringResource(Res.string.trip_detail_all)
    TripStatus.FAVOURITE -> stringResource(Res.string.trip_detail_favourite)
    TripStatus.PLANNED -> stringResource(Res.string.trip_detail_upcoming)
    TripStatus.ACTIVE -> stringResource(Res.string.trip_detail_active)
    TripStatus.COMPLETE -> stringResource(Res.string.trip_detail_past)
}