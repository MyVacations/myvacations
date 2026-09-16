package es.myvacations.myvacations.presentation.utils

expect object WidgetUtils {
    fun hasActiveTripsWidget(): Boolean
    fun hasActivePlacesWidget(): Boolean
    suspend fun refreshObserveTripsWidget()
    suspend fun refreshTripsWidget() : Boolean
    suspend fun refreshPlacesWidget() : Boolean
}