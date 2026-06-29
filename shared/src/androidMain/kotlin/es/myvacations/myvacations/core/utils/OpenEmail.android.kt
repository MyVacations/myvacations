package es.myvacations.myvacations.core.utils

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

actual fun openEmail(email: String, subject: String?) {
    val uri = "mailto:$email?subject=${Uri.encode(subject.orEmpty())}".toUri()
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    AndroidContextHolder.context.startActivity(intent)
}