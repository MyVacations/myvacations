package es.myvacations.myvacations.domain.usecase.chatbot

import es.myvacations.myvacations.data.datasource.remote.IntentClassifier
import es.myvacations.myvacations.domain.model.PredictionResult

class ClassifyIntentUseCase(
    private val intentClassifier: IntentClassifier
) {

    suspend operator fun invoke(
        text: String
    ): PredictionResult {
        return intentClassifier.classify(text)
    }
}