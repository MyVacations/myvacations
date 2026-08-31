package es.myvacations.myvacations.data.repository.placesrepository

import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.domain.repository.PlacesEventResult
import es.myvacations.myvacations.domain.repository.PlacesRepository
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.flow.Flow

expect class PlacesImpl(
) : PlacesRepository {
    override fun getMessages(): Flow<List<ChatMessageUiState>>
    override fun getMessageId(id: Long): ChatMessageUiState?
    override suspend fun addMessage(message: ChatMessageUiState, userLocation: LocationUiState)
    override suspend fun addMessageError(message: ChatMessageUiState)
    override suspend fun updateErrorToSuccessMessage(
        message: ChatMessageUiState,
        userLocation: LocationUiState
    )

    override suspend fun updateFeedback(id: Long)
    override suspend fun deleteMessage(id: Long)
    override suspend fun deleteAllMessages()

    override suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int,
        prediction: Prediction
    ): PlacesEventResult

    override fun openNavigationToPlace(
        latitude: Double,
        longitude: Double,
        name: String?
    )
}