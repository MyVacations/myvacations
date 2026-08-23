package es.myvacations.myvacations.data.datasource.remote

import kotlinx.serialization.Serializable

@Serializable
data class OsmResponse(
    val elements: List<OsmElement>
)

@Serializable
data class OsmElement(
    val type: String,
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OsmCenter? = null,
    val tags: Map<String, String>? = null
)

@Serializable
data class OsmCenter(
    val lat: Double,
    val lon: Double
)