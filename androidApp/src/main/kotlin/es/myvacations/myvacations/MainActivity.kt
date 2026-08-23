package es.myvacations.myvacations

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshPlacesWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val tripId = mutableStateOf("")
    private val widgetAction = mutableStateOf("")

    private fun processWidgetIntent(intent: Intent?) {
        intent ?: return

        tripId.value = intent.getStringExtra("trip_id").orEmpty()
        widgetAction.value = intent.getStringExtra("widget_action").orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()

        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition {
            false
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            )
        )
        processWidgetIntent(intent)

        setContent {
            App(tripId.value, widgetAction.value)
        }
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            refreshPlacesWidget()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processWidgetIntent(intent)
    }
}