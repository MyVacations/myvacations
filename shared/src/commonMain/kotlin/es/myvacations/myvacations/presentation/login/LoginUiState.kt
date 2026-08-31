package es.myvacations.myvacations.presentation.login

data class LoginUiState(
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)

