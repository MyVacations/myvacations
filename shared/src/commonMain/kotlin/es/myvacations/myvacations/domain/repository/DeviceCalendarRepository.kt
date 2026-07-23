package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.presentation.events.CalendarColor
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import es.myvacations.myvacations.presentation.utils.calendar.DeviceCalendar
import kotlinx.datetime.LocalDate

sealed interface CalendarAddEventResult {
    data class Success(val status: CalendarStatus) : CalendarAddEventResult
    data object PermissionDenied : CalendarAddEventResult
    data object NoCalendarAvailable : CalendarAddEventResult
    data class Error(val exception: Exception) : CalendarAddEventResult
}

interface DeviceCalendarRepository {
    fun observeCalendarChanges(
        onChange: () -> Unit
    )
    fun stopObservingCalendarChanges()
    fun getCalendars(id: String): List<DeviceCalendar>
    fun hasCalendarPermission(): Boolean
    suspend fun eventExists(id: String): Boolean
    suspend fun addEditDeleteEvent(
        calendarId: String,
        tripId: String,
        title: String,
        place: String,
        startDate: LocalDate,
        endDate: LocalDate,
        color: CalendarColor,
        status: CalendarStatus
    ): CalendarAddEventResult
}