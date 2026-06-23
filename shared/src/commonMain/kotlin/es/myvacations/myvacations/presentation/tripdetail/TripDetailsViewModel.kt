package es.myvacations.myvacations.presentation.tripdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.usecase.tripusecase.DeleteTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.presentation.createtrip.TripUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val deleteTripUseCase: DeleteTripUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TripDetailUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun clearUi() {
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

    fun getTripById(id: String) {
        viewModelScope.launch {
            getTripByIdUseCase(id).collect { tripDomain ->
                _uiState.update {
                    it.copy(
                        tripUiState = TripUiState(
                            titleTrip = tripDomain?.title ?: "",
                            placeTrip = tripDomain?.place ?: Country.SPAIN,
                            startDate = tripDomain?.startDate,
                            endDate = tripDomain?.endDate,
                            daysTraveling = tripDomain?.daysTraveling ?: 1,
                            travelers = tripDomain?.travelers ?: 1,
                            mainCost = tripDomain?.mainCost ?: 0.0,
                            mainBudget = tripDomain?.mainBudget ?: 0.0,
                            cover = tripDomain?.cover ?: TripCover.BARCELONA,
                            optionalExpenses = tripDomain?.optionalExpenses ?: emptyList()
                        )
                    )
                }
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            deleteTripUseCase(id)
            clearUi()
        }
    }
}