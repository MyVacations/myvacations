package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.AIRepository
import es.myvacations.myvacations.domain.model.ConfidenceResult
import es.myvacations.myvacations.domain.model.ModelMetadata

expect class AiRepositoryImpl() : AIRepository {
    override fun evaluate(probabilities: FloatArray): ConfidenceResult
    override fun load(): ModelMetadata
}