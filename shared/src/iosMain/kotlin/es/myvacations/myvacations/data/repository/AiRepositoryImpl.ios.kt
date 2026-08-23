package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.model.ConfidenceResult
import es.myvacations.myvacations.domain.model.ModelMetadata
import es.myvacations.myvacations.domain.repository.AIRepository

actual class AiRepositoryImpl actual constructor() :
    AIRepository {
    actual override fun evaluate(probabilities: FloatArray): ConfidenceResult {
        //Not used
        return ConfidenceResult(false, 0, 0f, 0f, 0f, 0f)
    }

    actual override fun load(): ModelMetadata {
        //Not used
        return ModelMetadata(emptyList(),emptyList(),emptyList())
    }
}