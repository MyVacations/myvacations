package es.myvacations.myvacations.presentation.chatbot

data class ChatUiState(
    val tutorial: Boolean = false,
    val chatText: String = "",
    val clickOnUpdate: Boolean = false,
    val updatedLocation: LocationUiState? = null,
    val messages: List<ChatMessageUiState> = emptyList(),
    val isFineLocationOn: Boolean = false,
    val outOfLimits: Boolean = false,
    val isLoading: Boolean = true
)

data class ChatTutorialPage(
    val title: String,
    val description: String,
    val type: ChatTutorialPageType
)

enum class ChatTutorialPageType {
    INTRO,
    EXAMPLES,
    RESULTS,
    MAP,
    DETAILS,
    NAVIGATION
}