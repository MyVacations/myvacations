package es.myvacations.myvacations.presentation.chatbot

data class ChatUiState(
    val tutorial: Boolean = false,
    val clickOnUpdate: Boolean = false,
    val messages: List<ChatMessageUiState> = emptyList(),
    val isLoading: Boolean = false
)