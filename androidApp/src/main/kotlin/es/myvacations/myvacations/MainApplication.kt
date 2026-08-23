package es.myvacations.myvacations

import android.app.Application
import es.myvacations.myvacations.core.di.initKoin
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.firebase.FirebaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val context = applicationContext
        AndroidContextHolder.initialize(context)
        FirebaseInitializer.initialize(context)

        initKoin()
    }
}