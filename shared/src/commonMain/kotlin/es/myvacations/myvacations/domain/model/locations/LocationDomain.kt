package es.myvacations.myvacations.domain.model.locations

data class LocationDomain(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int
)