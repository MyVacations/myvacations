package es.myvacations.myvacations.data.repository.placesrepository

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.local.PlacesLocalDataSource
import es.myvacations.myvacations.domain.mapper.toUiMapper
import es.myvacations.myvacations.domain.model.PlaceDomain
import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.domain.repository.PlacesEventResult
import es.myvacations.myvacations.domain.repository.PlacesRepository
import es.myvacations.myvacations.presentation.chatbot.ChatMessageUiState
import es.myvacations.myvacations.presentation.chatbot.LocationUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class PlacesImpl actual constructor() :
    PlacesRepository, KoinComponent {
    private val functions: FirebaseFunctions by inject()
    private val localDataSource: PlacesLocalDataSource by inject()
    private fun parseGoogle(response: Map<String, Any?>): List<PlaceDomain> {
        @Suppress("UNCHECKED_CAST")
        val places = response["places"] as? Map<String, Any?>
            ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val placesList =
            places["places"] as? List<Map<String, Any?>>
                ?: return emptyList()

        return placesList.mapNotNull { place ->

            val id = place["id"] as? String

            @Suppress("UNCHECKED_CAST")
            val displayName =
                place["displayName"] as? Map<String, Any?>

            val name = displayName?.get("text") as? String

            @Suppress("UNCHECKED_CAST")
            val location =
                place["location"] as? Map<String, Any?>

            val placeLatitude =
                (location?.get("latitude") as? Number)?.toDouble()

            val placeLongitude =
                (location?.get("longitude") as? Number)?.toDouble()

            val address =
                place["formattedAddress"] as? String

            val phone =
                place["phone"] as? String

            val website =
                place["website"] as? String

            val cuisine =
                place["cuisine"] as? String

            val type =
                place["primaryType"] as? String

            if (
                id == null ||
                placeLatitude == null ||
                placeLongitude == null
            ) {
                return@mapNotNull null
            }

            PlaceDomain(
                id = id,
                name = name,
                latitude = placeLatitude,
                longitude = placeLongitude,
                phone = phone,
                website = website,
                address = address,
                cuisine = cuisine,
                type = type,
                distance = 0.0
            )
        }
    }

    private fun parseOSM(response: Map<String, Any?>): List<PlaceDomain> {
        @Suppress("UNCHECKED_CAST")
        val places = response["places"] as? Map<String, Any?>
            ?: return emptyList()

        @Suppress("UNCHECKED_CAST")
        val elements =
            places["elements"] as? List<Map<String, Any?>>
                ?: return emptyList()

        return elements.mapNotNull { element ->

            val id = (element["id"] as? Number)?.toLong()?.toString()

            val placeLatitude =
                (element["lat"] as? Number)?.toDouble()

            val placeLongitude =
                (element["lon"] as? Number)?.toDouble()

            @Suppress("UNCHECKED_CAST")
            val tags =
                element["tags"] as? Map<String, Any?>

            val name =
                tags?.get("name") as? String

            val phone =
                tags?.get("phone") as? String

            val website =
                tags?.get("website") as? String

            val address =
                tags?.get("addr:street") as? String

            val cuisine =
                tags?.get("cuisine") as? String

            val type =
                tags?.get("amenity") as? String

            if (
                id == null ||
                placeLatitude == null ||
                placeLongitude == null
            ) {
                return@mapNotNull null
            }

            PlaceDomain(
                id = id,
                name = name,
                latitude = placeLatitude,
                longitude = placeLongitude,
                phone = phone,
                website = website,
                address = address,
                cuisine = cuisine,
                distance = 0.0,
                type = type
            )
        }
    }

    private fun parsePlaces(
        response: Map<String, Any?>
    ): List<PlaceDomain> {

        val provider = response["provider"] as? String
            ?: return emptyList()

        return when (provider) {
            "google" -> parseGoogle(response)
            "osm" -> parseOSM(response)
            else -> emptyList()
        }
    }

    actual override fun getMessages(): Flow<List<ChatMessageUiState>> {
        return localDataSource.getMessages().map { messagesData ->
            messagesData.map { messageData ->
                messageData.toUiMapper()
            }
        }
    }

    actual override suspend fun addMessage(message: ChatMessageUiState,userLocation: LocationUiState) {
        localDataSource.addMessage(message,userLocation)
    }

    actual override suspend fun updateFeedback(id: Long) {
        localDataSource.updateFeedback(id)
    }

    actual override suspend fun deleteMessage(id: Long) {
        localDataSource.deleteMessage(id)
    }

    actual override suspend fun deleteAllMessages() {
        localDataSource.deleteAllMessages()
    }

    actual override suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radius: Int,
        prediction: Prediction
    ): PlacesEventResult {
        return try {
            val label = if (prediction.label.confidence >= 0.8) prediction.label.value else "NONE"
            val subcategory =
                if (prediction.subcategory.confidence >= 0.9) prediction.subcategory.value else "GENERAL"
            val data = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "label" to label,
                "subcategory" to subcategory,
                "restaurantType" to prediction.restaurantType.value
            )

            val result = functions
                .getHttpsCallable("testBackend")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val response = result.data as Map<String, Any?>

            PlacesEventResult.Success(parsePlaces(response))
        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.FAILED_PRECONDITION -> {
                    PlacesEventResult.Error(Exception("No places find"))
                }

                else -> {
                    PlacesEventResult.Error(e)
                }
            }
        }
    }

    actual override fun openNavigationToPlace(
        latitude: Double,
        longitude: Double,
        name: String?
    ) {
        val label = Uri.encode(name ?: "Destino")

        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude($label)".toUri()

        val intent = Intent(
            Intent.ACTION_VIEW,
            uri
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        AndroidContextHolder.context.startActivity(intent)
    }
}