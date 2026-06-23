package es.myvacations.myvacations.core.navigation

data class NavigationStateData(
    val currentScreen: String,
    val backStack: List<String>
)