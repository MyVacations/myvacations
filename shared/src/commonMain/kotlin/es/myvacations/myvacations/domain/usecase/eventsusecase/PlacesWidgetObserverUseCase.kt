package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.mapper.toUiMapper
import es.myvacations.myvacations.domain.model.locations.LocationDomain
import es.myvacations.myvacations.domain.repository.LocationEventResult
import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.domain.usecase.chatbot.MapAndLocationUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.latestmodelrelease.EnsureModelInstalledUseCase
import es.myvacations.myvacations.domain.usecase.chatbot.overpass.PlacesUseCase
import es.myvacations.myvacations.presentation.chatbot.WidgetPlace
import es.myvacations.myvacations.presentation.utils.distanceInMeters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

class PlacesWidgetObserverUseCase(
    private val widgetUpdater: WidgetUpdater,
    private val placesUseCase: PlacesUseCase,
    private val locationUseCase: MapAndLocationUseCase,
    private val ensureModelInstalledUseCase: EnsureModelInstalledUseCase,
) {
    suspend fun refreshWidget() {
        if (!refreshWidgetInternal()) {
            if (!refreshWidgetInternal()) {
                widgetUpdater.updateLocationError()
            }
        }
    }

    private suspend fun refreshWidgetInternal(): Boolean {

        val result = withTimeoutOrNull(20_000L.milliseconds) {

            try {

                widgetUpdater.updateLocationLoading()

                val modelInstalled =
                    ensureModelInstalledUseCase.checkModelStatus()

                val updateAvailable =
                    ensureModelInstalledUseCase.isUpdateAvailable()

                if (!modelInstalled || updateAvailable) {

                    widgetUpdater.noModelInstallOrUpdate()

                    return@withTimeoutOrNull true
                }

                when (
                    val locationEvent =
                        locationUseCase.getCurrentLocation()
                ) {

                    is LocationEventResult.PermissionDenied -> {

                        widgetUpdater.updateLocationPermission(false)
                    }

                    is LocationEventResult.Success -> {

                        widgetUpdater.updateLocationPermission(true)

                        val widgetPlace =
                            getWidgetPlace(
                                locationEvent.locationDomain
                            )

                        if (widgetPlace != null) {

                            widgetUpdater.updatePlacesWidget(
                                widgetPlace
                            )

                        } else {

                            widgetUpdater.noMessagesLoad()
                        }
                    }

                    else -> {

                        widgetUpdater.updateLocationError()
                    }
                }

                true

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

                widgetUpdater.updateLocationError()

                false
            }
        }

        if (result == null) {

            widgetUpdater.updateLocationError()

            return false
        }

        return result
    }

    suspend fun update() {
        refreshWidget()
    }

    private suspend fun getWidgetPlace(
        currentLocation: LocationDomain
    ): WidgetPlace? {
        val messages = placesUseCase.getMessages().first()

        val lastMessage =
            messages.maxByOrNull { it.time } ?: return null

        val hasNearbyPlace =
            lastMessage.bot?.elementsFound?.any { place ->
                distanceInMeters(
                    userLatitude = currentLocation.latitude,
                    userLongitude = currentLocation.longitude,
                    latitude = place.latitude,
                    longitude = place.longitude
                ) <= 500
            } == true

        return if (hasNearbyPlace) {
            WidgetPlace(
                mainLocation = currentLocation.toUiMapper(),
                elementsFound = lastMessage.bot.elementsFound
            )
        } else {
            widgetUpdater.outOfLimits()
            lastMessage.bot?.elementsFound?.let { elementsFound ->
                WidgetPlace(
                    mainLocation = lastMessage.locationFor500m,
                    elementsFound = elementsFound
                )
            }
        }
    }
}