package es.myvacations.myvacations.presentation.utils.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalendarUiState()
    )
    val uiState = _uiState.asStateFlow()

    fun updateSelectedDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedDate = date
            )
        }
    }
}