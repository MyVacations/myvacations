package es.myvacations.myvacations.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import es.myvacations.myvacations.data.repository.DateChangedReceiver.Companion.WIDGET_MIDNIGHT
import java.util.Calendar

object WidgetMidnightScheduler {
    private const val REQUEST_CODE = 1001

    private fun createPendingIntent(
        context: Context
    ): PendingIntent {

        val intent = Intent(
            context,
            DateChangedReceiver::class.java
        ).apply {
            action = WIDGET_MIDNIGHT
        }

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(context: Context?) {
        val alarmManager = context?.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val pendingIntent = createPendingIntent(context)

        val nextMidnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextMidnight.timeInMillis,
            pendingIntent
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        alarmManager.cancel(
            createPendingIntent(context)
        )
    }

}