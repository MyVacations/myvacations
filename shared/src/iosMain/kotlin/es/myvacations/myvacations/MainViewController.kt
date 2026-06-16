package es.myvacations.myvacations

import androidx.compose.ui.window.ComposeUIViewController
import es.myvacations.myvacations.core.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}