package es.myvacations.myvacations.domain.usecase

import es.myvacations.myvacations.presentation.dashboard.Greetings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class GetDayPeriodUseCase {
    operator fun invoke(
        now: Int = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .hour
    ): Greetings {
        return when (now) {
            in 5..11 -> Greetings.MORNING
            in 11..17 -> Greetings.AFTERNOON
            in 17..21 -> Greetings.EVENING
            else -> Greetings.NIGHT
        }
    }
}