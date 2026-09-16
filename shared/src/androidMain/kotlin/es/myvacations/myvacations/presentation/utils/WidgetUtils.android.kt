package es.myvacations.myvacations.presentation.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.usecase.eventsusecase.PlacesWidgetObserverUseCase
import es.myvacations.myvacations.domain.usecase.eventsusecase.TripsWidgetObserverUseCase
import es.myvacations.myvacations.widget.MyPlacesWidgetReceiver
import es.myvacations.myvacations.widget.MyTripsWidgetReceiver
import org.koin.java.KoinJavaComponent

actual object WidgetUtils {

    actual fun hasActiveTripsWidget(): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(AndroidContextHolder.context)

        val componentName = ComponentName(
            AndroidContextHolder.context,
            MyTripsWidgetReceiver::class.java
        )

        return appWidgetManager
            .getAppWidgetIds(componentName)
            .isNotEmpty()
    }

    actual fun hasActivePlacesWidget(): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(AndroidContextHolder.context)

        val componentName = ComponentName(
            AndroidContextHolder.context,
            MyPlacesWidgetReceiver::class.java
        )

        return appWidgetManager
            .getAppWidgetIds(componentName)
            .isNotEmpty()
    }

    actual suspend fun refreshObserveTripsWidget() {
        val useCase: TripsWidgetObserverUseCase by
        KoinJavaComponent.inject(
            TripsWidgetObserverUseCase::class.java
        )
        useCase.observe()
    }

    actual suspend fun refreshTripsWidget(): Boolean {
        val useCase: TripsWidgetObserverUseCase by
        KoinJavaComponent.inject(
            TripsWidgetObserverUseCase::class.java
        )
        return useCase.updateTrips()
    }

    actual suspend fun refreshPlacesWidget(): Boolean {
        val useCase: PlacesWidgetObserverUseCase by
        KoinJavaComponent.inject(
            PlacesWidgetObserverUseCase::class.java
        )
        return useCase.refreshWidget()
    }
}