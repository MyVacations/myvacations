package es.myvacations.myvacations.presentation.chatbot

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

data class ChatMessageUiState(
    val id: Long = 0,
    val user: ChatElements,
    val bot: ChatElements? = null,
    val time: LocalDateTime = LocalDateTime(2023, 1, 1, 0, 0),
    val locationFor500m: LocationUiState = LocationUiState(0.0, 0.0, 0),
    val feedback: FeedbackState = FeedbackState()
)

@Serializable
data class LocationUiState(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int
)

@Serializable
data class ElementsFoundUiState(
    val id: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val distance: Double?,
    val phone: String?,
    val website: String?,
    val address: String?,
    val access: String?,
    val type: String?
)

@Serializable
data class ChatElements(
    val text: String,
    val elementsFound: List<ElementsFoundUiState> = emptyList(),
    val retryOn: Boolean = false
)

@Serializable
data class FeedbackState(
    val whatAsk: String = "",
    val label: String = "",
    val subcategory: String = "",
    val labelConfidence: Float = 0f,
    val subcategoryConfidence: Float = 0f,
    val elementsSizeFound: Int = 0,
    val feedbackDone: Boolean = false
)

@Serializable
data class WidgetPlace(
    val mainLocation: LocationUiState = LocationUiState(0.0, 0.0, 0),
    val elementsFound: List<ElementsFoundUiState> = emptyList(),
)
