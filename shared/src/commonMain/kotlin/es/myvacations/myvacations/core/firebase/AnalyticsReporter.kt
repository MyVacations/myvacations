package es.myvacations.myvacations.core.firebase

interface AnalyticsReporter {
    fun logEvent(
        event: AnalyticsEvent,
        params: Map<String, Any?> = emptyMap()
    )
}