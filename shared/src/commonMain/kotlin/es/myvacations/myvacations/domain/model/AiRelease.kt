package es.myvacations.myvacations.domain.model

sealed interface ModelState {

    data object Idle : ModelState

    data object NotInstalled : ModelState

    data class NeedUpdate(val versionAvailable: String) : ModelState

    data class Installing(
        val stage: Stage,
        val progress: Float,
        val totalForDownload: Double? = null,
        val downloaded: Double? = null
    ) : ModelState

    data object Ready : ModelState

    data class WaitingConnection(val progress: Float = 0f, val totalForDownload: Double? = null,
                                 val downloaded: Double? = null) : ModelState

    data class ResumeInstallation(
        val stage: ModelInstallStage,
        val progress: Float,
        val totalForDownload: Double? = null,
        val downloaded: Double? = null
    ) : ModelState

    data object ConnectionTimeout : ModelState

    data class Error(
        val message: String
    ) : ModelState
}

data class ModelRelease(
    val version: String,
    val downloadUrl: String,
    val size: Long
)

data class ModelMetadata(
    val idToLabel: List<String>,
    val idToSubcategory: List<String>,
    val idToRestaurantType: List<String>,
)

data class Prediction(
    val label: LabelPrediction,
    val subcategory: SubcategoryPrediction,
    val restaurantType: RestaurantTypePrediction,
)

data class LabelPrediction(
    val value: String,
    val confidence: Float,
    val accepted: Boolean,
    val top1: Float,
    val top2: Float,
    val gap: Float,
    val requiredGap: Float,
)

data class SubcategoryPrediction(
    val value: String,
    val confidence: Float,
)

data class RestaurantTypePrediction(
    val value: String,
    val confidence: Float,
)

data class PredictionResult(
    val prediction: Prediction
)

data class ChatMessageDomain(
    val text: String,
    val fromBot: Boolean
)

data class ConfidenceResult(
    val accepted: Boolean,
    val bestIndex: Int,
    val top1: Float,
    val top2: Float,
    val gap: Float,
    val requiredGap: Float
)

enum class ModelInstallStage {
    DOWNLOADING,
    EXTRACTING,
    VERIFYING
}

enum class Stage {
    DOWNLOADING,
    EXTRACTING,
    VERIFYING
}
