package es.myvacations.myvacations.core.firebase

interface AnalyticsReporter {
    fun logEvent(
        name: String,
        params: Map<String, Any?> = emptyMap()
    )
}