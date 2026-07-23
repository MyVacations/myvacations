package es.myvacations.myvacations.presentation.createedittrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.extensions.toSafeDouble
import es.myvacations.myvacations.domain.model.Country
import es.myvacations.myvacations.domain.model.TripCover
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetTripByIdUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.SaveTripUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.UpdateTripUseCase
import es.myvacations.myvacations.presentation.mapper.toDomainModel
import es.myvacations.myvacations.presentation.mapper.toUiState
import es.myvacations.myvacations.presentation.utils.Currency
import es.myvacations.myvacations.presentation.utils.TravelIcon
import es.myvacations.myvacations.presentation.utils.TripExpenseUiState
import es.myvacations.myvacations.presentation.utils.calendar.CalendarUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

private const val MAX_NUMBER = 100_000.0
private const val MAX_EXPENSE = 100_0.0


class CreateEditTripsViewModel(
    private val saveTrip: SaveTripUseCase,
    private val getTripIdUseCase: GetTripByIdUseCase,
    private val editTrip: UpdateTripUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TripUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(currency = settings?.preferredCurrency ?: Currency.EURO)
                }
            }
        }
    }

    fun setLoading(loading: Boolean) = _uiState.update { it.copy(isLoading = loading) }

    fun getTripById(id: String) {
        viewModelScope.launch {
            val startTime = Clock.System.now().toEpochMilliseconds()
            getTripIdUseCase(id).collect { tripDomain ->
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                val remaining = 1000 - elapsed
                if (remaining > 0) {
                    delay(remaining.milliseconds)
                }

                _uiState.update {
                    it.copy(
                        id = tripDomain?.id ?: "",
                        titleTrip = tripDomain?.title ?: "",
                        placeTrip = tripDomain?.place ?: Country.SPAIN,
                        startDate = tripDomain?.startDate ?: CalendarUiState().selectedDate,
                        endDate = tripDomain?.endDate ?: CalendarUiState().selectedDate,
                        mainCost = tripDomain?.mainCost ?: 0.0,
                        mainBudget = tripDomain?.mainBudget ?: 0.0,
                        cover = tripDomain?.cover ?: TripCover.BARCELONA,
                        optionalExpensesExpanded = true,
                        optionalExpenses = tripDomain?.optionalExpenses?.map { expenseDomain -> expenseDomain.toUiState() }
                            ?: emptyList(),
                        isLoading = false,
                        favourite = tripDomain?.favourite ?: false
                    )
                }
            }
        }
    }

    fun updateEditMode(tripId: String) {
        _uiState.update {
            it.copy(editMode = tripId.isNotEmpty())
        }
    }

    fun updateTripTitle(title: String) {
        _uiState.update {
            it.copy(titleTrip = title)
        }
    }

    fun updateCountry(country: Country) {
        _uiState.update {
            it.copy(placeTrip = country)
        }
    }

    fun updateStartDate(startDate: LocalDate) {
        _uiState.update {
            it.copy(
                startDate = startDate,
                endDate = when {
                    (startDate > _uiState.value.endDate) || (startDate < _uiState.value.endDate) -> startDate
                    else -> _uiState.value.endDate
                }
            )
        }
    }

    fun updateEndDate(endDate: LocalDate) {
        _uiState.update {
            it.copy(
                endDate = endDate,
                errorStartDate = _uiState.value.startDate > endDate
            )
        }
    }

    fun updateTravelers(travelers: Int) {
        _uiState.update {
            it.copy(
                travelers = travelers
            )
        }
    }

    fun updateMainCost(cost: String) {
        val numberToProceed = cost.toSafeDouble()
        _uiState.update {
            it.copy(
                errorInScreen = (MAX_NUMBER <= numberToProceed),
                mainCost = numberToProceed
            )
        }
    }

    fun updateMainBudget(budget: String) {
        val numberToProceed = budget.toSafeDouble()
        _uiState.update {
            it.copy(
                errorInScreen = (MAX_NUMBER <= numberToProceed),
                mainBudget = numberToProceed
            )
        }
    }

    fun toggleOptionalExpenses() {
        _uiState.update {
            it.copy(optionalExpensesExpanded = !_uiState.value.optionalExpensesExpanded)
        }
    }

    fun updateCover(cover: TripCover) {
        _uiState.update {
            it.copy(cover = cover)
        }
    }

    fun createExpense(id: String, name: String, amount: String, icon: TravelIcon) {
        _uiState.update {
            it.copy(
                optionalExpenses = _uiState.value.optionalExpenses + TripExpenseUiState(
                    id = id,
                    name = name,
                    amount = amount.toSafeDouble(),
                    icon = icon
                )
            )
        }
    }

    fun updateExpense(id: String, name: String, amount: String, icon: TravelIcon) {
        _uiState.update { uiState ->
            uiState.copy(
                optionalExpenses = _uiState.value.optionalExpenses.map {
                    if (it.id == id) {
                        it.copy(name = name, amount = amount.toSafeDouble(), icon = icon)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun updateErrorAmount(value: Boolean) {
        _uiState.update {
            it.copy(
                optionalExpensesErrorAmount = value
            )
        }
    }

    fun deleteExpense(id: String) {
        _uiState.update {
            it.copy(optionalExpenses = _uiState.value.optionalExpenses.filter { it.id != id })

        }
    }

    fun updateFavourite(favourite: Boolean) {
        _uiState.update {
            it.copy(favourite = favourite)
        }
    }

    fun clearUi() {
        _uiState.update {
            it.copy(
                id = "",
                titleTrip = "",
                placeTrip = Country.SPAIN,
                startDate = CalendarUiState().selectedDate,
                errorStartDate = false,
                endDate = CalendarUiState().selectedDate,
                travelers = 1,
                mainCost = 0.0,
                mainBudget = 0.0,
                cover = TripCover.BARCELONA,
                optionalExpensesExpanded = true,
                editMode = false,
                optionalExpenses = emptyList(),
                favourite = false
            )
        }
    }

    fun saveTrip() {
        viewModelScope.launch {
            if (_uiState.value.editMode) editTrip.invoke(uiState.value.toDomainModel()) else saveTrip.invoke(
                uiState.value.toDomainModel().copy(id = Uuid.random().toHexString())
            )
            clearUi()
        }
    }
}