package es.myvacations.myvacations.core.di

import es.myvacations.myvacations.data.di.dataModule
import es.myvacations.myvacations.domain.di.domainModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun initLogger() {
    Napier.base(DebugAntilog())
}

actual fun initKoin() {
    initLogger()

    startKoin {
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