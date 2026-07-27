package es.myvacations.myvacations.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import es.myvacations.myvacations.domain.manager.WidgetObserverManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DateChangedReceiver : BroadcastReceiver() {

    companion object {
        const val WIDGET_MIDNIGHT =
            "es.myvacations.myvacations.WIDGET_MIDNIGHT"
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_CHANGED, WIDGET_MIDNIGHT -> {

                val pendingResult = goAsync()

                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        val widgetObserverManager: WidgetObserverManager =
                            GlobalContext.get().get()
                        widgetObserverManager.update()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}