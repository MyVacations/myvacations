package es.myvacations.myvacations

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.ComposeUIViewController
import es.myvacations.myvacations.core.di.initKoin
import io.github.aakira.napier.Napier

fun MainViewController() = ComposeUIViewController {
    initKoin()
    val tripId = mutableStateOf("")
    val widgetAction = mutableStateOf("")
    App(tripId.value,widgetAction.value)
}