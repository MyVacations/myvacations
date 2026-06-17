package es.myvacations.myvacations.core.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NavigationState {
    private val backStack = mutableListOf<ScreenDestination>()

    var currentScreen by mutableStateOf<ScreenDestination>(
        ScreenDestination.Dashboard
    )
        private set

    fun navigateBottomBar(
        destination: ScreenDestination
    ) {
        clearBackStack()
        currentScreen = destination
    }

    fun navigate(destination: ScreenDestination) {
        backStack.add(currentScreen)
        currentScreen = destination
    }

    fun clearBackStack() {
        backStack.clear()
    }

    fun popBackStack() {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
        }
    }
}