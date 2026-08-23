package es.myvacations.myvacations.data.datasource.local

import es.myvacations.myvacations.data.datasource.remote.ModelLocalDataSource
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher

actual class ModelLocalDataSourceImpl actual constructor(private val dispatcher: CoroutineDispatcher) :
    ModelLocalDataSource {
    actual override suspend fun saveZip(
        channel: ByteReadChannel, totalBytes: Long, append: Boolean,
        onProgress: (Long, Long) -> Unit
    ) {
        //Not used
    }

    actual override suspend fun unzip() {
        //Not used
    }

    actual override suspend fun deleteZip(): Boolean {
        //Not used
        return false
    }

    actual override suspend fun isModelInstalled(): Boolean {
        //Not used
        return false
    }

    actual override suspend fun installedVersion(): String? {
        //Not used
        return ""
    }

    actual override suspend fun saveInstalledVersion(version: String) {
        //Not used
    }
}