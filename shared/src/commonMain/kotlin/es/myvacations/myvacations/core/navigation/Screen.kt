package es.myvacations.myvacations.core.navigation

sealed interface Screen {
    data object Dashboard : Screen
    data object Trips : Screen
    data object Statistics : Screen
    data object Settings : Screen
}