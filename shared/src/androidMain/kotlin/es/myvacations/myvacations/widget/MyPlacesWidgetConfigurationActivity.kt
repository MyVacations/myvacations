package es.myvacations.myvacations.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import es.myvacations.myvacations.shared.R
import kotlinx.coroutines.launch

class MyPlacesWidgetConfigurationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        lifecycleScope.launch {

            val appWidgetManager =
                AppWidgetManager.getInstance(
                    this@MyPlacesWidgetConfigurationActivity
                )

            val componentName = ComponentName(
                this@MyPlacesWidgetConfigurationActivity,
                MyPlacesWidgetReceiver::class.java
            )

            val existingWidgetIds =
                appWidgetManager.getAppWidgetIds(
                    componentName
                )

            val otherWidgetIds =
                existingWidgetIds.filter {
                    it != appWidgetId
                }

            if (otherWidgetIds.isNotEmpty()) {

                Toast.makeText(
                    this@MyPlacesWidgetConfigurationActivity,
                    this@MyPlacesWidgetConfigurationActivity.getString(R.string.only_1_widget),
                    Toast.LENGTH_LONG
                ).show()

                setResult(RESULT_CANCELED)
                finish()

                return@launch
            }

            setResult(
                RESULT_OK,
                intent
            )

            finish()
        }
    }
}