package es.myvacations.myvacations.domain.manager

import es.myvacations.myvacations.domain.usecase.eventsusecase.ObserveTripForAlertsUseCase
import kotlinx.coroutines.flow.collect

class NotificationObserverManager(
    private val observeTripForAlertsUseCase: ObserveTripForAlertsUseCase
) {
    suspend fun start()
    {
        observeTripForAlertsUseCase().collect()
    }
}