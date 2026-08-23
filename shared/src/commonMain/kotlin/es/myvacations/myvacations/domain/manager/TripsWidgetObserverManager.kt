package es.myvacations.myvacations.domain.manager

import es.myvacations.myvacations.domain.usecase.eventsusecase.TripsWidgetObserverUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TripsWidgetObserverManager(
    private val tripsWidgetObserverUseCase: TripsWidgetObserverUseCase
) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private var observerJob: Job? = null

    fun start() {
        if (observerJob?.isActive == true) return

        observerJob = scope.launch {
            tripsWidgetObserverUseCase.observe()
        }
    }

    fun update() {
        if (observerJob?.isActive == null) return
        observerJob = scope.launch {
            tripsWidgetObserverUseCase.updateTrips()
        }
    }

    fun stop() {
        observerJob?.cancel()
        observerJob = null
    }
}