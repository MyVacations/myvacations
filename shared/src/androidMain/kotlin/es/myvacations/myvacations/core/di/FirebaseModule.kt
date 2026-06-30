package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.AndroidCrashReporter
import es.myvacations.myvacations.core.firebase.CrashReporter
import org.koin.dsl.module

val firebaseModule = module {
    //Analytics desactivado inicialmente hasta que implementes una pantalla de consentimiento o determines que, según tu caso de uso y la normativa aplicable, no necesitas ese consentimiento.
    /*
    single {
        FirebaseAnalytics.getInstance(AndroidContextHolder.context)
    }

    single<AnalyticsReporter> {
        AndroidAnalyticsReporter(get())
    }
     */
    single<CrashReporter> {
        AndroidCrashReporter()
    }
}