package es.myvacations.myvacations.firebase

import android.Manifest
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.google.firebase.analytics.FirebaseAnalytics
import es.myvacations.myvacations.core.firebase.AnalyticsEvent
import es.myvacations.myvacations.core.firebase.AnalyticsReporter

class AndroidAnalyticsReporter(
    private val firebaseAnalytics : FirebaseAnalytics
): AnalyticsReporter {
    @RequiresPermission(allOf = [Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK])
    override fun logEvent(
        event: AnalyticsEvent,
        params: Map<String, Any?>
    ) {
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
        firebaseAnalytics.logEvent(event.name.lowercase(), bundle)
    }
}
