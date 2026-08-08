package es.myvacations.myvacations.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.domain.repository.SettingsRepository
import es.myvacations.myvacations.domain.usecase.chatbot.ClassifyIntentUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.latestmodelrelease.EnsureModelInstalledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val ensureModelInstalledUseCase: EnsureModelInstalledUseCase,
    private val classifyIntentUseCase: ClassifyIntentUseCase,
    private val repository: SettingsRepository,
    private val analytics: AnalyticsReporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState()
    )
    val uiState = _uiState.asStateFlow()
    val state = ensureModelInstalledUseCase.modelState
    var userText = ""
    var iaResponse = ""
    var iaConfidence = 0f

    init {
        analytics.logEvent(
            AnalyticsEvent.SCREEN_VIEW,
            mapOf("screen" to "chatbot")
        )
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            repository.getSettings().collect { settingsDomain ->
                _uiState.update {
                    it.copy(tutorial = settingsDomain?.iaTutorial ?: false)
                }
            }
        }
        viewModelScope.launch {
            ensureModelInstalledUseCase.refreshState()
            _uiState.update {
                it.copy(isLoading = false)
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

    fun sendASearch(text: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(messages = it.messages + ChatMessageUiState(text, false))
            }
            userText = text
            classifyIntentUseCase(text)

            //iaResponse = classifyIntentUseCase(text).predictions.maxBy { it.confidence }.label
            //iaConfidence = classifyIntentUseCase(text).predictions.maxBy { it.confidence }.confidence
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + ChatMessageUiState(
                        iaResponse,
                        true
                    )
                )
            }
            analytics.logEvent(
                AnalyticsEvent.AI_PREDICTION,
                mapOf("screen" to "chatbot")
            )

        }
    }

    //fun feedback(catego)
}