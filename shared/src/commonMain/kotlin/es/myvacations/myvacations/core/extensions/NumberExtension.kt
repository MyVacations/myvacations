package es.myvacations.myvacations.core.extensions

import kotlin.math.roundToInt

fun Double.shortCurrency(): String {
    return when {
        this >= 1_000_000 -> {
            val value = (this / 100_000).roundToInt() / 10.0
            "$value"+"M".replace(".0M", "M")
        }

        this >= 1_000 -> {
            val value = (this / 100).roundToInt() / 10.0
            "$value"+"K".replace(".0K", "K")
        }

        else -> toInt().toString()
    }
}