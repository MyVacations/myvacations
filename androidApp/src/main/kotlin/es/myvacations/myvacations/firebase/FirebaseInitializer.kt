package es.myvacations.myvacations.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.auth
import es.myvacations.myvacations.BuildConfig

object FirebaseInitializer {

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)

        val appCheck = FirebaseAppCheck.getInstance()

        if (BuildConfig.DEBUG) {
            Log.d("AppCheck", ">>> INSTALANDO DEBUG PROVIDER <<<")

            appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            Log.d("AppCheck", ">>> INSTALANDO PLAY INTEGRITY <<<")

            appCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        if (Firebase.auth.currentUser == null) {
            Firebase.auth.signInAnonymously()
        }

    }
}