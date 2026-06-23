package es.myvacations.myvacations

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import es.myvacations.myvacations.core.navigation.NavigationRoot


@Composable
fun App() {
    BoxWithConstraints {
        val isLandscape = maxWidth > maxHeight
        MaterialTheme(
            colorScheme = darkColorScheme(),
            content = {
                NavigationRoot(isLandscape)
            }
        )
    }
}