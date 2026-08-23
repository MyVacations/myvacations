package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.model.ModelInstallStage
import es.myvacations.myvacations.domain.model.ModelRelease
import es.myvacations.myvacations.domain.model.ModelState
import es.myvacations.myvacations.domain.repository.ModelRepository
import kotlinx.coroutines.flow.StateFlow

expect class ModelServiceRepositoryImpl : ModelRepository {
    override val modelState: StateFlow<ModelState>
    override suspend fun refreshState()
    override suspend fun isInstalled(): Boolean
    override suspend fun getLatestRelease(): ModelRelease
    override suspend fun isUpdateAvailable(): Boolean
    override suspend fun installModel(release: ModelRelease)
    override suspend fun cancelInstallation()
    override suspend fun waitForConnection()
    override suspend fun checkPendingInstallation()
    override suspend fun resumeInstallation()
    override suspend fun getInstallStage(): ModelInstallStage?
    override suspend fun saveInstallStage(stage: ModelInstallStage?)
}
