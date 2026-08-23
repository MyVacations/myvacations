package es.myvacations.myvacations.core.di

import android.annotation.SuppressLint
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.functions.FirebaseFunctions
import es.myvacations.myvacations.core.firebase.AnalyticsReporter
import es.myvacations.myvacations.core.firebase.CrashReporter
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.database.MyVacationsDatabase
import es.myvacations.myvacations.data.datasource.remote.IntentClassifier
import es.myvacations.myvacations.data.datasource.remote.ModelVerifier
import es.myvacations.myvacations.data.repository.AndroidIntentClassifier
import es.myvacations.myvacations.data.repository.AndroidModelVerifier
import es.myvacations.myvacations.data.repository.ModelServiceRepositoryImpl
import es.myvacations.myvacations.domain.repository.ModelRepository
import es.myvacations.myvacations.firebase.AndroidAnalyticsReporter
import es.myvacations.myvacations.firebase.AndroidCrashReporter
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module


actual fun dataBaseModule(): List<Module> = listOf(
    module {
        single<MyVacationsDatabase> {
            val driver = AndroidSqliteDriver(
                schema = MyVacationsDatabase.Schema,
                context = androidContext(),
                name = "myvacations.db"
            )

            MyVacationsDatabase(driver)
        }
        @SuppressLint("MissingPermission")
        single {
            FirebaseAnalytics.getInstance(AndroidContextHolder.context)
        }

        single { FirebaseFunctions.getInstance("europe-southwest1") }

        single { FirebaseCrashlytics.getInstance() }

        single<AnalyticsReporter>
        {
            AndroidAnalyticsReporter(get())
        }
        single<CrashReporter> {
            AndroidCrashReporter(get())
        }
        single<IntentClassifier> {
            AndroidIntentClassifier(aiRepository = get())
        }
        single<ModelVerifier> {
            AndroidModelVerifier()
        }

        single<ModelRepository> {
            ModelServiceRepositoryImpl(
                remoteDataSource = get(),
                localDataSource = get(),
                verifier = get(),
                settingsRepository = get()
            )
        }
    }
)