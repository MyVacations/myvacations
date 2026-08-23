package es.myvacations.myvacations.domain.usecase.chatbot.latestmodelrelease

import es.myvacations.myvacations.domain.model.ModelState
import es.myvacations.myvacations.domain.repository.ModelRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.io.IOException
import kotlin.time.Duration.Companion.minutes

class EnsureModelInstalledUseCase(
    private val repository: ModelRepository
) {
    val modelState = repository.modelState

    private suspend fun checkPendingInstallation() {
        repository.checkPendingInstallation()
    }

    suspend fun checkModelStatus() = repository.isInstalled()

    suspend fun isUpdateAvailable() = repository.isUpdateAvailable()

    suspend fun refreshState() {
        checkPendingInstallation()

        if (modelState.value !is ModelState.ResumeInstallation) {
            repository.refreshState()
        }
    }

    suspend fun cancelInstall()
    {
        repository.cancelInstallation()
    }

    suspend operator fun invoke() {

        while (true) {

            try {

                if (repository.getInstallStage() != null) {
                    repository.resumeInstallation()
                    return
                }

                if (checkModelStatus() && !isUpdateAvailable()) {
                    return
                }

                val release = repository.getLatestRelease()

                repository.installModel(release)
                return

            } catch (_: IOException) {
                withTimeout(5.minutes) {
                    repository.waitForConnection()
                }

            } catch (_: TimeoutCancellationException) {
                return

            } catch (_: Exception) {
                return
            }
        }
    }
}