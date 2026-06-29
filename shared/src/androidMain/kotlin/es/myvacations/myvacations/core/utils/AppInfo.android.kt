package es.myvacations.myvacations.core.utils

actual object AppInfo {

    actual val appName: String
        get() = AndroidContextHolder.context.applicationInfo
            .loadLabel(AndroidContextHolder.context.packageManager)
            .toString()

    actual val versionName: String
        get() = AndroidContextHolder.context.packageManager
            .getPackageInfo(AndroidContextHolder.context.packageName, 0)
            .versionName ?: ""
}