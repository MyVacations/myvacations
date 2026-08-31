package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.PlaceDomain
import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.flow.Flow

sealed interface PlacesEventResult {
    data class Success(val placesDomain: List<PlaceDomain>) : PlacesEventResult
    data class Error(val exception: Exception) : PlacesEventResult
}

interface PlacesRepository {
    fun getMessages(): Flow<List<ChatMessageUiState>>
    fun getMessageId(id: Long): ChatMessageUiState?
    suspend fun addMessage(message: ChatMessageUiState,userLocation: LocationUiState)
    suspend fun addMessageError(message: ChatMessageUiState)
    suspend fun updateErrorToSuccessMessage(message: ChatMessageUiState,userLocation: LocationUiState)
    suspend fun updateFeedback(id: Long)
    suspend fun deleteMessage(id: Long)
    suspend fun deleteAllMessages()

    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int,
        prediction: Prediction
    ): PlacesEventResult

    fun openNavigationToPlace(
        latitude: Double,
        longitude: Double,
        name: String?
    )
}