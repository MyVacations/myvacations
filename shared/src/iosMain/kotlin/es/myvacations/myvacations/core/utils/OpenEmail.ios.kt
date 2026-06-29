package es.myvacations.myvacations.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openEmail(email: String, subject: String?) {
    val url = if (subject.isNullOrBlank()) {
        "mailto:$email"
    } else {
        "mailto:$email?subject=$subject"
    }

    NSURL.URLWithString(url)?.let { nsUrl ->
        UIApplication.sharedApplication.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null
        )
    }
}
