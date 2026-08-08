package es.myvacations.myvacations.data.datasource.remote

import io.ktor.utils.io.ByteReadChannel

interface ModelLocalDataSource {
    suspend fun saveZip(
        channel: ByteReadChannel,
        totalBytes: Long,
        append: Boolean,
        onProgress: (Long, Long) -> Unit
    )

    suspend fun unzip()
    suspend fun deleteZip(): Boolean
    suspend fun isModelInstalled(): Boolean
    suspend fun installedVersion(): String?
    suspend fun saveInstalledVersion(version: String)
}