package es.myvacations.myvacations.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import es.myvacations.myvacations.data.repository.KindOfWidget
import es.myvacations.myvacations.data.repository.WidgetMidnightScheduler
import org.koin.core.component.KoinComponent


class MyTripsWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceAppWidget
        get() = MyVacationWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetMidnightScheduler.schedule(context, KindOfWidget.TRIPS)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        WidgetMidnightScheduler.cancel(context)
    }
}