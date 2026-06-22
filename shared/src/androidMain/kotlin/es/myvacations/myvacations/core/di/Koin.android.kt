package es.myvacations.myvacations.core.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import es.myvacations.myvacations.data.di.dataModule
import es.myvacations.myvacations.domain.di.domainModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initLogger() {
    Napier.base(DebugAntilog())
}

private lateinit var appContext: Context

fun initializeKoinContext(context: Context) {
    appContext = context
}

actual fun initKoin() {
    initLogger()

    startKoin {
        androidContext(appContext)
        modules(
            buildList {
                add(appModule)
                add(domainModule)
                add(dataModule)
                addAll(dataBaseModule())
            }
        )
    }
}
