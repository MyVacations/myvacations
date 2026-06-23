package es.myvacations.myvacations.core.extensions

fun Double.roundTo2Decimals(): Double =
    kotlin.math.round(this * 100) / 100.0

fun String.transformInInitials(): String = trim()
    .split("\\s+".toRegex())
    .filter { it.isNotBlank() }
    .take(2).joinToString("") { it.first().uppercase() }