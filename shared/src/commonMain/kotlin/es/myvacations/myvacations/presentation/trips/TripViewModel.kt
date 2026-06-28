package es.myvacations.myvacations.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import es.myvacations.myvacations.presentation.mapper.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripViewModel(
    private val getTripsUseCase: GetTripsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TripsUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun loadTrips() {
        viewModelScope.launch {
            getTripsUseCase().collect { tripsDomain ->
                _uiState.value = _uiState.value.copy(trips = tripsDomain.map { tripDomain ->
                    tripDomain.toUiState()
                })
            }
        }
    }
}