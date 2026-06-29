package es.myvacations.myvacations.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.model.TravelersDomain
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.settingsusecase.UpdateSettingsUseCase
import es.myvacations.myvacations.presentation.mapper.toDomainSettingsState
import es.myvacations.myvacations.presentation.utils.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            getSettingsUseCase().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    userName = settings?.username ?: "",
                    currency = settings?.preferredCurrency ?: Currency.EURO,
                    isLoading = false
                )
            }
        }
    }

    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            _uiState.update { settings ->
                settings.copy(currency = currency)
            }
            updateSettingsUseCase.invoke(uiState.value.toDomainSettingsState())
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            _uiState.update { settings ->
                settings.copy(userName = name)
            }
            updateSettingsUseCase.invoke(uiState.value.toDomainSettingsState())
        }
    }
}