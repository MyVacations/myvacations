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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

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
                        startDate = tripDomain?.startDate,
                        endDate = tripDomain?.endDate,
                        daysTraveling = tripDomain?.daysTraveling ?: 1,
                        travelers = tripDomain?.travelers ?: 1,
                        mainCost = tripDomain?.mainCost ?: 0.0,
                        mainBudget = tripDomain?.mainBudget ?: 0.0,
                        cover = tripDomain?.cover ?: TripCover.BARCELONA,
                        optionalExpensesExpanded = false,
                        optionalExpenses = tripDomain?.optionalExpenses?.map { expenseDomain -> expenseDomain.toUiState() }
                            ?: emptyList(),
                        isLoading = false
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
        _uiState.value = _uiState.value.copy(titleTrip = title)
    }

    fun updateCountry(country: Country) {
        _uiState.value = _uiState.value.copy(placeTrip = country)
    }

    fun updateStartDate(startDate: LocalDate) {
        _uiState.value = _uiState.value.copy(startDate = startDate)
    }

    fun updateEndDate(endDate: LocalDate) {
        _uiState.value = _uiState.value.copy(endDate = endDate)
    }

    fun updateDaysTraveling(days: Int) {
        _uiState.value = _uiState.value.copy(daysTraveling = days)
    }

    fun updateTravelers(travelers: Int) {
        _uiState.value = _uiState.value.copy(travelers = travelers)
    }

    fun updateMainCost(cost: String) {
        _uiState.value = _uiState.value.copy(mainCost = cost.toSafeDouble())
    }

    fun updateMainBudget(budget: String) {
        _uiState.value = _uiState.value.copy(mainBudget = budget.toSafeDouble())
    }

    fun toggleOptionalExpenses() {
        _uiState.value = _uiState.value.copy(
            optionalExpensesExpanded = !_uiState.value.optionalExpensesExpanded
        )
    }

    fun updateCover(cover: TripCover) {
        _uiState.value = _uiState.value.copy(cover = cover)
    }

    fun updateExpenseName(
        id: String,
        name: String
    ) {
        _uiState.value = _uiState.value.copy(
            optionalExpenses = _uiState.value.optionalExpenses.map {
                if (it.id == id) {
                    it.copy(name = name)
                } else {
                    it
                }
            }
        )
    }

    fun updateExpenseAmount(
        id: String,
        amount: String
    ) {
        _uiState.value = _uiState.value.copy(
            optionalExpenses = _uiState.value.optionalExpenses.map {
                if (it.id == id) {
                    it.copy(amount = amount.toSafeDouble())
                } else {
                    it
                }
            }
        )
    }

    fun updateExpenseIcon(
        id: String,
        icon: TravelIcon
    ) {
        _uiState.value = _uiState.value.copy(
            optionalExpenses = _uiState.value.optionalExpenses.map { expense ->
                if (expense.id == id) {
                    expense.copy(icon = icon)
                } else {
                    expense
                }
            }
        )
    }

    fun addExpense() {
        _uiState.value = _uiState.value.copy(
            optionalExpenses = _uiState.value.optionalExpenses + TripExpenseUiState(
                id = Random.nextInt().toString(),
                name = "",
                icon = TravelIcon.RESTAURANT,
                amount = 100.0,
                currency = _uiState.value.currency
            )
        )
    }

    fun deleteExpense(id: String) {
        _uiState.value = _uiState.value.copy(
            optionalExpenses = _uiState.value.optionalExpenses.filter { it.id != id }
        )
    }

    fun clearUi() {
        _uiState.update {
            it.copy(
                id = "",
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