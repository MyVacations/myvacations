package es.myvacations.myvacations

import android.app.Application
import es.myvacations.myvacations.core.di.initKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}