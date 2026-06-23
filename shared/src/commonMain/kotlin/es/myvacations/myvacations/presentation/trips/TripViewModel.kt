package es.myvacations.myvacations.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.usecase.tripusecase.DeleteTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripViewModel(
    private val getTripsUseCase: GetTripsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TripsUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun clearUi()
    {
        /*
        _uiState.update {
            it.copy(
                titleTrip = "",
                placeTrip = Country.SPAIN,
                startDate = null,
                endDate = null,
                daysTraveling = 1,
                travelers = 1,
                mainCost = 0.0,
                mainBudget = 0.0,
                cover = TripCover.BARCELONA,
                optionalExpensesExpanded = false,
                optionalExpenses = emptyList()
            )
        }

         */
    }

    fun getTrips() {
        viewModelScope.launch {
            getTripsUseCase().collect { tripsDomain ->

            }
        }
    }

}