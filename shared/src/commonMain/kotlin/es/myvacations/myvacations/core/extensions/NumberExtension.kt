package es.myvacations.myvacations.core.extensions

import kotlin.math.roundToInt

fun Double.shortCurrencyWhen1000(): String {
    return when {
        this >= 1_000_000 -> {
            val millions = (this / 100_000).toInt() / 10.0
            "${millions}M".replace(".0M", "M")
        }

        this >= 100_000 -> {
            val thousands = (this / 1000).toInt()
            "${thousands}K"
        }
        this >= 1_000-> {
            val thousand = this / 1000
            if (thousand < 10) {
                "${(thousand * 10).roundToInt() / 10.0}K"
                    .replace(".0K", "K")
            } else {
                "${thousand.roundToInt()}K"
            }
        }
        else ->{
            this.toString()
        }
    }
}

fun Double.shortCurrencyWhen100000(): String {
    return when {
        this >= 1_000_000 -> {
            val millions = (this / 100_000).toInt() / 10.0
            "${millions}M".replace(".0M", "M")
        }

        this >= 100_000 -> {
            val thousands = (this / 1000).toInt()
            "${thousands}K"
        }

        else -> {
            toInt().toString()
        }
    }
}