package es.myvacations.myvacations.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.domain.mapper.toUiMapper
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.PlacesEventResult
import es.myvacations.myvacations.domain.repository.SettingsRepository
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
        }
    }

    private suspend fun getMainLocation() {
        when (val locationEvent = locationUseCase.getCurrentLocation()) {
            is LocationEventResult.Success -> {
                _uiState.update {
                    it.copy(updatedLocation = locationEvent.locationDomain.toUiMapper())
                }
            }

            else -> {
                _uiState.update {
                    it.copy(updatedLocation = LocationUiState(0.0, 0.0, 0))
                }
            }
        }
    }

    fun getMapLocation(
        message: ChatMessageUiState
    ) {
        viewModelScope.launch {
            if (locationObserverJob?.isActive == true || !checkInit()) return@launch

            locationObserverJob = viewModelScope.launch {
                locationUseCase().collect { event ->
                    if (event is LocationEventResult.Success) {
                        val hasNearbyPlace = message.bot?.elementsFound?.any { place ->
                            distanceInMeters(
                                userLatitude = event.locationDomain.latitude,
                                userLongitude = event.locationDomain.longitude,
                                latitude = place.latitude,
                                longitude = place.longitude
                            ) <= 500
                        } == true

                        if (hasNearbyPlace) _uiState.update {
                            it.copy(
                                updatedLocation = event.locationDomain.toUiMapper(),
                                outOfLimits = false
                            )
                        } else {
                            _uiState.update {
                                it.copy(
                                    updatedLocation = message.mainLocation,
                                    outOfLimits = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onResume() {
        viewModelScope.launch {
            checkInit()

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

            ensureModelInstalledUseCase.refreshState()
        }
    }

    private fun startObservingMessages() {
        viewModelScope.launch {
            placesUseCase.getMessages().collect { messages ->
                _uiState.update {
                    it.copy(
                        messages = messages,
                        isLoading = false
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
        fromUser: String,
        fromRetry: Boolean
    ) {
        val timeNow = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        viewModelScope.launch {
            if (fromRetry) {
                _uiState.update {
                    it.copy(messages = it.messages.dropLast(1))
                }
            }

            val newMessage = ChatMessageUiState(
                user = ChatElements(
                    text = fromUser
                ),
                time = timeNow,
                isItemLoading = true
            )

            _uiState.update {
                it.copy(
                    messages = it.messages + newMessage
                )
            }

            var text: String
            var retry = false

            val prediction = classifyIntentUseCase(fromUser).prediction
            getMainLocation()
            when (
                val event = placesUseCase(
                    uiState.value.updatedLocation.latitude,
                    uiState.value.updatedLocation.longitude,
                    uiState.value.updatedLocation.radiusMeters,
                    prediction = prediction
                )
            ) {

                is PlacesEventResult.Success -> {
                    text = ""
                    val elementsFound = event.placesDomain
                        .map {
                            it.toUiMapper().copy(
                                distance = distanceInMeters(
                                    uiState.value.updatedLocation.latitude,
                                    uiState.value.updatedLocation.longitude,
                                    it.latitude,
                                    it.longitude
                                )
                            )
                        }
                        .sortedBy {
                            it.distance
                        }
                        .take(50)
                    val message = ChatMessageUiState(
                        user = ChatElements(
                            text = fromUser
                        ),
                        bot = ChatElements(
                            text = text,
                            elementsFound = elementsFound,
                            retryOn = false
                        ),
                        isItemLoading = false,
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
                    placesUseCase.addMessages(message, uiState.value.updatedLocation)
                    _uiState.update {
                        it.copy(
                            messages = it.messages.dropLast(1) + message
                        )
                    }
                }

                is PlacesEventResult.Error -> {

                    text =
                        if (event.exception.message == "No places find") {
                            getString(Res.string.boterror1)
                        } else {
                            retry = true
                            getString(Res.string.boterror2)
                        }

                    _uiState.update {
                        it.copy(
                            messages = it.messages.dropLast(1) +
                                    ChatMessageUiState(
                                        user = ChatElements(
                                            text = fromUser
                                        ),
                                        bot = ChatElements(
                                            text = text,
                                            elementsFound = emptyList(),
                                            retryOn = retry
                                        ),
                                        isItemLoading = false,
                                        time = timeNow
                                    )
                        )
                    }
                }
            }
        }
    }

    fun getNearestPlaces(
        places: List<ElementsFoundUiState>
    ): List<ElementsFoundUiState> {
        return places
            .map { place ->
                uiState.value.updatedLocation.latitude
                place.copy(
                    distance = distanceInMeters(
                        userLatitude = uiState.value.updatedLocation.latitude,
                        userLongitude = uiState.value.updatedLocation.longitude,
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