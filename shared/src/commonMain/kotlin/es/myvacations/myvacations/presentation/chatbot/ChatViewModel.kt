package es.myvacations.myvacations.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.domain.mapper.toUiMapper
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.PlacesEventResult
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.domain.usecase.chatbot.AdsUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.ClassifyIntentUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.MapAndLocationUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.latestmodelrelease.EnsureModelInstalledUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.overpass.PlacesUseCase
import es.myvacations.myvacations.presentation.utils.distanceInMeters
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import myvacations.shared.generated.resources.Res
import myvacations.shared.generated.resources.boterror1
import myvacations.shared.generated.resources.boterror2
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock

class ChatViewModel(
    private val ensureModelInstalledUseCase: EnsureModelInstalledUseCase,
    private val classifyIntentUseCase: ClassifyIntentUseCase,
    private val locationUseCase: MapAndLocationUseCase,
    private val placesUseCase: PlacesUseCase,
    private val adsUseCase: AdsUseCase,
    private val repository: SettingsRepository,
    private val analytics: AnalyticsReporter
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ChatUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _stateLocation = MutableStateFlow<LocationEventResult>(LocationEventResult.Idle)
    val stateLocation = _stateLocation.asStateFlow()
    val state = ensureModelInstalledUseCase.modelState
    private var locationObserverJob: Job? = null

    init {
        analytics.logEvent(
            AnalyticsEvent.SCREEN_VIEW,
            mapOf("screen" to "chatbot")
        )

        viewModelScope.launch {
            val hasPermission = checkInit()
            if (hasPermission) {
                getMainLocation()
                startObservingMessages()
            }
            ensureModelInstalledUseCase.refreshState()
            _uiState.update { it.copy(isFullScreenLoading = false) }
        }
    }

    fun getMainLocation() {
        viewModelScope.launch {
            when (val locationEvent = locationUseCase.getCurrentLocation()) {
                is LocationEventResult.Success -> {
                    _uiState.update {
                        it.copy(updatedLocation = locationEvent.locationDomain.toUiMapper())
                    }
                }

                else -> {
                    _uiState.update {
                        it.copy(updatedLocation = null)
                    }
                }
            }
        }
    }

    fun getMapLocation(
        message: ChatMessageUiState
    ) {
        locationObserverJob?.cancel()

        locationObserverJob = viewModelScope.launch {
            if (!checkInit()) return@launch

            locationUseCase().collect { event ->
                if (event is LocationEventResult.Success) {

                    val hasNearbyPlace = message.bot.elementsFound.any { place ->
                        distanceInMeters(
                            userLatitude = event.locationDomain.latitude,
                            userLongitude = event.locationDomain.longitude,
                            latitude = place.latitude,
                            longitude = place.longitude
                        ) <= 500
                    }

                    _uiState.update {
                        it.copy(
                            outOfLimits = !hasNearbyPlace,
                            updatedLocation = if (hasNearbyPlace) {
                                event.locationDomain.toUiMapper()
                            } else {
                                it.updatedLocation
                            }
                        )
                    }
                }
            }
        }
    }

    fun checkInit(): Boolean {
        val hasPermission =
            locationUseCase.hasLocationPermissions()

        val hasPreciseLocation =
            locationUseCase.hasApproximateLocationPermission()

        _stateLocation.value =
            if (hasPermission) {
                LocationEventResult.PermissionOk
            } else {
                LocationEventResult.PermissionDenied
            }

        _uiState.update {
            it.copy(
                isFineLocationOn = hasPreciseLocation
            )
        }

        return hasPermission && hasPreciseLocation
    }

    fun afterPermissionOk() {
        viewModelScope.launch {
            val hasPermission = checkInit()
            if (hasPermission) {
                getMainLocation()
                startObservingMessages()
            }

            val settingsDomain = repository
                .getSettings()
                .first()

            _uiState.update {
                it.copy(
                    tutorial = settingsDomain?.iaTutorial ?: false
                )
            }
        }
    }

    private fun startObservingMessages() {
        viewModelScope.launch {
            placesUseCase.getMessages().collect { messages ->
                _uiState.update {
                    it.copy(
                        messages = messages
                    )
                }
            }
        }
    }

    fun installUpdateModel() {
        viewModelScope.launch {
            ensureModelInstalledUseCase()
        }
    }

    fun cancelInstall() {
        viewModelScope.launch {
            ensureModelInstalledUseCase.cancelInstall()
        }
    }

    fun enableTutorial() {
        viewModelScope.launch {
            repository.updateIATutorialSettings(true)
            _uiState.update {
                it.copy(tutorial = true)
            }
        }
    }

    fun disableTutorial() {
        viewModelScope.launch {
            repository.updateIATutorialSettings(false)
            _uiState.update {
                it.copy(tutorial = false)
            }
        }
    }

    fun onTextChatChange(value: String) {
        _uiState.update {
            it.copy(chatText = value)
        }
    }

    fun openNavigationToPlace(lat: Double, lon: Double, name: String?) {
        placesUseCase.openNavigationToPlace(lat, lon, name)
    }

    fun sendASearch(
        retryId: Long? = null,
        fromUser: String
    ) {
        val timeNow = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            val location = uiState.value.updatedLocation ?: return@launch errorPlacesEvent(
                retryId,
                placesUseCase, fromUser,
                timeNow,
                PlacesEventResult.Error(Exception("No location found"))
            )

            if (retryId != null && placesUseCase.getMessageId(retryId) == null) {
                val newUserMessage = ChatMessageUiState(
                    id = retryId,
                    user = ChatElements(
                        text = fromUser
                    ),
                    bot = ChatElements(text = ""),
                    time = timeNow
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + newUserMessage
                    )
                }
            }

            val prediction = classifyIntentUseCase(fromUser).prediction
            when (
                val event = placesUseCase(
                    location.latitude,
                    location.longitude,
                    location.radiusMeters,
                    prediction = prediction
                )
            ) {
                is PlacesEventResult.Success -> {
                    val elementsFound = event.placesDomain.map {
                        it.toUiMapper().copy(
                            distance = distanceInMeters(
                                location.latitude,
                                location.longitude,
                                it.latitude,
                                it.longitude
                            )
                        )
                    }
                        .sortedBy {
                            it.distance
                        }
                        .take(50)
                    val message = if (retryId == null) ChatMessageUiState(
                        user = ChatElements(
                            text = fromUser
                        ),
                        bot = ChatElements(
                            text = "",
                            elementsFound = elementsFound
                        ),
                        feedback = FeedbackState(
                            whatAsk = fromUser,
                            label = prediction.label.value,
                            subcategory = prediction.subcategory.value,
                            labelConfidence = prediction.label.confidence,
                            subcategoryConfidence = prediction.subcategory.confidence,
                            elementsSizeFound = elementsFound.size,
                            feedbackDone = false
                        ),
                        time = timeNow
                    )
                    else ChatMessageUiState(
                        id = retryId,
                        user = ChatElements(
                            text = fromUser
                        ),
                        bot = ChatElements(
                            text = "",
                            elementsFound = elementsFound
                        ),
                        feedback = FeedbackState(
                            whatAsk = fromUser,
                            label = prediction.label.value,
                            subcategory = prediction.subcategory.value,
                            labelConfidence = prediction.label.confidence,
                            subcategoryConfidence = prediction.subcategory.confidence,
                            elementsSizeFound = elementsFound.size,
                            feedbackDone = false
                        ),
                        time = timeNow
                    )

                    if (retryId != null && placesUseCase.getMessageId(retryId) != null) placesUseCase.updateAnErrorMessageForaSuccess(
                        message,
                        location
                    ) else placesUseCase.addMessages(
                        message,
                        location
                    )

                    _uiState.update {
                        val updatedList = it.messages.map { messageUiState ->
                            if (messageUiState.id == retryId) message else messageUiState
                        }

                        it.copy(
                            messages = updatedList
                        )
                    }
                    adsUseCase.showInterstitial()
                }

                is PlacesEventResult.Error -> {
                    errorPlacesEvent(retryId, placesUseCase, fromUser, timeNow, event)
                }
            }
        }
    }

    private suspend fun errorPlacesEvent(
        retryId: Long?,
        placesUseCase: PlacesUseCase,
        fromUser: String,
        timeNow: LocalDateTime,
        event: PlacesEventResult.Error,
    ) {
        val text = if (event.exception.message == "No places find") {
            getString(Res.string.boterror1)
        } else {
            getString(Res.string.boterror2)
        }

        val errorMessage = if (retryId == null) ChatMessageUiState(
            user = ChatElements(
                text = fromUser
            ),
            bot = ChatElements(
                text = text,
                elementsFound = emptyList()
            ),
            time = timeNow
        )
        else ChatMessageUiState(
            id = retryId,
            user = ChatElements(
                text = fromUser
            ),
            bot = ChatElements(
                text = text,
                elementsFound = emptyList()
            ),
            time = timeNow
        )

        if (placesUseCase.getMessageId(errorMessage.id) == null) {
            placesUseCase.addErrorMessage(
                errorMessage
            )
            _uiState.update {
                val updatedList = it.messages.map { messageUiState ->
                    if (messageUiState.id == errorMessage.id) errorMessage else messageUiState
                }

                it.copy(
                    messages = updatedList
                )
            }
        }
    }

    fun getNearestPlaces(
        places: List<ElementsFoundUiState>
    ): List<ElementsFoundUiState> {
        val location = uiState.value.updatedLocation ?: return emptyList()
        return places.map { place ->
            place.copy(
                distance = distanceInMeters(
                    userLatitude = location.latitude,
                    userLongitude = location.longitude,
                    latitude = place.latitude,
                    longitude = place.longitude
                )
            )
        }
            .sortedBy { it.distance }
            .take(3)
    }

    fun deleteAElement(id: Long) {
        viewModelScope.launch {
            placesUseCase.deleteMessage(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            placesUseCase.deleteMessagesPlaces()
        }
    }

    fun positiveFeedback(state: ChatMessageUiState) {
        viewModelScope.launch {
            placesUseCase.updateFeedback(state.id)
            analytics.logEvent(
                AnalyticsEvent.AI_PREDICTION,
                params = mapOf(
                    "feedback" to "positive",
                    "label" to state.feedback.label,
                    "subcategory" to state.feedback.subcategory,
                    "labelConfidence" to state.feedback.labelConfidence,
                    "subcategoryConfidence" to state.feedback.subcategoryConfidence
                )
            )
        }
    }

    fun negativeFeedback(state: ChatMessageUiState) {
        viewModelScope.launch {
            placesUseCase.updateFeedback(state.id)
            analytics.logEvent(
                AnalyticsEvent.AI_PREDICTION,
                params = mapOf(
                    "feedback" to "negative",
                    "label" to state.feedback.label,
                    "subcategory" to state.feedback.subcategory,
                    "labelConfidence" to state.feedback.labelConfidence,
                    "subcategoryConfidence" to state.feedback.subcategoryConfidence
                )
            )
        }
    }
}