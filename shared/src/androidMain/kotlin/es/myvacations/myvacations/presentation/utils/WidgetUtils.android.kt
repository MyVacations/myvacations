package es.myvacations.myvacations.presentation.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.widget.MyVacationsWidgetReceiver

actual object WidgetUtils {

    actual fun hasActiveWidget(): Boolean  {
        val appWidgetManager = AppWidgetManager.getInstance(AndroidContextHolder.context)

        val componentName = ComponentName(
            AndroidContextHolder.context,
            MyVacationsWidgetReceiver::class.java
        )

        return appWidgetManager
            .getAppWidgetIds(componentName)
            .isNotEmpty()
    }
}