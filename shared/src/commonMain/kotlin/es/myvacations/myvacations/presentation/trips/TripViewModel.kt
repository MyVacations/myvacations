package es.myvacations.myvacations.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.presentation.mapper.toUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class TripViewModel(
    private val getTripsUseCase: GetTripsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TripsUiState()
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    fun loadTrips() {
        viewModelScope.launch {
            val startTime = Clock.System.now().toEpochMilliseconds()
            getTripsUseCase().collect { tripsDomain ->
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                val remaining = 1000 - elapsed
                if (remaining > 0) {
                    delay(remaining.milliseconds)
                }
                _uiState.update {
                    it.copy(
                        trips = tripsDomain.map { tripDomain ->
                            tripDomain.toUiState()
                        }, isLoading = false
                    )
                }
            }
        }
    }
}