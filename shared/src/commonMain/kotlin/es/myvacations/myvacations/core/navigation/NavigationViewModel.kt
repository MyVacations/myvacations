package es.myvacations.myvacations.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NavigationStateUi()
    )

    val uiState = _uiState.asStateFlow()

    init {
       viewModelScope.launch {
           repository.getSettings().collect { settings ->
               _uiState.value = _uiState.value.copy(
                   welcomeShow = settings?.welcomeShow ?: false
               )
           }
       }
    }
}