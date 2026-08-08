package es.myvacations.myvacations.data.datasource.remote

import io.ktor.utils.io.ByteReadChannel


interface ModelRemoteDataSource {
    suspend fun getLatestRelease(): GithubReleaseDto

    suspend fun openDownload(
        url: String,
        downloadedBytes: Long,
        write: suspend (ByteReadChannel, Long, Boolean) -> Unit
    )
}