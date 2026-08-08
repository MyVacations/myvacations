package es.myvacations.myvacations.data.repository

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.remote.IntentClassifier
import es.myvacations.myvacations.domain.model.LabelPrediction
import es.myvacations.myvacations.domain.model.ModelMetadata
import es.myvacations.myvacations.domain.model.Prediction
import es.myvacations.myvacations.domain.model.PredictionResult
import es.myvacations.myvacations.domain.model.RestaurantTypePrediction
import es.myvacations.myvacations.domain.model.SubcategoryPrediction
import es.myvacations.myvacations.domain.repository.AIRepository
import io.github.aakira.napier.Napier
import java.io.File
import java.nio.LongBuffer

class AndroidIntentClassifier(private val aiRepository: AIRepository) : IntentClassifier {
    private val metadata by lazy {
        aiRepository.load()
    }
    private val environment by lazy {
        OrtEnvironment.getEnvironment()
    }
    private val modelPath = File(
        AndroidContextHolder.context.filesDir,
        "ai/model_quantized.onnx"
    ).absolutePath

    private val tokenizerPath = File(
        AndroidContextHolder.context.filesDir,
        "ai/tokenizer.onnx"
    ).absolutePath

    private val session by lazy {
        environment.createSession(modelPath)
    }

    private val tokenizerSession by lazy {
        val options = OrtSession.SessionOptions()

        options.registerCustomOpLibrary(
            OrtxPackage.getLibraryPath()
        )

        environment.createSession(
            tokenizerPath,
            options
        )
    }

    override suspend fun classify(text: String): PredictionResult {

        val textTensor = OnnxTensor.createTensor(
            environment,
            arrayOf(text)
        )

        val tokenizerResult = tokenizerSession.run(
            mapOf(
                "text" to textTensor
            )
        )

        val inputIdsTensor =
            tokenizerResult["input_ids"]!!.get() as OnnxTensor

        val attentionMaskTensor =
            tokenizerResult["attention_mask"]!!.get() as OnnxTensor

        val inputIds = inputIdsTensor.value as LongArray
        val attentionMask = attentionMaskTensor.value as LongArray

        val shape = longArrayOf(1, inputIds.size.toLong())

        val modelInputIds = OnnxTensor.createTensor(
            environment,
            LongBuffer.wrap(inputIds),
            shape
        )

        val modelAttentionMask = OnnxTensor.createTensor(
            environment,
            LongBuffer.wrap(attentionMask),
            shape
        )

        val modelResult = session.run(
            mapOf(
                "input_ids" to modelInputIds,
                "attention_mask" to modelAttentionMask
            )
        )

        val labelLogits =
            ((modelResult["label_logits"]!!.get() as OnnxTensor).value as Array<FloatArray>)[0]

        val subcategoryLogits =
            ((modelResult["subcategory_logits"]!!.get() as OnnxTensor).value as Array<FloatArray>)[0]

        val restaurantTypeLogits =
            ((modelResult["restaurant_type_logits"]!!.get() as OnnxTensor).value as Array<FloatArray>)[0]

        val labelProbabilities = softmax(labelLogits)
        val subcategoryProbabilities = softmax(subcategoryLogits)
        val restaurantTypeProbabilities = softmax(restaurantTypeLogits)

        val confidence = aiRepository.evaluate(
            labelProbabilities
        )

        val subcategoryIndex =
            subcategoryProbabilities.indices.maxBy { subcategoryProbabilities[it] }

        val restaurantTypeIndex =
            restaurantTypeProbabilities.indices.maxBy { restaurantTypeProbabilities[it] }

        val labelPrediction = LabelPrediction(
            value = if (confidence.accepted)
                metadata.idToLabel[confidence.bestIndex]
            else
                "NONE",
            confidence = confidence.top1,
            accepted = confidence.accepted,
            top1 = confidence.top1,
            top2 = confidence.top2,
            gap = confidence.gap,
            requiredGap = confidence.requiredGap
        )

        val subcategoryPrediction = SubcategoryPrediction(
            value = metadata.idToSubcategory[subcategoryIndex],
            confidence = subcategoryProbabilities[subcategoryIndex]
        )

        val restaurantTypePrediction = RestaurantTypePrediction(
            value = metadata.idToRestaurantType[restaurantTypeIndex],
            confidence = restaurantTypeProbabilities[restaurantTypeIndex]
        )

        val prediction = Prediction(
            label = labelPrediction,
            subcategory = subcategoryPrediction,
            restaurantType = restaurantTypePrediction
        )

        return PredictionResult(
            prediction = prediction
        )
    }
}

private fun softmax(logits: FloatArray): FloatArray {
    val max = logits.max()

    val exp = FloatArray(logits.size)
    var sum = 0.0

    logits.forEachIndexed { index, value ->
        exp[index] = kotlin.math.exp((value - max).toDouble()).toFloat()
        sum += exp[index]
    }

    return FloatArray(logits.size) { index ->
        exp[index] / sum.toFloat()
    }
}