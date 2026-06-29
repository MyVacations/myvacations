package es.myvacations.myvacations.core.extensions

fun String.shortenTitle(maxLength: Int = 8) =
    if (this.length > maxLength) this.take(maxLength - 2) + "..." else this