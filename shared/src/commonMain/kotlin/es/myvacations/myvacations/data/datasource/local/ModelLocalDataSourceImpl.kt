package es.myvacations.myvacations.data.datasource.local

import es.myvacations.myvacations.data.datasource.remote.ModelLocalDataSource
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher

expect class ModelLocalDataSourceImpl(dispatcher: CoroutineDispatcher) : ModelLocalDataSource {
    override suspend fun saveZip(
        channel: ByteReadChannel, totalBytes: Long, append: Boolean,
        onProgress: (Long, Long) -> Unit
    )

    override suspend fun unzip()
    override suspend fun deleteZip(): Boolean
    override suspend fun isModelInstalled(): Boolean
    override suspend fun installedVersion(): String?
    override suspend fun saveInstalledVersion(version: String)
}

object ModelFiles {
    const val DIRECTORY = "ai"
    const val ZIP = "latest.zip"
    const val MODEL = "model_quantized.onnx"
    const val TOKENIZER = "tokenizer.onnx"
    const val VERSION = "version.json"
    const val METADATA = "metadata.json"
}