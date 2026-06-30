package es.myvacations.myvacations.core.firebase

interface CrashReporter {
    fun log(message: String)
    fun recordException(throwable: Throwable)
}