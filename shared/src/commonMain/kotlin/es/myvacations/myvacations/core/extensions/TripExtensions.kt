package es.myvacations.myvacations.core.extensions

fun Double.roundTo2Decimals(): Double =
    kotlin.math.round(this * 100) / 100.0

fun Double.roundTo1Decimals(): Double =
    kotlin.math.round(this * 10) / 10.0

fun String.transformInInitials(): String = trim()
    .split("\\s+".toRegex())
    .filter(String::isNotBlank)
    .take(2)
    .map { it.first().uppercaseChar() }
    .joinToString("")