package es.myvacations.myvacations.core.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NavigationState(
    initialScreen: ScreenDestination = ScreenDestination.Dashboard,
    initialBackStack: List<ScreenDestination> = emptyList()
) {
    private val backStack = initialBackStack.toMutableList()

    var currentScreen by mutableStateOf(initialScreen)
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

    fun getBackStack(): List<ScreenDestination> {
        return backStack.toList()
    }
}