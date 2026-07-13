package es.myvacations.myvacations.domain.usecase

import es.myvacations.myvacations.presentation.dashboard.Greetings
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDayPeriodUseCaseTest {

    private val getDayPeriodUseCase = GetDayPeriodUseCase()

    @Test
    fun invoke_morning_hours_returnsMorningGreetings() {
        assertEquals(Greetings.MORNING, getDayPeriodUseCase(5))
        assertEquals(Greetings.MORNING, getDayPeriodUseCase(8))
        assertEquals(Greetings.MORNING, getDayPeriodUseCase(11))
    }

    @Test
    fun invoke_afternoon_hours_returnsAfternoonGreetings() {
        assertEquals(Greetings.AFTERNOON, getDayPeriodUseCase(12))
        assertEquals(Greetings.AFTERNOON, getDayPeriodUseCase(15))
        assertEquals(Greetings.AFTERNOON, getDayPeriodUseCase(17))
    }

    @Test
    fun invoke_evening_hours_returnsEveningGreetings() {
        assertEquals(Greetings.EVENING, getDayPeriodUseCase(18))
        assertEquals(Greetings.EVENING, getDayPeriodUseCase(20))
        assertEquals(Greetings.EVENING, getDayPeriodUseCase(21))
    }

    @Test
    fun invoke_night_hours_returnsNightGreetings() {
        assertEquals(Greetings.NIGHT, getDayPeriodUseCase(22))
        assertEquals(Greetings.NIGHT, getDayPeriodUseCase(23))
        assertEquals(Greetings.NIGHT, getDayPeriodUseCase(0))
        assertEquals(Greetings.NIGHT, getDayPeriodUseCase(3))
        assertEquals(Greetings.NIGHT, getDayPeriodUseCase(4))
    }
}
