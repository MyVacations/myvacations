package es.myvacations.myvacations.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.presentation.mapper.toUiState
import es.myvacations.myvacations.presentation.utils.Currency
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class StatisticsViewModel(private val getTripsUseCase: GetTripsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(
        StatisticsUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    fun loadTrips() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val startTime = Clock.System.now().toEpochMilliseconds()
            getTripsUseCase().collect { tripsDomain ->
                val tripsUi = tripsDomain.map { it.toUiState() }
                val elapsed =  Clock.System.now().toEpochMilliseconds() - startTime

                val remaining = 1000 - elapsed

                if (remaining > 0) {
                    delay(remaining.milliseconds)
                }
                _uiState.update {
                    it.copy(
                        trips = tripsUi,
                        currency = tripsUi.firstOrNull()?.currency ?: Currency.EURO,
                        isLoading = false
                    )
                }
            }
        }
    }
}