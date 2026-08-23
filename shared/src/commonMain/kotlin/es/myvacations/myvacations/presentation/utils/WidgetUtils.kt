package es.myvacations.myvacations.presentation.utils

expect object WidgetUtils {
    fun hasActiveTripsWidget(): Boolean
    fun hasActivePlacesWidget(): Boolean
    suspend fun refreshPlacesWidget()
}