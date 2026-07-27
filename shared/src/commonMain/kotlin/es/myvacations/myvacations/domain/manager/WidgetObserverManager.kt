package es.myvacations.myvacations.domain.manager

import es.myvacations.myvacations.domain.usecase.eventsusecase.WidgetObserverUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WidgetObserverManager(
    private val widgetObserverUseCase: WidgetObserverUseCase
) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private var observerJob: Job? = null

    fun start() {
        if (observerJob?.isActive == true) return

        observerJob = scope.launch {
            widgetObserverUseCase.observe()
        }
    }

    fun update() {
        if (observerJob?.isActive == null) return
        observerJob = scope.launch {
            widgetObserverUseCase.update()
        }
    }

    fun stop() {
        observerJob?.cancel()
        observerJob = null
    }
}