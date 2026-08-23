package es.myvacations.myvacations.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import es.myvacations.myvacations.data.repository.WidgetMidnightScheduler
import es.myvacations.myvacations.data.repository.WidgetPermissionScheduler
import es.myvacations.myvacations.domain.usecase.eventsusecase.PlacesWidgetObserverUseCase
import es.myvacations.myvacations.presentation.utils.WidgetUtils.hasActivePlacesWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent


class MyPlacesWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {

    override val glanceAppWidget: GlanceAppWidget
        get() = PlacesWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        WidgetMidnightScheduler.schedule(context)
        WidgetPermissionScheduler.schedule(context)

        CoroutineScope(Dispatchers.IO).launch {

            val useCase: PlacesWidgetObserverUseCase by
            KoinJavaComponent.inject(
                PlacesWidgetObserverUseCase::class.java
            )

            useCase.refreshWidget()
        }
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        super.onDeleted(context, appWidgetIds)

        if (!hasActivePlacesWidget()) {

            WidgetMidnightScheduler.cancel(context)
            WidgetPermissionScheduler.cancel(context)
        }
    }
}