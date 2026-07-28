package es.myvacations.myvacations.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.presentation.trips.TripsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: SettingsRepository) : ViewModel() {

    fun onOnboardingFinished() {
        viewModelScope.launch {
            repository.updateWelcomeShow(false)
        }
    }
}