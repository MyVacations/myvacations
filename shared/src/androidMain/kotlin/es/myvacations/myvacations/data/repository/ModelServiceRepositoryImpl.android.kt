package es.myvacations.myvacations.data.repository

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.annotation.RequiresPermission
import es.myvacations.myvacations.core.extensions.roundTo1Decimals
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.local.ModelFiles
import es.myvacations.myvacations.data.datasource.local.ModelFiles.DIRECTORY
import es.myvacations.myvacations.data.datasource.local.ModelFiles.ZIP
import es.myvacations.myvacations.data.datasource.remote.ModelLocalDataSource
import es.myvacations.myvacations.data.datasource.remote.ModelRemoteDataSource
import es.myvacations.myvacations.data.datasource.remote.ModelVerifier
import es.myvacations.myvacations.data.mapper.toDomain
import es.myvacations.myvacations.domain.model.ModelInstallStage
import es.myvacations.myvacations.domain.model.ModelRelease
import es.myvacations.myvacations.domain.model.ModelState
import es.myvacations.myvacations.domain.model.Stage
import es.myvacations.myvacations.domain.repository.ModelRepository
import es.myvacations.myvacations.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.io.IOException
import java.io.File

actual class ModelServiceRepositoryImpl(
    private val remoteDataSource: ModelRemoteDataSource,
    private val localDataSource: ModelLocalDataSource,
    private val verifier: ModelVerifier,
    private val settingsRepository: SettingsRepository
) :
    ModelRepository {
    private val aiDirectory = File(AndroidContextHolder.context.filesDir, DIRECTORY)
    private val zipFile = File(aiDirectory, ZIP)
    private val _modelState = MutableStateFlow<ModelState>(ModelState.Idle)
    private var recordDownload = 0f
    private var totalForDownload: Double? = 0.0
    private var totalDownloaded: Double? = 0.0

    actual override val modelState: StateFlow<ModelState> = _modelState

    actual override suspend fun refreshState() {

        if (getInstallStage() != null) {
            checkPendingInstallation()
            return
        }

        val installed = isInstalled()

        if (!installed) {
            _modelState.value = ModelState.NotInstalled
            return
        }

        val release = getLatestRelease()

        if (localDataSource.installedVersion() != release.version) {
            _modelState.value = ModelState.NeedUpdate(release.version)
        } else {
            _modelState.value = ModelState.Ready
        }
    }

    actual override suspend fun isInstalled(): Boolean {
        return localDataSource.isModelInstalled()
    }

    actual override suspend fun getLatestRelease(): ModelRelease {
        return remoteDataSource.getLatestRelease().toDomain()
    }

    actual override suspend fun isUpdateAvailable(): Boolean {
        val localVersion = localDataSource.installedVersion()
        val remoteVersion = getLatestRelease().version
        return localVersion != remoteVersion
    }

    fun getZipSize(): Long {
        val zipFile = zipFile

        return if (zipFile.exists()) {
            zipFile.length()
        } else {
            0L
        }
    }

    actual override suspend fun installModel(release: ModelRelease) {
        try {
            val downloadedBytes = getZipSize()
            saveInstallStage(ModelInstallStage.DOWNLOADING)
            remoteDataSource.openDownload(
                release.downloadUrl,
                downloadedBytes
            ) { channel, totalBytes, append ->

                if (!append && downloadedBytes > 0) {
                    localDataSource.deleteZip()
                }
                localDataSource.saveZip(
                    channel, totalBytes,
                    append
                ) { downloaded, total ->
                    val progress = downloaded.toFloat() / total
                    val visualProgress = progress * 0.9f

                    recordDownload = visualProgress

                    totalForDownload = (totalBytes / 1_000_000.0).roundTo1Decimals()
                    totalDownloaded = (downloaded / 1_000_000.0).roundTo1Decimals()

                    _modelState.value = ModelState.Installing(
                        stage = Stage.DOWNLOADING,
                        progress = visualProgress,
                        totalForDownload = totalForDownload,
                        downloaded = totalDownloaded
                    )
                }
            }
            totalForDownload = null
            totalDownloaded = null
            recordDownload = 0.9f
            saveInstallStage(ModelInstallStage.EXTRACTING)
            _modelState.value = ModelState.Installing(
                Stage.EXTRACTING,
                0.9f
            )
            localDataSource.unzip()
            recordDownload = 0.95f
            _modelState.value = ModelState.Installing(
                Stage.VERIFYING,
                0.95f
            )
            saveInstallStage(ModelInstallStage.VERIFYING)

            verifyModel()
            completeInstallation(release.version)
        } catch (e: IOException) {
            _modelState.value =
                ModelState.WaitingConnection(recordDownload, totalForDownload, totalDownloaded)
            throw e
        } catch (e: Exception) {
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
            saveInstallStage(null)
            localDataSource.deleteZip()
            throw e
        }
    }

    actual override suspend fun cancelInstallation() {
        totalForDownload = null
        totalDownloaded = null
        recordDownload = 0f
        _modelState.value = ModelState.NotInstalled
        saveInstallStage(null)
        localDataSource.deleteZip()
    }

    actual override suspend fun getInstallStage(): ModelInstallStage? {
        return settingsRepository
            .getSettings()
            .first()
            ?.modelStage
    }

    actual override suspend fun saveInstallStage(stage: ModelInstallStage?) {
        settingsRepository.updateDownloadStage(stage?.name)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    actual override suspend fun waitForConnection() {
        suspendCancellableCoroutine { continuation ->

            val connectivityManager =
                AndroidContextHolder.context.getSystemService(
                    Context.CONNECTIVITY_SERVICE
                ) as ConnectivityManager

            val callback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    connectivityManager.unregisterNetworkCallback(this)

                    if (continuation.isActive) {
                        continuation.resume(Unit) { _, _, _ -> }
                    }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            continuation.invokeOnCancellation {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }

    actual override suspend fun checkPendingInstallation() {
        val stage = getInstallStage() ?: return

        val progress = when (stage) {

            ModelInstallStage.DOWNLOADING -> {
                val total = getLatestRelease().size
                totalForDownload = (total / 1_000_000.0).roundTo1Decimals()
                if (total > 0) {
                    totalDownloaded = (getZipSize() / 1_000_000.0).roundTo1Decimals()
                    (getZipSize().toFloat() / total) * 0.9f
                } else {
                    0f
                }
            }

            ModelInstallStage.EXTRACTING -> {
                totalDownloaded = null
                totalForDownload = null
                0.9f
            }

            ModelInstallStage.VERIFYING -> {
                totalDownloaded = null
                totalForDownload = null
                0.95f
            }
        }

        _modelState.value =
            ModelState.ResumeInstallation(
                progress = progress.coerceIn(0f, 0.95f),
                stage = stage,
                totalForDownload = totalForDownload,
                downloaded = totalDownloaded
            )
    }

    actual override suspend fun resumeInstallation() {
        try {
            when (getInstallStage()) {

                ModelInstallStage.DOWNLOADING -> {
                    val release = getLatestRelease()
                    installModel(release)
                }

                ModelInstallStage.EXTRACTING -> {
                    _modelState.value = ModelState.Installing(
                        Stage.EXTRACTING,
                        0.9f,
                    )

                    localDataSource.unzip()
                    _modelState.value = ModelState.Installing(
                        Stage.VERIFYING,
                        0.95f,
                    )

                    verifyModel()

                    completeInstallation(getLatestRelease().version)
                }

                ModelInstallStage.VERIFYING -> {
                    _modelState.value = ModelState.Installing(
                        Stage.VERIFYING,
                        0.95f,
                    )

                    verifyModel()

                    completeInstallation(getLatestRelease().version)
                }

                null -> Unit
            }
        } catch (e: IOException) {
            _modelState.value =
                ModelState.WaitingConnection(recordDownload, totalForDownload, totalDownloaded)
            throw e
        } catch (e: Exception) {
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
            saveInstallStage(null)
            localDataSource.deleteZip()
            throw e
        }
    }

    private suspend fun verifyModel() {
        verifier.verify()
    }

    private suspend fun completeInstallation(version: String) {
        localDataSource.saveInstalledVersion(version)
        saveInstallStage(null)
        localDataSource.deleteZip()
        _modelState.value = ModelState.Ready
        recordDownload = 1f
    }
}