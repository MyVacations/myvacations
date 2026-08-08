package es.myvacations.myvacations.data.datasource.remote

import es.myvacations.myvacations.domain.model.PredictionResult

interface IntentClassifier {
    suspend fun classify(
        text: String
    ): PredictionResult
}