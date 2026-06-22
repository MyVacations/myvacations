package es.myvacations.myvacations

import android.app.Application
import es.myvacations.myvacations.core.di.initKoin
import es.myvacations.myvacations.core.di.initializeKoinContext


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoinContext(this)
        initKoin()
    }
}