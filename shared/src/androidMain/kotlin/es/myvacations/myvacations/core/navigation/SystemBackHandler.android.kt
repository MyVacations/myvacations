package es.myvacations.myvacations.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SystemBackHandler(onBack: () -> Unit) {
    BackHandler(
        enabled = true,
        onBack = onBack
    )
}