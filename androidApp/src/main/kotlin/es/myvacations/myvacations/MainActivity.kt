package es.myvacations.myvacations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.functions.FirebaseFunctions
import es.myvacations.myvacations.data.repository.AdsRepositoryImpl
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshObserveTripsWidget
import es.myvacations.myvacations.presentation.utils.WidgetUtils.refreshPlacesWidget
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val tripId = mutableStateOf("")
    private val widgetAction = mutableStateOf("")
    private fun getCurrentVersion(): String {
        return packageManager
            .getPackageInfo(packageName, 0)
            .versionName
            ?.substringBefore("-")
            ?: "0.0.0"
    }

    private lateinit var minimumVersion: String
    private val functions = FirebaseFunctions.getInstance("europe-southwest1")
    val adsRepository: AdsRepositoryImpl by inject()
    private val updateLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode != RESULT_OK) {
                Log.d(
                    "VersionCheck",
                    "Mandatory update cancelled"
                )

                finishAndRemoveTask()
            }
        }
    private lateinit var appUpdateManager: AppUpdateManager


    private fun processWidgetIntent(intent: Intent?) {
        intent ?: return
        tripId.value = intent.getStringExtra("trip_id").orEmpty()
        widgetAction.value = intent.getStringExtra("widget_action").orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        adsRepository.setActivity(this)
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

        val application = application as MainApplication
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(""))
            .build()

        MobileAds.setRequestConfiguration(configuration)
        application.consentManager.requestConsent(this) {
            if (application.consentManager.canRequestAds()) {
                MobileAds.initialize(this) {
                    adsRepository.loadInterstitial(this)
                }
            }
        }
        lifecycleScope.launch {
            try {
                val result = functions
                    .getHttpsCallable("getMinimumVersion")
                    .call()
                    .await()

                val data = result.data as Map<*, *>
                minimumVersion = data["minimumVersion"] as? String ?: throw IllegalStateException(
                    "minimumVersion no encontrada"
                )

                Log.d(
                    "VersionCheck",
                    "Minimum version: $minimumVersion"
                )

                appUpdateManager =
                    AppUpdateManagerFactory.create(this@MainActivity)

                checkForUpdate(minimumVersion)
            } catch (e: Exception) {
                Log.e(
                    "VersionCheck",
                    "Error al obtener la versión mínima",
                    e
                )
            }
        }
        setContent {
            App(tripId.value, widgetAction.value)
        }
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            refreshPlacesWidget()
            refreshObserveTripsWidget()
        }
        if (::appUpdateManager.isInitialized) {
            appUpdateManager.appUpdateInfo
                .addOnSuccessListener { info ->

                    if (
                        info.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        startImmediateUpdate(info)
                    }
                }
        }
    }

    private fun startImmediateUpdate(
        info: com.google.android.play.core.appupdate.AppUpdateInfo
    ) {
        appUpdateManager.startUpdateFlowForResult(
            info,
            updateLauncher,
            AppUpdateOptions.newBuilder(
                AppUpdateType.IMMEDIATE
            ).build()
        )
    }

    private fun checkForUpdate(minimumVersion: String) {
        val currentVersion = getCurrentVersion()

        if (!isVersionLower(currentVersion, minimumVersion)) {
            return
        }

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->

                when {
                    info.updateAvailability() ==
                            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {

                        startImmediateUpdate(info)
                    }

                    info.updateAvailability() ==
                            UpdateAvailability.UPDATE_AVAILABLE &&
                            info.isUpdateTypeAllowed(
                                AppUpdateType.IMMEDIATE
                            ) -> {

                        startImmediateUpdate(info)
                    }
                }
            }
    }

    private fun isVersionLower(
        currentVersion: String,
        minimumVersion: String
    ): Boolean {
        val current = currentVersion
            .split(".")
            .map { it.toIntOrNull() ?: 0 }

        val minimum = minimumVersion.split(".")
            .map { it.toInt() }

        val maxSize = maxOf(current.size, minimum.size)

        for (i in 0 until maxSize) {
            val currentPart = current.getOrElse(i) { 0 }
            val minimumPart = minimum.getOrElse(i) { 0 }

            if (currentPart < minimumPart) {
                return true
            }

            if (currentPart > minimumPart) {
                return false
            }
        }

        return false
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processWidgetIntent(intent)
    }
}