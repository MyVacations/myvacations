package es.myvacations.myvacations.data.repository.remote

import es.myvacations.myvacations.data.datasource.remote.GithubReleaseDto
import es.myvacations.myvacations.data.datasource.remote.ModelRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.Json

class ModelRemoteDataSourceImpl(
    private val client: HttpClient
) : ModelRemoteDataSource {
    override suspend fun getLatestRelease(): GithubReleaseDto {
        return client
            .get("https://api.github.com/repos/MyVacations/intent-classifier/releases/latest")
            .body<GithubReleaseDto>()
    }

    override suspend fun openDownload(
        url: String,
        downloadedBytes: Long,
        write: suspend (ByteReadChannel, Long, Boolean) -> Unit
    ) {
        client.prepareGet(url) {
            if (downloadedBytes > 0L) {
                header(HttpHeaders.Range, "bytes=$downloadedBytes-")
            }
        }.execute { response ->
            val resumed =
                response.headers["Content-Range"] != null

            val totalSize =
                if (resumed) {
                    downloadedBytes + (response.contentLength() ?: 0L)
                } else {
                    response.contentLength() ?: 0L
                }

            write(
                response.bodyAsChannel(),
                totalSize, resumed
            )
        }
    }
}