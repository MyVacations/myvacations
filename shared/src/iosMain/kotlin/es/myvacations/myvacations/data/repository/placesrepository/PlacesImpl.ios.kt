package es.myvacations.myvacations.data.repository.placesrepository

import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.domain.repository.PlacesEventResult
import es.myvacations.myvacations.domain.repository.PlacesRepository
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.flow.Flow

actual class PlacesImpl actual constructor() :
    PlacesRepository {
    actual override fun getMessages(): Flow<List<ChatMessageUiState>> {
        TODO("Not yet implemented")
    }

    actual override fun getMessageId(id: Long): ChatMessageUiState? {
        TODO("Not yet implemented")
    }

    actual override suspend fun addMessage(
        message: ChatMessageUiState,
        userLocation: LocationUiState
    ) {
        TODO("Not yet implemented")
    }

    actual override suspend fun addMessageError(message: ChatMessageUiState) {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateErrorToSuccessMessage(
        message: ChatMessageUiState,
        userLocation: LocationUiState
    ) {
        TODO("Not yet implemented")
    }

    actual override suspend fun updateFeedback(id: Long) {
        TODO("Not yet implemented")
    }

    actual override suspend fun deleteMessage(id: Long) {
        TODO("Not yet implemented")
    }

    actual override suspend fun deleteAllMessages() {
        TODO("Not yet implemented")
    }

    actual override suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int,
        prediction: Prediction
    ): PlacesEventResult {
        TODO("Not yet implemented")
    }

    actual override fun openNavigationToPlace(
        latitude: Double,
        longitude: Double,
        name: String?
    ) {
        TODO("Not yet implemented")
    }
}