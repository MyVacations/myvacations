package es.myvacations.myvacations

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import es.myvacations.myvacations.core.navigation.NavigationRoot
import es.myvacations.myvacations.domain.manager.DatabaseInitializer
import es.myvacations.myvacations.domain.manager.NotificationObserverManager
import org.koin.compose.koinInject


@Composable
fun App() {
    val initializer: DatabaseInitializer = koinInject()
    val manager: NotificationObserverManager = koinInject()

    LaunchedEffect(Unit) {
        initializer.initialize()
        manager.start()
    }

    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        MaterialTheme(
            colorScheme = darkColorScheme(), content = {
                NavigationRoot(isLandscape)
            })
    }
}