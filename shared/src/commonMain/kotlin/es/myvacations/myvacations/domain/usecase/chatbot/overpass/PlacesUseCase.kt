package es.myvacations.myvacations.domain.usecase.chatbot.overpass

import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.domain.repository.PlacesRepository
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.flow.Flow

class PlacesUseCase(
    val placesRepository: PlacesRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, radius: Int, prediction: Prediction) =
        placesRepository.getNearbyPlaces(lat, lon, radius, prediction)

    fun openNavigationToPlace(lat: Double, lon: Double, name: String?) =
        placesRepository.openNavigationToPlace(lat, lon, name)

    fun getMessages(): Flow<List<ChatMessageUiState>> {
        return placesRepository.getMessages()
    }

    suspend fun addMessages(message: ChatMessageUiState,userLocation: LocationUiState) {
        placesRepository.addMessage(message,userLocation)
    }

    suspend fun updateFeedback(id: Long) {
        placesRepository.updateFeedback(id)
    }

    suspend fun deleteMessage(id: Long) {
        placesRepository.deleteMessage(id)
    }

    suspend fun deleteMessagesPlaces() {
        placesRepository.deleteAllMessages()
    }
}