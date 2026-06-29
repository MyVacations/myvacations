package es.myvacations.myvacations

import android.app.Application
import es.myvacations.myvacations.core.di.initKoin
import es.myvacations.myvacations.core.utils.AndroidContextHolder


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.initialize(this)
        initKoin()
    }
}