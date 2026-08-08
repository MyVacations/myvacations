package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.domain.model.ConfidenceResult
import es.myvacations.myvacations.domain.model.ModelMetadata
import es.myvacations.myvacations.domain.repository.AIRepository
import org.json.JSONObject
import java.io.File

actual class AiRepositoryImpl actual constructor() :
    AIRepository {
    actual override fun evaluate(probabilities: FloatArray): ConfidenceResult {
        val sortedIndexes = probabilities
            .indices
            .sortedByDescending { probabilities[it] }

        val bestIndex = sortedIndexes[0]

        val top1 = probabilities[sortedIndexes[0]]
        val top2 = probabilities[sortedIndexes[1]]

        val gap = top1 - top2

        val requiredGap = when {
            top1 >= 0.95f -> 0.02f
            top1 >= 0.90f -> 0.04f
            top1 >= 0.80f -> 0.06f
            else -> 0.10f
        }

        return ConfidenceResult(
            accepted = gap >= requiredGap,
            bestIndex = bestIndex,
            top1 = top1,
            top2 = top2,
            gap = gap,
            requiredGap = requiredGap
        )
    }

    actual override fun load(): ModelMetadata {
        val json = File(
            AndroidContextHolder.context.filesDir,
            "ai/metadata.json"
        ).bufferedReader().use { it.readText() }

        val root = JSONObject(json)

        fun readArray(name: String): List<String> {
            val obj = root.getJSONObject(name)

            return (0 until obj.length())
                .map { index ->
                    obj.getString(index.toString())
                }
        }

        return ModelMetadata(
            idToLabel = readArray("id2label"),
            idToSubcategory = readArray("id2subcategory"),
            idToRestaurantType = readArray("id2restaurant_type")
        )
    }
}