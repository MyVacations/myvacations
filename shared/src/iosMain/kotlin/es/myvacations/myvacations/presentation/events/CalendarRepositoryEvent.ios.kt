package es.myvacations.myvacations.presentation.events

import androidx.compose.runtime.Composable
import es.myvacations.myvacations.domain.repository.CalendarAddEventResult
import es.myvacations.myvacations.domain.repository.DeviceCalendarRepository
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import es.myvacations.myvacations.presentation.utils.calendar.DeviceCalendar
import kotlinx.datetime.LocalDate

actual class GetDeviceCalendarRepository actual constructor() :
    DeviceCalendarRepository {
    actual override fun observeCalendarChanges(onChange: () -> Unit) {
        //Not used
    }

    actual override fun stopObservingCalendarChanges() {
        //Not used
    }

    actual override fun getCalendars(id: String): List<DeviceCalendar> {
        return emptyList()
    }

    actual override fun hasCalendarPermission(): Boolean {
        //Not used
        return false
    }

    actual override suspend fun eventExists(id: String): Boolean {
        //Not used
        return false
    }

    actual override suspend fun addEditDeleteEvent(
        calendarId: String,
        tripId: String,
        title: String,
        place: String,
        startDate: LocalDate,
        endDate: LocalDate,
        color: CalendarColor,
        status: CalendarStatus
    ): CalendarAddEventResult {
        //Not used
        return CalendarAddEventResult.Success(CalendarStatus.DELETE)
    }
}

@Composable
actual fun CalendarPermissionHandler(
    onUpdateResponse: (Boolean) -> Unit,
    dialogRequestingCalendarPermissions: Boolean
) {
    //Not used
}