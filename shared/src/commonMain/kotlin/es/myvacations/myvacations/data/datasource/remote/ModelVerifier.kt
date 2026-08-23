package es.myvacations.myvacations.data.datasource.remote

interface ModelVerifier {
    suspend fun verify()
}