package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.runtime.Composable
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.trip_detail_expenses
import myvacations.shared.generated.resources.trip_detail_overview
import myvacations.shared.generated.resources.trip_detail_travelers
import org.jetbrains.compose.resources.stringResource

enum class TripDetailsTab {
    OVERVIEW, EXPENSES, TRAVELER
}

@Composable
fun TripDetailsTab.toTripDetailName(): String = when (this) {
    TripDetailsTab.OVERVIEW -> stringResource(Res.string.trip_detail_overview)
    TripDetailsTab.EXPENSES -> stringResource(Res.string.trip_detail_expenses)
    TripDetailsTab.TRAVELER -> stringResource(Res.string.trip_detail_travelers)
}