package es.myvacations.myvacations.data.repository

import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.widget.MyVacationWidget
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

actual class AppWidgetUpdaterRepository :
    WidgetUpdater {

    actual override suspend fun update(trip: List<TripUiState>?) {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            MyVacationWidget::class.java
        )
        val getTripsInOrder =
            trip
                ?.filter { it.tripStatus == TripStatus.ACTIVE }
                ?.minByOrNull { it.endDate }
                ?: trip
                    ?.filter { it.tripStatus == TripStatus.PLANNED }
                    ?.minByOrNull { it.startDate }

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                if (getTripsInOrder != null) {
                    preferences[ActiveTripIdKey] = getTripsInOrder.id
                    preferences[ActiveTripTitleKey] = getTripsInOrder.titleTrip
                    preferences[ActiveTripPlaceKey] = getTripsInOrder.placeTrip.displayName()
                    preferences[ActiveTripStartDateKey] = getTripsInOrder.startDate.toString()
                    preferences[ActiveTripEndDateKey] = getTripsInOrder.endDate.toString()
                    preferences[ActiveTripMainCostKey] = getTripsInOrder.mainCost
                    preferences[ActiveTripMainBudgetKey] = getTripsInOrder.mainBudget
                    preferences[ActiveTripCoverKey] = getTripsInOrder.cover.name
                    preferences[ActiveTripExpensesKey] =
                        Json.encodeToString(
                            getTripsInOrder.optionalExpenses
                        )
                    preferences[ActiveTripFavouriteKey] = getTripsInOrder.favourite

                } else {
                    preferences.remove(ActiveTripIdKey)
                    preferences.remove(ActiveTripTitleKey)
                    preferences.remove(ActiveTripPlaceKey)
                    preferences.remove(ActiveTripStartDateKey)
                    preferences.remove(ActiveTripEndDateKey)
                    preferences.remove(ActiveTripMainCostKey)
                    preferences.remove(ActiveTripMainBudgetKey)
                    preferences.remove(ActiveTripCoverKey)
                    preferences.remove(ActiveTripExpensesKey)
                    preferences.remove(ActiveTripFavouriteKey)
                }
            }
            MyVacationWidget().update(
                context = context,
                id = glanceId
            )
        }
    }
}