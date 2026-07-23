package es.myvacations.myvacations.presentation.tripdetail

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.repository.CalendarAddEventResult
import es.myvacations.myvacations.domain.usecase.eventsusecase.AddEventToCalendarUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.DeleteTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.GetTravelersUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.InsertTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.UpdateMainTravelerUseCase
import es.myvacations.myvacations.domain.usecase.travelersusecase.UpdateTravelerUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.DeleteTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.UpdateTripUseCase
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.presentation.events.CalendarColor
import es.myvacations.myvacations.presentation.mapper.toDomainModel
import es.myvacations.myvacations.presentation.mapper.toUiState
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

class TripDetailsViewModel(
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val getSettingUseCase: GetSettingsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val editTrip: UpdateTripUseCase,
    private val selectedTravelers: GetTravelersUseCase,
    private val updateTravelers: UpdateTravelerUseCase,
    private val deleteTravelerUseCase: DeleteTravelerUseCase,
    private val insertTravelerUseCase: InsertTravelerUseCase,
    private val updateMainTravelerUseCase: UpdateMainTravelerUseCase,
    private val calendarEventUseCase: AddEventToCalendarUseCase
) : ViewModel() {
    private val _eventsSync = MutableSharedFlow<CalendarAddEventResult>()
    val eventsSync = _eventsSync.asSharedFlow()

    private val _uiState = MutableStateFlow(
        TripDetailUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    calendarStatus = it.calendarStatus.copy(
                        calendarPermission = calendarEventUseCase.isPermissionGranted()
                    )
                )
            }
        }
    }

    fun observeCalendarChanges() {
        calendarEventUseCase.observeCalendarChanges {
            viewModelScope.launch {
                getCalendars()
                updateEventCreated()
            }
        }
    }

    fun stopObservingCalendarChanges() {
        calendarEventUseCase.stopObservingCalendarChanges()
    }

    fun cleanUi() {
        _uiState.value = TripDetailUiState()
    }

    fun setLoading(loading: Boolean) = _uiState.update { it.copy(isLoading = loading) }

    fun updateEventCreated() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    calendarStatus = it.calendarStatus.copy(
                        calendarSync = calendarEventUseCase.isEventCreated(_uiState.value.tripUiState.id)
                    )
                )
            }
        }
    }

    fun updateColor(color: CalendarColor) {
        _uiState.update {
            it.copy(
                calendarStatus = it.calendarStatus.copy(
                    colorForCalendar = color
                )
            )
        }
    }

    fun updateStatus(status: CalendarStatus) {
        _uiState.update {
            it.copy(
                calendarStatus = it.calendarStatus.copy(
                    calendarStatus = status
                )
            )
        }
    }

    fun getCalendars() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    calendarStatus = it.calendarStatus.copy(
                        calendarsAvailable = calendarEventUseCase.getCalendars(uiState.value.tripUiState.id)
                    )
                )
            }
        }
    }

    fun startSendingCalendarRequest() {
        viewModelScope.launch {
            _uiState.value.let { state ->
                state.tripUiState.let { trip ->
                    val invoke = calendarEventUseCase.invoke(
                        calendarId = state.calendarStatus.calendarID,
                        id = trip.id,
                        title = trip.titleTrip,
                        place = trip.placeTrip.displayName(),
                        startDate = trip.startDate,
                        endDate = trip.endDate,
                        color = state.calendarStatus.colorForCalendar,
                        status = state.calendarStatus.calendarStatus
                    )
                    updateStatus(CalendarStatus.INSERT)
                    when (invoke) {
                        is CalendarAddEventResult.Success -> {
                            _eventsSync.emit(CalendarAddEventResult.Success(invoke.status))
                        }

                        is CalendarAddEventResult.PermissionDenied -> {
                            _eventsSync.emit(CalendarAddEventResult.PermissionDenied)
                        }

                        CalendarAddEventResult.NoCalendarAvailable -> {
                            _eventsSync.emit(CalendarAddEventResult.NoCalendarAvailable)
                        }

                        is CalendarAddEventResult.Error -> {
                            _eventsSync.emit(CalendarAddEventResult.Error(invoke.exception))
                        }
                    }
                }
            }
        }
    }

    fun getTravelers(tripId: String) {
        viewModelScope.launch {
            selectedTravelers(tripId).collect { travelers ->
                _uiState.update {
                    it.copy(
                        travelers = travelers.map { travelerDomain ->
                            travelerDomain.toUiState()
                        })
                }
            }
        }
    }

    fun getTripById(id: String) {
        viewModelScope.launch {
            val startTime = Clock.System.now().toEpochMilliseconds()
            getTripByIdUseCase(id).collect { tripDomain ->
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                val remaining = 1000 - elapsed
                if (remaining > 0) {
                    delay(remaining.milliseconds)
                }
                _uiState.update {
                    it.copy(
                        tripUiState = tripDomain?.toUiState() ?: TripUiState(),
                        isLoading = false,
                        calendarStatus = it.calendarStatus.copy(
                            calendarSync = calendarEventUseCase.isEventCreated(
                                tripDomain?.id ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    fun getCurrency() {
        viewModelScope.launch {
            getSettingUseCase().collect { settings ->
                _uiState.update {
                    it.copy(currency = settings?.preferredCurrency ?: Currency.EURO)
                }
            }
        }
    }

    fun onTabSelected(tab: TripDetailsTab) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            deleteTripUseCase(id)
            updateStatus(CalendarStatus.DELETE)
            _uiState.value.let { state ->
                state.tripUiState.let { trip ->
                    calendarEventUseCase.invoke(
                        calendarId = state.calendarStatus.calendarID,
                        id = trip.id,
                        title = trip.titleTrip,
                        place = trip.placeTrip.displayName(),
                        startDate = trip.startDate,
                        endDate = trip.endDate,
                        color = state.calendarStatus.colorForCalendar,
                        status = state.calendarStatus.calendarStatus
                    )
                }
            }
            updateStatus(CalendarStatus.INSERT)
        }
    }

    fun updateFavourite(favourite: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    tripUiState = it.tripUiState.copy(favourite = favourite)
                )
            }
            editTrip.invoke(_uiState.value.tripUiState.copy(favourite = favourite).toDomainModel())
        }
    }

    fun onTravelerNameChanged(id: String, name: String, isMainTraveler: Boolean) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    travelers = state.travelers.map { traveler ->
                        if (traveler.id == id) {
                            traveler.copy(travelerName = name)
                        } else {
                            traveler
                        }
                    })
            }

            if (isMainTraveler) updateMainTravelerUseCase.invoke(
                TravelersDomain(
                    id,
                    _uiState.value.tripUiState.id,
                    name,
                    true
                )
            ) else
                updateTravelers.invoke(
                    TravelersDomain(
                        id,
                        _uiState.value.tripUiState.id,
                        name,
                        false
                    )
                )
        }
    }

    fun onInsertTraveler() {
        viewModelScope.launch {
            val trip = _uiState.value.tripUiState
            val traveler = TravelersDomain(
                id = Uuid.random().toHexString(),
                tripId = trip.id,
                travelerName = "",
                isMainTraveler = false
            )
            insertTravelerUseCase(traveler)
            editTrip(
                trip.copy(
                    travelers = trip.travelers + 1
                ).toDomainModel()
            )
            _uiState.update {
                it.copy(
                    travelers = it.travelers + traveler.toUiState(),
                    tripUiState = it.tripUiState.copy(
                        travelers = it.tripUiState.travelers + 1
                    )
                )
            }
        }
    }

    fun onDeleteTraveler(id: String, tripId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    travelers = state.travelers.filter { it.id != id && it.tripId == tripId }
                        .map { traveler ->
                            traveler
                        })
            }
            deleteTravelerUseCase.invoke(id, tripId)
        }
    }

    fun updateCalendarId(toString: String) {
        _uiState.update {
            it.copy(
                calendarStatus = it.calendarStatus.copy(
                    calendarID = toString
                )
            )
        }
    }
}