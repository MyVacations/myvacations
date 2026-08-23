package es.myvacations.myvacations.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import es.myvacations.myvacations.data.repository.WidgetMidnightScheduler
import es.myvacations.myvacations.domain.manager.TripsWidgetObserverManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MyTripsWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    private val tripsWidgetObserverManager: TripsWidgetObserverManager by inject()

    override val glanceAppWidget: GlanceAppWidget
        get() = MyVacationWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        tripsWidgetObserverManager.start()
        WidgetMidnightScheduler.schedule(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        tripsWidgetObserverManager.stop()
        WidgetMidnightScheduler.cancel(context)
    }
}