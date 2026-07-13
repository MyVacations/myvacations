package es.myvacations.myvacations.domain.repository

interface AppInfoRepository {
    suspend fun isFirstLogin(): Boolean
    suspend fun messageFromServer(): String
    suspend fun markWelcomeShown()
    suspend fun messageSeen(): Boolean
}