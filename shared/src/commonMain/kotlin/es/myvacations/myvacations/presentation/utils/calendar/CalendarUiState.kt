package es.myvacations.myvacations.presentation.utils.calendar

import com.kizitonwose.calendar.core.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(Clock.System, timeZone = TimeZone.currentSystemDefault()),
)
