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

    fun getMessageId(id: Long): ChatMessageData? {
        return queries.selectMessagesFromId(id).executeAsOneOrNull()
    }

    fun addMessage(message: ChatMessageUiState, userLocation: LocationUiState) {
        queries.insertMessage(
            userText = message.user.text,
            mainLocationLongitude = userLocation.longitude,
            mainLocationLatitude = userLocation.latitude,
            botText = message.bot.text,
            botElementsFound = message.bot.let {
                json.encodeToString(it.elementsFound)
            },
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

    fun addErrorMessage(message: ChatMessageUiState) {
        queries.insertErrorMessage(
            userText = message.user.text,
            botText = message.bot.text,
            botElementsFound = message.bot.let {
                json.encodeToString(it.elementsFound)
            },
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

    fun updateErrorToSuccessMessage(message: ChatMessageUiState, userLocation: LocationUiState) {
        queries.updateErrorToSuccessMessage(
            id = message.id,
            mainLocationLongitude = userLocation.longitude,
            mainLocationLatitude = userLocation.latitude,
            botText = message.bot.text,
            botElementsFound = message.bot.let {
                json.encodeToString(it.elementsFound)
            })
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