package es.myvacations.myvacations.consentimiento

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class AdConsentManager(
    private val context: Context
) {
    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    fun requestConsent(
        activity: Activity,
        onComplete: () -> Unit
    ) {
        val params = ConsentRequestParameters.Builder()
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { error ->
                    if (error != null) {
                        Log.e(
                            "AdConsentManager",
                            "Error showing consent form: ${error.message}"
                        )
                    }

                    onComplete()
                }
            },
            { error ->
                Log.e("AdConsentManager", "Error updating consent: ${error.message}")
                onComplete()
            }
        )
    }

    fun canRequestAds(): Boolean =
        consentInformation.canRequestAds()
}