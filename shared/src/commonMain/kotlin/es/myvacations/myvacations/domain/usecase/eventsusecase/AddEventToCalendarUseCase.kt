package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.DeviceCalendarRepository
import es.myvacations.myvacations.presentation.events.CalendarColor
import es.myvacations.myvacations.presentation.utils.calendar.CalendarStatus
import kotlinx.datetime.LocalDate

class AddEventToCalendarUseCase(
    private val deviceCalendarRepository: DeviceCalendarRepository
) {
    fun observeCalendarChanges(onChange: () -> Unit) =
        deviceCalendarRepository.observeCalendarChanges(onChange)

    fun stopObservingCalendarChanges() = deviceCalendarRepository.stopObservingCalendarChanges()
    fun isPermissionGranted() = deviceCalendarRepository.hasCalendarPermission()
    fun getCalendars(id: String) = deviceCalendarRepository.getCalendars(id)
    suspend fun isEventCreated(id: String) = deviceCalendarRepository.eventExists(id)
    suspend operator fun invoke(
        calendarId: String,
        id: String,
        title: String,
        place: String,
        startDate: LocalDate,
        endDate: LocalDate,
        color: CalendarColor,
        status: CalendarStatus
    ) = deviceCalendarRepository.addEditDeleteEvent(
            calendarId = calendarId,
            tripId = id,
            title = title,
            place = place,
            startDate = startDate,
            endDate = endDate,
            color = color,
            status = status
        )
}