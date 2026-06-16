package es.myvacations.myvacations.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.usecase.GetDayPeriodUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class DashboardViewModel(
    private val getDayPeriod: GetDayPeriodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                waitUntilNextPeriodChange()
                refreshGreetings()
            }
        }
    }

    private suspend fun waitUntilNextPeriodChange() {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val nextHour = when {
            now.hour <= 4 -> 5
            now.hour <= 11 -> 12
            now.hour <= 17 -> 18
            now.hour <= 21 -> 22
            else -> 5 // 05:00 del día siguiente
        }

        val nextChange = if (nextHour < 24) {
            now.date.atTime(nextHour, 0)
        } else {
            now.date.plus(DatePeriod(days = 1)).atTime(5, 0)
        }

        val millis = nextChange
            .toInstant(TimeZone.currentSystemDefault())
            .minus(now.toInstant(TimeZone.currentSystemDefault()))
            .inWholeMilliseconds

        delay(millis.milliseconds)
    }

    fun refreshGreetings() {
        _uiState.update {
            it.copy(
                greetings = getDayPeriod(),
                userName = "Jesus" // El nombre tambien por si se cambia de ajustes
            )
        }
    }
}