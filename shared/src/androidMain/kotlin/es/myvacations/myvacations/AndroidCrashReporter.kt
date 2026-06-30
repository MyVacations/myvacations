package es.myvacations.myvacations

import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.myvacations.myvacations.core.firebase.CrashReporter

class AndroidCrashReporter : CrashReporter {

    override fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    override fun recordException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}