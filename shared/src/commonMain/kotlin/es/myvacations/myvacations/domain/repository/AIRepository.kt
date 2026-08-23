package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.ConfidenceResult
import es.myvacations.myvacations.domain.model.ModelMetadata

interface AIRepository {
    fun evaluate(probabilities: FloatArray): ConfidenceResult
    fun load(): ModelMetadata
}