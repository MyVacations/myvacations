package es.myvacations.myvacations.presentation.utils

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private fun degreesToRadians(degrees: Double): Double =
    degrees * PI / 180.0

fun distanceInMeters(
    userLatitude: Double,
    userLongitude: Double,
    latitude: Double,
    longitude: Double
): Double {

    val earthRadius = 6_371_000.0

    val lat1 = degreesToRadians(userLatitude)
    val lat2 = degreesToRadians(latitude)

    val deltaLat = degreesToRadians(latitude - userLatitude)
    val deltaLon = degreesToRadians(longitude - userLongitude)

    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
            cos(lat1) * cos(lat2) *
            sin(deltaLon / 2) * sin(deltaLon / 2)

    val c = 2 * atan2(
        sqrt(a),
        sqrt(1 - a)
    )

    return earthRadius * c
}