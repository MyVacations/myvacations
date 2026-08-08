package es.myvacations.myvacations.firebase

import android.Manifest
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.google.firebase.analytics.FirebaseAnalytics
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import io.github.aakira.napier.Napier

class AndroidAnalyticsReporter: AnalyticsReporter {

    @RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
    override fun logEvent(
        event: AnalyticsEvent,
        params: Map<String, Any?>
    ) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(AndroidContextHolder.context)
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        Napier.d(
            tag = "Analytics",
            message = "Event=${event.name.lowercase()}, params=$params"
        )
        //firebaseAnalytics.logEvent(event.name.lowercase(), bundle)
    }
}
