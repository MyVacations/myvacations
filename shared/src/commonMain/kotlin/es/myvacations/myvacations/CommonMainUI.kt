package es.myvacations.myvacations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.core.navigation.NavigationRoot
import es.myvacations.myvacations.domain.manager.DatabaseInitializer
import es.myvacations.myvacations.domain.manager.NotificationObserverManager
import es.myvacations.myvacations.domain.manager.TripsWidgetObserverManager
import es.myvacations.myvacations.presentation.utils.WidgetUtils
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class InitializationState {
    LOADING,
    READY,
    ERROR
}

@Composable
fun App(
    tripId: String,
    value: String,
) {

    var initializationState by remember {
        mutableStateOf(InitializationState.LOADING)
    }
    val initializer: DatabaseInitializer = koinInject()
    val manager: NotificationObserverManager = koinInject()
    val tripsWidgetObserverManager: TripsWidgetObserverManager = koinInject()
    val analytics: AnalyticsReporter = koinInject()
    LaunchedEffect(Unit)
    {
        initializationState = try {
            initializer.initialize()
            launch {
                manager.start()
            }
            if (WidgetUtils.hasActiveTripsWidget()) {
                launch {
                    tripsWidgetObserverManager.start()
                }
            }
            InitializationState.READY
        } catch (e: Exception) {
            InitializationState.ERROR
        }
        if (initializationState == InitializationState.READY) {
            analytics.logEvent(
                AnalyticsEvent.SCREEN_VIEW,
                mapOf("screen" to "main")
            )
        }
    }

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        MaterialTheme(
            colorScheme = darkColorScheme(), content = {
                when (initializationState) {

                    InitializationState.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    InitializationState.READY -> {
                        NavigationRoot(
                            isLandscape = isLandscape,
                            tripIdFromWidget = tripId,
                            widgetAction = value
                        )
                    }

                    InitializationState.ERROR -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(onClick = {
                                initializationState = InitializationState.LOADING
                            }){
                                Text("Error")
                            }
                        }
                    }
                }
            })
    }
}