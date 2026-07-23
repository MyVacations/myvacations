package es.myvacations.myvacations.presentation.utils.calendar

import com.kizitonwose.calendar.core.now
import es.myvacations.myvacations.presentation.events.CalendarColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(
        Clock.System,
        timeZone = TimeZone.currentSystemDefault()
    ),
    val calendarID: String = "",
    val calendarPermission: Boolean = false,
    val colorForCalendar: CalendarColor = CalendarColor(),
    val calendarSync: Boolean = false,
    val calendarsAvailable: List<DeviceCalendar> = emptyList(),
    val calendarStatus: CalendarStatus = CalendarStatus.INSERT
)
