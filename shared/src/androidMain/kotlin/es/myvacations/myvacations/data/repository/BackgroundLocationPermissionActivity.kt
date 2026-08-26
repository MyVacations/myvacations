package es.myvacations.myvacations.data.repository

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import es.myvacations.myvacations.shared.R

class BackgroundLocationPermissionActivity : ComponentActivity() {
    private var openedSettings = false

    override fun onResume() {
        super.onResume()
        if (!openedSettings) return

        if (hasBackgroundLocationPermission()) {
            Log.d("pruebas", "BackgroundLocationPermissionActivity")
            WidgetPlacesScheduler.refreshNow(this)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlertDialog(
                onDismissRequest = {
                    finish()
                },
                title = {
                    Text(resources.getString(R.string.background_location_title))
                },
                text = {
                    Text(
                        resources.getString(R.string.background_location_message)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openLocationSettings()
                        }
                    ) {
                        Text(resources.getString(R.string.background_location_settings))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            finish()
                        }
                    ) {
                        Text(resources.getString(R.string.cancel))
                    }
                }
            )

        }
    }

    private fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun openLocationSettings() {
        openedSettings = true
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        ).apply {
            data = "package:$packageName".toUri()
        }

        startActivity(intent)
    }
}