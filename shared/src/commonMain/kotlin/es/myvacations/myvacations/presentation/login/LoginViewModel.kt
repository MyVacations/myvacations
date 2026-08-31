package es.myvacations.myvacations.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.domain.repository.FirebaseAuthRepository
import es.myvacations.myvacations.presentation.mapper.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    val firebaseAuthServices: FirebaseAuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        LoginUiState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        firebaseAuthServices.signInWithAnonymously()
    }

    fun googleClick() {
        viewModelScope.launch {
            firebaseAuthServices.signInWithGoogle().onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message?.toUserMessage()
                    )
                }
            }
        }
    }

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuthServices.signUpWithEmaiAndPassword(email, password).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message?.toUserMessage()
                    )
                }
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isSuccess = false,
                    error = null
                )
            }
            firebaseAuthServices.signInWithEmaiAndPassword(email, password).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                }
            }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = false,
                            error = exception.message?.toUserMessage()
                        )
                    }
                }
        }
    }

    fun resetPassword(email: String)
    {
        viewModelScope.launch {
            firebaseAuthServices.resetPassword(email).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Enviado".toUserMessage()
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = exception.message?.toUserMessage()
                    )
                }
            }
        }
    }
}