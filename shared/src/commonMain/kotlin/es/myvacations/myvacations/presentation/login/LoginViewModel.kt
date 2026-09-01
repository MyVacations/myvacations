package es.myvacations.myvacations.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.repository.FirebaseAuthRepository
import es.myvacations.myvacations.presentation.mapper.toUserMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    val firebaseAuthServices: FirebaseAuthRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    sealed interface UiEvent {
        data class ShowError(val message: String?) : UiEvent
        data object Success : UiEvent
        data object Loading : UiEvent
    }

    init {
        firebaseAuthServices.signInWithAnonymously()
    }

    fun googleClick() {
        viewModelScope.launch {
            _events.emit(UiEvent.Loading)
            firebaseAuthServices.signInWithGoogle().onSuccess {
                _events.emit(UiEvent.Success)
            }.onFailure { exception ->
                _events.emit(UiEvent.ShowError(exception.message?.toUserMessage()))
            }
        }
    }

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.Loading)
            firebaseAuthServices.signUpWithEmaiAndPassword(email, password).onSuccess {
                _events.emit(UiEvent.Success)
            }.onFailure { exception ->
                _events.emit(UiEvent.ShowError(exception.message?.toUserMessage()))
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.Loading)
            firebaseAuthServices.signInWithEmaiAndPassword(email, password).onSuccess {
                _events.emit(UiEvent.Success)
            }.onFailure { exception ->
                _events.emit(UiEvent.ShowError(exception.message?.toUserMessage()))
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _events.emit(UiEvent.Loading)
            firebaseAuthServices.resetPassword(email).onSuccess {
                _events.emit(UiEvent.ShowError("Enviado".toUserMessage()))
            }.onFailure { exception ->
                _events.emit(UiEvent.ShowError(exception.message?.toUserMessage()))
            }
        }
    }
}