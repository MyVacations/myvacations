package es.myvacations.myvacations.data.mapper

import es.myvacations.myvacations.data.datasource.remote.GithubReleaseDto
import es.myvacations.myvacations.domain.model.ModelRelease

fun GithubReleaseDto.toDomain(): ModelRelease {

    val asset = assets.first {
        it.name == "latest.zip"
    }

    return ModelRelease(
        version = tagName,
        downloadUrl = asset.browserDownloadUrl,
        size = asset.size
    )
}