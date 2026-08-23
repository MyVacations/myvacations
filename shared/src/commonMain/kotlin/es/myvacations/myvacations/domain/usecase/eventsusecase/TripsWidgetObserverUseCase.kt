package es.myvacations.myvacations.domain.usecase.eventsusecase

import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.domain.usecase.settingsusecase.GetSettingsUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetActiveTripUseCase
import es.myvacations.myvacations.presentation.mapper.toUiState
import es.myvacations.myvacations.presentation.utils.Currency
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

class TripsWidgetObserverUseCase(
    private val widgetUpdater: WidgetUpdater,
    private val getActiveTripUseCase: GetActiveTripUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) {
    suspend fun observe() {
        combine(
            getActiveTripUseCase(),
            getSettingsUseCase().distinctUntilChanged(),
        ) { trips, settings ->

            trips.map { trip ->
                trip.toUiState().copy(
                    currency = settings?.preferredCurrency ?: Currency.EURO
                )
            }

        }.collect { trips ->
            widgetUpdater.updateTripWidget(trips)
        }
    }

    suspend fun updateTrips() {
        val trips = getActiveTripUseCase().first()
        val settings = getSettingsUseCase().first()

        val tripsUi = trips.map { trip ->
            trip.toUiState().copy(
                currency = settings?.preferredCurrency ?: Currency.EURO
            )
        }

        widgetUpdater.updateTripWidget(tripsUi)
    }
}