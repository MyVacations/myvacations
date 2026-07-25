package es.myvacations.myvacations.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import es.myvacations.myvacations.domain.repository.WidgetUpdater
import es.myvacations.myvacations.domain.usecase.eventsusecase.WidgetObserverUseCase
import es.myvacations.myvacations.domain.usecase.tripusecase.GetActiveTripUseCase
import es.myvacations.myvacations.presentation.mapper.toUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.mp.KoinPlatform.getKoin

class DateChangedReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (
            intent.action != Intent.ACTION_DATE_CHANGED &&
            intent.action != Intent.ACTION_TIME_CHANGED
        ) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val widgetObserverUseCase: WidgetObserverUseCase =
                    GlobalContext.get().get()

               widgetObserverUseCase.update()
            } finally {
                pendingResult.finish()
            }
        }
    }
}