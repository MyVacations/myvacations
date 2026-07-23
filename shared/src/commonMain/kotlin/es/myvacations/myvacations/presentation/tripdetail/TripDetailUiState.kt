package es.myvacations.myvacations.presentation.tripdetail

import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.calendar.CalendarUiState

data class TripDetailUiState(
    val selectedTab: TripDetailsTab = TripDetailsTab.OVERVIEW,
    val tripUiState: TripUiState = TripUiState(),
    val travelers: List<TravelerUiState> = emptyList(),
    val currency: Currency = Currency.EURO,
    val calendarStatus: CalendarUiState = CalendarUiState(),
    val isLoading: Boolean = true
)