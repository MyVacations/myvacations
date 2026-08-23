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

class PlacesWidgetObserverUseCase(
    private val widgetUpdater: WidgetUpdater,
    private val placesUseCase: PlacesUseCase,
    private val locationUseCase: MapAndLocationUseCase,
    private val ensureModelInstalledUseCase: EnsureModelInstalledUseCase,
) {
    var lastWidgetLocation: LocationDomain? = null

    suspend fun refreshWidget() {
        widgetUpdater.updateLocationLoading()

        if(!ensureModelInstalledUseCase.checkModelStatus() || ensureModelInstalledUseCase.isUpdateAvailable())
        {
            widgetUpdater.noModelInstallOrUpdate()
            return
        }

        when (val locationEvent = locationUseCase.getCurrentLocation()) {

            is LocationEventResult.PermissionDenied -> {
                widgetUpdater.updateLocationPermission(false)
                return
            }

            is LocationEventResult.Success -> {
                widgetUpdater.updateLocationPermission(true)

                val widgetPlace = getWidgetPlace(
                    locationEvent.locationDomain
                )

                if (widgetPlace != null) {
                    widgetUpdater.updatePlacesWidget(widgetPlace)
                }
                else {
                    widgetUpdater.noMessagesLoad()
                }

                return
            }

            else -> {
                widgetUpdater.updateLocationError()
            }
        }
    }

    suspend fun updateForLocation(
        currentLocation: LocationDomain
    ) {
        val previousLocation = lastWidgetLocation

        if (previousLocation != null) {

            val distance = distanceInMeters(
                userLatitude = currentLocation.latitude,
                userLongitude = currentLocation.longitude,
                latitude = previousLocation.latitude,
                longitude = previousLocation.longitude
            )

            if (distance < 20) {
                return
            }
        }

        val messages = placesUseCase.getMessages().first()
        val lastMessage = messages.maxByOrNull { it.time }

        if (lastMessage == null) return

        val hasNearbyPlace =
            lastMessage.bot?.elementsFound?.any { place ->
                distanceInMeters(
                    userLatitude = currentLocation.latitude,
                    userLongitude = currentLocation.longitude,
                    latitude = place.latitude,
                    longitude = place.longitude
                ) <= 500
            } == true

        val widgetPlace = if (hasNearbyPlace) {
            WidgetPlace(
                mainLocation = currentLocation.toUiMapper(),
                elementsFound = lastMessage.bot.elementsFound
            )
        } else {
            lastMessage.bot?.elementsFound?.let { elementsFound ->
                WidgetPlace(
                    mainLocation = lastMessage.mainLocation,
                    elementsFound = elementsFound
                )
            }
        }
        widgetUpdater.updatePlacesWidget(widgetPlace)
        lastWidgetLocation = currentLocation
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
            lastMessage.bot?.elementsFound?.let { elementsFound ->
                WidgetPlace(
                    mainLocation = lastMessage.mainLocation,
                    elementsFound = elementsFound
                )
            }
        }
    }

}