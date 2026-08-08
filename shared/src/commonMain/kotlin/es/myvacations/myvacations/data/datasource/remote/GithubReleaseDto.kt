package es.myvacations.myvacations.data.datasource.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GithubAssetDto(

    val name: String,

    @SerialName("browser_download_url")
    val browserDownloadUrl: String,

    val size: Long

)

@Serializable
data class GithubReleaseDto(

    @SerialName("tag_name")
    val tagName: String,

    val assets: List<GithubAssetDto>

)