package es.myvacations.myvacations

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import es.myvacations.myvacations.core.firebase.AnalyticsReporter

class AndroidAnalyticsReporter(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsReporter {

    override fun logEvent(
        name: String,
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

        firebaseAnalytics.logEvent(name, bundle)
    }
}