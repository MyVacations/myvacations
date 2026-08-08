package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.model.ModelInstallStage
import es.myvacations.myvacations.domain.model.ModelRelease
import es.myvacations.myvacations.domain.model.ModelState
import es.myvacations.myvacations.domain.repository.ModelRepository
import kotlinx.coroutines.flow.StateFlow

actual class ModelServiceRepositoryImpl :
    ModelRepository {
    actual override val modelState: StateFlow<ModelState>
        get() = TODO("Not yet implemented")

    actual override suspend fun refreshState() {
        TODO("Not yet implemented")
    }

    actual override suspend fun isInstalled(): Boolean {
        TODO("Not yet implemented")
    }

    actual override suspend fun getLatestRelease(): ModelRelease {
        TODO("Not yet implemented")
    }

    actual override suspend fun isUpdateAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    actual override suspend fun installModel(release: ModelRelease) {
    }

    actual override suspend fun cancelInstallation() {
        TODO("Not yet implemented")
    }

    actual override suspend fun waitForConnection() {
    }

    actual override suspend fun checkPendingInstallation() {
        TODO("Not yet implemented")
    }

    actual override suspend fun resumeInstallation() {
        TODO("Not yet implemented")
    }

    actual override suspend fun getInstallStage(): ModelInstallStage? {
        TODO("Not yet implemented")
    }

    actual override suspend fun saveInstallStage(stage: ModelInstallStage?) {
        TODO("Not yet implemented")
    }
}