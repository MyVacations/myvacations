package es.myvacations.myvacations.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import es.myvacations.myvacations.data.repository.WidgetMidnightScheduler
import es.myvacations.myvacations.data.repository.WidgetPlacesScheduler
import es.myvacations.myvacations.presentation.utils.WidgetUtils.hasActivePlacesWidget
import org.koin.core.component.KoinComponent


class MyPlacesWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceAppWidget
        get() = PlacesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WidgetMidnightScheduler.schedule(context)
        WidgetPlacesScheduler.refreshNow(context)
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        super.onDeleted(context, appWidgetIds)

        if (!hasActivePlacesWidget()) {

            WidgetMidnightScheduler.cancel(context)
            WidgetPlacesScheduler.cancel(context)
        }
    }
}