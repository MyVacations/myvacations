package es.myvacations.myvacations.domain.mapper

import es.myvacations.myvacations.data.database.ChatMessageData
import es.myvacations.myvacations.domain.model.PlaceDomain
import es.myvacations.myvacations.domain.model.locations.LocationDomain
import es.myvacations.myvacations.presentation.chatbot.ChatElements
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.ElementsFoundUiState
import es.myvacations.myvacations.presentation.chatbot.FeedbackState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}

fun ChatMessageData.toUiMapper(): ChatMessageUiState {
    return ChatMessageUiState(
        id = id,
        user = ChatElements(
            text = userText
        ),
        bot = ChatElements(
            text = botText ?: "",
            elementsFound = botElementsFound?.let { jsonString ->
                json.decodeFromString(jsonString)
            } ?: emptyList()
        ),
        time = LocalDateTime.parse(time),
        locationFor500m = LocationUiState(
            mainLocationLatitude ?: 0.0,
            mainLocationLongitude ?: 0.0,
            5000
        ),
        feedback = FeedbackState(
            whatAsk = whatAsk,
            label = label,
            subcategory = subcategory,
            labelConfidence = labelConfidence.toFloat(),
            subcategoryConfidence = subcategoryConfidence.toFloat(),
            elementsSizeFound = elementsSizeFound.toInt(),
            feedbackDone = feedbackDone
        )
    )
}

fun LocationDomain.toUiMapper() = LocationUiState(
    latitude = this.latitude,
    longitude = this.longitude,
    radiusMeters = this.radiusMeters
)

fun PlaceDomain.toUiMapper() = ElementsFoundUiState(
    id = this.id,
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    distance = this.distance,
    phone = this.phone,
    website = this.website,
    address = this.address,
    access = this.cuisine,
    type = this.type
)