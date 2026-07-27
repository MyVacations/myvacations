package es.myvacations.myvacations.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import es.myvacations.myvacations.data.repository.WidgetMidnightScheduler
import es.myvacations.myvacations.domain.manager.WidgetObserverManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MyVacationsWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    private val widgetObserverManager: WidgetObserverManager by inject()

    override val glanceAppWidget: GlanceAppWidget
        get() = MyVacationWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        widgetObserverManager.start()
        WidgetMidnightScheduler.schedule(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        widgetObserverManager.stop()
        WidgetMidnightScheduler.cancel(context)
    }
}