package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.ModelInstallStage
import es.myvacations.myvacations.domain.model.ModelRelease
import es.myvacations.myvacations.domain.model.ModelState
import kotlinx.coroutines.flow.StateFlow

interface ModelRepository {
    val modelState: StateFlow<ModelState>
    suspend fun refreshState()
    suspend fun isInstalled(): Boolean
    suspend fun getLatestRelease(): ModelRelease
    suspend fun isUpdateAvailable(): Boolean
    suspend fun installModel(
        release: ModelRelease
    )
    suspend fun cancelInstallation()
    suspend fun waitForConnection()
    suspend fun checkPendingInstallation()
    suspend fun resumeInstallation()
    suspend fun getInstallStage(): ModelInstallStage?
    suspend fun saveInstallStage(
        stage: ModelInstallStage?
    )

}