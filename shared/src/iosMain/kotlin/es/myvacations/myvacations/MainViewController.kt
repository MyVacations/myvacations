package es.myvacations.myvacations

import androidx.compose.ui.window.ComposeUIViewController
import es.myvacations.myvacations.core.di.initKoin
import es.myvacations.myvacations.data.database.di.platformModule

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}