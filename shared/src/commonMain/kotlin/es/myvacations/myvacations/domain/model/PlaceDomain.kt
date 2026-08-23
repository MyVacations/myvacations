package es.myvacations.myvacations.domain.model

data class PlaceDomain(
    val id: String,
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val distance: Double?,
    val phone: String?,
    val website: String?,
    val address: String?,
    val cuisine: String?,
    val type: String?
)