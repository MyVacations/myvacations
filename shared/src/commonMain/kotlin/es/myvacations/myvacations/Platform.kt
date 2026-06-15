package es.myvacations.myvacations

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform