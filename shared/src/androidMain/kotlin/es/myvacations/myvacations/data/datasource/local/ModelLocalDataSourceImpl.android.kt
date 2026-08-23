package es.myvacations.myvacations.data.datasource.local

import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.local.ModelFiles.DIRECTORY
import es.myvacations.myvacations.data.datasource.local.ModelFiles.MODEL
import es.myvacations.myvacations.data.datasource.local.ModelFiles.TOKENIZER
import es.myvacations.myvacations.data.datasource.local.ModelFiles.VERSION
import es.myvacations.myvacations.data.datasource.local.ModelFiles.ZIP
import es.myvacations.myvacations.data.datasource.remote.ModelLocalDataSource
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

actual class ModelLocalDataSourceImpl actual constructor(private val dispatcher: CoroutineDispatcher) :
    ModelLocalDataSource {

    private val aiDirectory = File(AndroidContextHolder.context.filesDir, DIRECTORY)

    private val zipFile = File(aiDirectory, ZIP)

    private val modelFile = File(aiDirectory, MODEL)

    private val tokenizerFile = File(aiDirectory, TOKENIZER)

    private val versionFile = File(aiDirectory, VERSION)

    actual override suspend fun saveZip(
        channel: ByteReadChannel, totalBytes: Long,
        append: Boolean,
        onProgress: (Long, Long) -> Unit
    ) {
        aiDirectory.mkdirs()

        withContext(dispatcher) {

            var downloadedBytes = if (append && zipFile.exists()) {
                zipFile.length()
            } else {
                0L
            }

            FileOutputStream(zipFile, append).use { output ->

                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                while (!channel.isClosedForRead) {

                    val read = channel.readAvailable(buffer)

                    if (read == -1) break

                    output.write(buffer, 0, read)

                    downloadedBytes += read

                    onProgress(downloadedBytes, totalBytes)
                }

                output.flush()
            }
        }
    }

    actual override suspend fun unzip() {
        withContext(dispatcher) {
            ZipInputStream(FileInputStream(zipFile)).use { zip ->

                var entry = zip.nextEntry

                while (entry != null) {

                    val outFile = File(aiDirectory, entry.name)

                    if (entry.isDirectory) {

                        outFile.mkdirs()

                    } else {

                        outFile.parentFile?.mkdirs()

                        FileOutputStream(outFile).use { output ->
                            zip.copyTo(output)
                        }
                    }

                    zip.closeEntry()

                    entry = zip.nextEntry
                }
            }
        }
    }

    actual override suspend fun deleteZip(): Boolean {
        if (!zipFile.exists()) {
            return false
        }

        val deleted = zipFile.delete()
        return deleted
    }

    actual override suspend fun isModelInstalled(): Boolean {
        return modelFile.exists() &&
                tokenizerFile.exists() &&
                versionFile.exists()
    }

    actual override suspend fun installedVersion(): String? {
        if (!versionFile.exists()) {
            return null
        }

        return versionFile.readText().trim()
    }

    actual override suspend fun saveInstalledVersion(version: String) {
        aiDirectory.mkdirs()

        versionFile.writeText(version)
    }
}