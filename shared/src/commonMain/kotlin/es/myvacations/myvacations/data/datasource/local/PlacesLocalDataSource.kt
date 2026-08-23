package es.myvacations.myvacations.data.datasource.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import es.myvacations.myvacations.data.database.ChatMessageData
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import es.myvacations.myvacations.domain.mapper.json
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class PlacesLocalDataSource(
    private val database: MyVacationsDatabase
) {
    private val queries = database.vacationsEntityQueries

    fun getMessages(): Flow<List<ChatMessageData>> {
        return queries.selectAllMessages().asFlow().mapToList(Dispatchers.IO)
    }

    fun addMessage(message: ChatMessageUiState,userLocation: LocationUiState) {
        queries.insertMessage(
            userText = message.user.text,
            userElementsFound = json.encodeToString(message.user.elementsFound),
            userRetryOn = message.user.retryOn,
            mainLocationLongitude = userLocation.longitude,
            mainLocationLatitude = userLocation.latitude,
            botText = message.bot?.text,
            botElementsFound = message.bot?.let {
                json.encodeToString(it.elementsFound)
            },
            botRetryOn = message.bot?.retryOn,
            whatAsk = message.feedback.whatAsk,
            label = message.feedback.label,
            subcategory = message.feedback.subcategory,
            labelConfidence = message.feedback.labelConfidence.toDouble(),
            subcategoryConfidence = message.feedback.subcategoryConfidence.toDouble(),
            elementsSizeFound = message.feedback.elementsSizeFound.toLong(),
            time = message.time.toString(),
            feedbackDone = message.feedback.feedbackDone
        )
    }

    fun updateFeedback(id: Long) {
        queries.updateFeedback(true, id)
    }

    fun deleteMessage(id: Long) {
        queries.deleteMessage(id)
    }

    fun deleteAllMessages() {
        queries.deleteAllMessages()
    }

}