package es.myvacations.myvacations.firebase

import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.myvacations.myvacations.core.firebase.CrashReporter

class AndroidCrashReporter(
    private val crashlytics: FirebaseCrashlytics
) : CrashReporter {
    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}