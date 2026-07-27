package es.myvacations.myvacations

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import es.myvacations.myvacations.core.navigation.NavigationRoot
import es.myvacations.myvacations.domain.manager.DatabaseInitializer
import es.myvacations.myvacations.domain.manager.NotificationObserverManager
import es.myvacations.myvacations.domain.manager.WidgetObserverManager
import es.myvacations.myvacations.presentation.utils.WidgetUtils
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun App(
    tripId: String,
    value: String,
) {
    val initializer: DatabaseInitializer = koinInject()
    val manager: NotificationObserverManager = koinInject()
    val widgetObserverManager: WidgetObserverManager = koinInject()
    LaunchedEffect(Unit)
    {
        initializer.initialize()
        launch {
            manager.start()
        }
        if (WidgetUtils.hasActiveWidget()) {
            launch {
                widgetObserverManager.start()
            }
        }
    }

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        MaterialTheme(
            colorScheme = darkColorScheme(), content = {
                NavigationRoot(isLandscape, tripId, value)
            })
    }
}