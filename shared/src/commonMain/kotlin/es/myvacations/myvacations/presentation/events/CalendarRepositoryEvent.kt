package es.myvacations.myvacations.presentation.events

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.domain.repository.CalendarAddEventResult
import es.myvacations.myvacations.domain.repository.DeviceCalendarRepository
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import es.myvacations.myvacations.presentation.utils.calendar.DeviceCalendar
import kotlinx.datetime.LocalDate

expect class GetDeviceCalendarRepository() : DeviceCalendarRepository {
    override fun observeCalendarChanges(onChange: () -> Unit)
    override fun stopObservingCalendarChanges()
    override fun getCalendars(id: String): List<DeviceCalendar>
    override fun hasCalendarPermission(): Boolean
    override suspend fun eventExists(id: String): Boolean

    override suspend fun addEditDeleteEvent(
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

@Composable
expect fun CalendarPermissionHandler(
    onUpdatePermission: (CalendarAddEventResult) -> Unit,
    dialogRequestingCalendarPermissions: Boolean
)