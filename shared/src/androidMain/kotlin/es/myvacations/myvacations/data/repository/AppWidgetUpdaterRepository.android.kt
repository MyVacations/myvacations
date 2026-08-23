package es.myvacations.myvacations.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.model.TripStatus
import es.myvacations.myvacations.domain.model.displayName
import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.presentation.chatbot.WidgetPlace
import es.myvacations.myvacations.presentation.createedittrip.TripUiState
import es.myvacations.myvacations.widget.MapBitmapGenerator
import es.myvacations.myvacations.widget.MyVacationWidget
import es.myvacations.myvacations.widget.PlacesWidget
import kotlinx.serialization.json.Json

actual class AppWidgetUpdaterRepositoryImpl :
    WidgetUpdater {
    actual override suspend fun updateTripWidget(trip: List<TripUiState>?) {
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


    actual override suspend fun updatePlacesWidget(
        widgetElement: WidgetPlace?
    ) {
        val context = AndroidContextHolder.context

        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        val mapFile = widgetElement?.let {
            MapBitmapGenerator.generateMapBitmap(
                context = context,
                location = it.mainLocation,
                places = it.elementsFound,
                width = 613,
                height = 340
            )
        }

        val mapUri = mapFile?.let {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                it
            )
        }

        mapUri?.let { uri ->
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }

            val homeActivities = context.packageManager
                .queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

            homeActivities.forEach { resolveInfo ->
                context.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLocationPermissionsKey] = widgetElement != null
                preferences[ActiveLoadingLocationKey] = false
                preferences[ActiveErrorLocationKey] = false
                preferences[ActiveNoMessagesKey] = false
                preferences[ActiveNoModelKey] = false
                if (mapFile != null) {
                    preferences[ActiveMapFileKey] = mapUri.toString()
                } else {
                    preferences.remove(ActiveMapFileKey)
                }
            }

            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }

    actual override suspend fun updateLocationPermission(hasLocationPermission: Boolean) {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLocationPermissionsKey] = hasLocationPermission
            }
            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }

    actual override suspend fun updateLocationLoading() {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLoadingLocationKey] = true
                preferences[ActiveErrorLocationKey] = false
                preferences[ActiveNoMessagesKey] = false
                preferences[ActiveNoModelKey] = false
            }
            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }

    actual override suspend fun updateLocationError() {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLoadingLocationKey] = false
                preferences[ActiveErrorLocationKey] = true
                preferences[ActiveNoMessagesKey] = false
                preferences[ActiveNoModelKey] = false
            }
            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }

    actual override suspend fun noMessagesLoad() {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLoadingLocationKey] = false
                preferences[ActiveErrorLocationKey] = false
                preferences[ActiveNoMessagesKey] = true
                preferences[ActiveNoModelKey] = false
            }
            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }

    actual override suspend fun noModelInstallOrUpdate() {
        val context = AndroidContextHolder.context
        val manager = GlanceAppWidgetManager(context)

        val glanceIds = manager.getGlanceIds(
            PlacesWidget::class.java
        )

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                glanceId = glanceId
            ) { preferences ->
                preferences[ActiveLoadingLocationKey] = false
                preferences[ActiveErrorLocationKey] = false
                preferences[ActiveNoMessagesKey] = false
                preferences[ActiveNoModelKey] = true
            }
            PlacesWidget().update(
                context = context,
                id = glanceId
            )
        }
    }
}