package es.myvacations.myvacations.core.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun initLogger() {
    Napier.base(DebugAntilog())
}

fun initKoin() {
    initLogger()

    startKoin {
        modules(appModule)
    }
}